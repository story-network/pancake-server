/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import io.heartpattern.mcremapper.MCRemapper;
import io.heartpattern.mcremapper.model.LocalVariableFixType;
import io.heartpattern.mcremapper.model.Mapping;
import io.heartpattern.mcremapper.parser.proguard.MappingProguardParser;
import io.heartpattern.mcremapper.preprocess.InheritabilityPreprocessor;
import io.heartpattern.mcremapper.preprocess.SuperTypeResolver;
import sh.pancake.classloader.ModdedClassLoader;
import sh.pancake.common.object.VersionInfo;
import sh.pancake.common.object.VersionManifest;
import sh.pancake.common.storage.ObjectStorage;
import sh.pancake.common.util.DownloadUtil;
import sh.pancake.common.util.Hash;
import sh.pancake.common.util.Hex;
import sh.pancake.common.util.MCLauncherUtil;
import sh.pancake.mod.MixinClassModder;
import sh.pancake.server.IPancakeServer;

public class PancakeLauncher {

    // STATIC ZONE START

    private static final Logger LOGGER = LogManager.getLogger("PancakeLauncher");

    private static PancakeLauncher launcher;

    public static PancakeLauncher createLauncher(String[] args) {
        if (launcher != null)
            throw new RuntimeException("Server instance is already made");

        return launcher = new PancakeLauncher(args);
    }

    public static PancakeLauncher getLauncher() {
        return launcher;
    }

    // STATIC ZONE END

    private String[] args;

    private ObjectStorage serverObjectStorage;

    private MixinClassModder mixinModder;

    private ClassLoader launcherClassLoader;
    private ModdedClassLoader serverClassLoader;

    private URL launchPath;

    private boolean launched;

    private IPancakeServer server;

    private PancakeLauncher(String[] args) {
        this.args = args;

        this.serverObjectStorage = new ObjectStorage(new File("objects"));

        this.mixinModder = new MixinClassModder();

        this.launcherClassLoader = PancakeLauncher.class.getClassLoader();
        this.serverClassLoader = new ModdedClassLoader(launcherClassLoader, mixinModder);

        this.launchPath = PancakeLauncher.class.getProtectionDomain().getCodeSource().getLocation();

        this.server = null;
        this.launched = false;
    }

    public String[] getArgs() {
        return args;
    }

    public ObjectStorage getServerObjectStorage() {
        return serverObjectStorage;
    }

    public boolean isStarted() {
        return launched;
    }

    public IPancakeServer getServer() {
        return server;
    }

    public URL getLaunchPath() {
        return launchPath;
    }

    public MixinClassModder getMixinModder() {
        return mixinModder;
    }

    public ClassLoader getLauncherClassLoader() {
        return launcherClassLoader;
    }

    public ModdedClassLoader getServerClassLoader() {
        return serverClassLoader;
    }

    protected VersionInfo provideVersionInfo(String version, boolean recache) {
        String name = version + "-info.json";

        if (!recache) {
            try {
                return MCLauncherUtil.getInfoFromJson(new String(serverObjectStorage.read(name)));
            } catch (IOException e) {

            }
        }

        LOGGER.info("Version info for " + version + " does not exist. Try downloading...");

        VersionManifest manifest = MCLauncherUtil.fetchVersionManifest();

        if (manifest == null) {
            LOGGER.info("Cannot fetch Version manifest");
            return null;
        }

        String rawVersionInfo = MCLauncherUtil.fetchRawVersionInfo(manifest, version);

        try {
            serverObjectStorage.write(name, rawVersionInfo.getBytes("utf-8"));
        } catch (IOException e) {
            LOGGER.warn("Cannot write version info. Skipping...");
        }

        return MCLauncherUtil.getInfoFromJson(rawVersionInfo);
    }

    protected File provideRawMinecraftServer(String version, boolean recache) {
        VersionInfo info = provideVersionInfo(version, recache);

        if (info == null) {
            LOGGER.error("Version info data is invalid. Cannot proceed more");
            return null;
        }

        String name = version + "-server-raw.jar";

        File ref = serverObjectStorage.getReference(name);
        byte[] rawServerData = null;
        try {
            rawServerData = serverObjectStorage.read(name);
        } catch (IOException e) {

        }

        if (rawServerData == null
                || !info.downloads.server.sha1.equalsIgnoreCase(Hex.byteArrayToHex(Hash.sha1From(rawServerData)))) {
            LOGGER.info("Server for " + version + " does not exist or corrupted. Redownloading...");

            try {
                rawServerData = DownloadUtil.fetchData(info.downloads.server.url);
                serverObjectStorage.write(name, rawServerData);
            } catch (IOException e) {
                LOGGER.error("Cannot download server for " + version + ". Cannot proceed more");
                return null;
            }
        }

        return ref;
    }

    protected String provideServerMapping(String version, boolean recache) {
        VersionInfo info = provideVersionInfo(version, recache);

        if (info == null) {
            LOGGER.error("Version info data is invalid. Cannot proceed more");
            return null;
        }

        String name = version + "-server-mapping.txt";

        byte[] rawServerMapping = null;
        try {
            rawServerMapping = serverObjectStorage.read(name);
        } catch (IOException e) {

        }

        if (rawServerMapping == null
                || !info.downloads.serverMappings.sha1.equalsIgnoreCase(Hex.byteArrayToHex(Hash.sha1From(rawServerMapping)))) {
            LOGGER.info("Server mapping for " + version + " does not exist or corrupted. Redownloading...");

            try {
                rawServerMapping = DownloadUtil.fetchData(info.downloads.serverMappings.url);
                serverObjectStorage.write(name, rawServerMapping);
            } catch (IOException e) {
                LOGGER.error("Cannot download server mapping for " + version + ". Cannot proceed more");
                return null;
            }
        }

        return new String(rawServerMapping);
    }

    protected File provideMinecraftServer(String version, boolean recache) {
        String name = version + "-server-mapped.jar";

        File ref = serverObjectStorage.getReference(name);

        if (!serverObjectStorage.exists(name) || recache) {
            LOGGER.info("Patching server...");

            File rawMCServer = provideRawMinecraftServer(version, recache);
            String mappingStr = provideServerMapping(version, recache);

            Mapping mapping = MappingProguardParser.INSTANCE.parse(mappingStr).reversed();
            mapping = InheritabilityPreprocessor.INSTANCE.preprocess(mapping, rawMCServer);

            MCRemapper remapper = new MCRemapper(mapping, SuperTypeResolver.Companion.fromFile(rawMCServer),
                    LocalVariableFixType.DELETE);

            // Make parent dir before start mapping
            File parent = ref.getParentFile();
            if (!parent.exists()) parent.mkdirs();

            remapper.applyMapping(rawMCServer, ref, Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
        }

        return ref;
    }

    public void launch() {
        if (launched) {
            LOGGER.error("Server already started!!");
            return;
        }

        LOGGER.info("Starting on " + launchPath);

        ServiceLoader<IPancakeServer> serverLoader = ServiceLoader.load(IPancakeServer.class, launcherClassLoader);

        Optional<IPancakeServer> serverOptional = serverLoader.findFirst();

        if (serverOptional.isPresent()) {
            server = serverOptional.get();
        } else {
            LOGGER.error("Cannot find IPancakeServer service. Server cannot start");
            return;
        }

        this.server = serverOptional.get();

        File minecraftServerJar = provideMinecraftServer(server.getVersion(), false);

        // add mc server
        try {
            serverClassLoader.addURL(minecraftServerJar.toURI().toURL());
        } catch (MalformedURLException e) {
            LOGGER.error("Cannot find IPancakeServer Path. Server cannot start");
            return;
        }

        LOGGER.info("Starting server...");

        Thread.currentThread().setContextClassLoader(serverClassLoader);

        // Init mixin
        MixinBootstrap.init();

        serverClassLoader.getModder().initModder();

        this.launched = true;
        server.start(args, serverClassLoader, serverObjectStorage, this::finishMixinInit);
    }

    private void finishMixinInit() {
        try {
			Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            m.invoke(null, MixinEnvironment.Phase.INIT);
			m.invoke(null, MixinEnvironment.Phase.DEFAULT);
		} catch (Exception e) {
			throw new RuntimeException(e);
        }
    }

}

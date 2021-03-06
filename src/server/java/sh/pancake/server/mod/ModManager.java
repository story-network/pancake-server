/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import sh.pancake.common.storage.DiskIOStorage;
import sh.pancake.server.Constants;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.ServerStartStatus;

public class ModManager {

    private static final Logger LOGGER = LogManager.getLogger("ModManager");

    private PancakeServer server;
    
    private DiskIOStorage modStorage;

    private Map<String, ModData> modMap;

    public ModManager(PancakeServer server, String modFolderName) {
        this.server = server;
        this.modStorage = new DiskIOStorage(modFolderName);

        this.modMap = new ConcurrentHashMap<>();
    }

    public PancakeServer getServer() {
        return server;
    }

    public DiskIOStorage getModStorage() {
        return modStorage;
    }

    public boolean hasModId(String modId) {
        return modMap.containsKey(modId);
    }

    public ModData getModData(String modId) {
        return modMap.get(modId);
    }

    public void forEach(Consumer<ModData> func) {
        modMap.values().forEach(func);
    }

	public void loadMod(File modFile) throws Exception {
        ModInfo info = null;

        try (JarFile pluginJar = new JarFile(modFile.getAbsolutePath())) {
            ZipEntry configEntry = pluginJar.getEntry(Constants.MOD_CONFIG);
            if (configEntry != null) {
                try (InputStream configStream = pluginJar.getInputStream(configEntry)) {
                    info = new Gson().fromJson(new String(configStream.readAllBytes()), ModInfo.class);
                }
            }
        }

        if (info == null) {
            LOGGER.info(modFile.getName() + " doesn't look like a mod. Only adding to classpath");
            server.addURLToClassPath(modFile.toURI().toURL());
            return;
        }
        
        if (hasModId(info.getId())) {
            throw new Exception("Plugin id " + info.getId() + " conflict!!");
        }

        server.addURLToClassPath(modFile.toURI().toURL());

        LOGGER.info("Constructing " + info.getName() + " (id: " + info.getId() + " )");

        IPancakeMod mod = (IPancakeMod) PancakeServer.class.getClassLoader().loadClass(info.getModClassName())
                .getConstructor().newInstance();

        ModData data = new ModData(server, mod, info, new ModDataStorage(new File(modStorage.getDirectory(), info.getId())));

        modMap.put(info.getId(), data);

        String mixinConfigName = info.getMixinConfigName();
        if (!mixinConfigName.isBlank()) {
            try {
                Mixins.addConfiguration(mixinConfigName);
                LOGGER.info("Mixin config loaded from " + info.getMixinConfigName());
            } catch (Exception e) {
                LOGGER.error("Cannot apply mixin for " + info.getName() + " mod can work incorrectly");
            }
        }

        mod.init(data);
        LOGGER.info(info.getName() + " has been initialized");

        mod.onLoad();

        LOGGER.info(info.getName() + " has been loaded");

        if (server.getStartStatus() != ServerStartStatus.NOT_STARTED) {
            mod.onServerPreInit();

            if (server.getStartStatus() != ServerStartStatus.PREINIT) {
                mod.onServerPostInit();
            }
        }

	}

}

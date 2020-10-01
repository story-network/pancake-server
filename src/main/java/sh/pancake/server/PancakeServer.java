/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import sh.pancake.classloader.ModdedClassLoader;
import sh.pancake.common.storage.ObjectStorage;

public class PancakeServer implements IPancakeServer {

    private static final Logger LOGGER = LogManager.getLogger("PancakeServer");

    private static final String version;

    static {
        try {
            version = new String(PancakeServer.class.getClassLoader().getResourceAsStream("target_version").readAllBytes());
        } catch (IOException e) {
            LOGGER.fatal("Cannot load target version");

            throw new RuntimeException(e);
        }
    }

    public String getVersion() {
        return version;
    }
 
    public void start(String[] args, ModdedClassLoader loader, ObjectStorage serverDataStorage, Runnable finishMixinInit) {
		LOGGER.info("Running on " + Runtime.version().toString());
        LOGGER.info("Total Memory: " + (Math.floor(Runtime.getRuntime().maxMemory() / 1024 / 1024) / 1024d) + " GB");

        LOGGER.info("Server version: " + getVersion());
        LOGGER.info("Applying server arguments [ " + String.join(", ", args) + " ]");

        // ah yes pancake
        Mixins.addConfiguration("pancake-config.json");
        finishMixinInit.run();

        List<String> argList = new ArrayList<>(Arrays.asList(args));

        // Kill ugly gui
        argList.add("nogui");

        // Force to use our configuration
        loader.addIgnoreRes("log4j2.xml");

        try {
            Class<?> serverClass = loader.loadClass("net.minecraft.server.Main");

            serverClass.getMethod("main", String[].class).invoke(null, (Object) argList.toArray(new String[argList.size()]));
        } catch (Exception e) {
            LOGGER.info("Server start failed: " + e.getLocalizedMessage());
        }
    }

}

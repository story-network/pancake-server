/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import sh.pancake.classloader.ModdedClassLoader;

public class ExampleServer implements IPancakeServer {

    private static final Logger LOGGER = LogManager.getLogger("ExampleServer");

    public String getVersion() {
        return "1.16.3";
    }
 
    public void start(String[] args, ModdedClassLoader loader) {
        LOGGER.info("Running on " + Runtime.version().toString());
        LOGGER.info("Total Memory: " + (Math.floor(Runtime.getRuntime().maxMemory() / 1024 / 1024) / 1024d) + " GB");

        LOGGER.info("Server version: " + getVersion());
        LOGGER.info("Applying server arguments [ " + String.join(", ", args) + " ]");

        try {
            Class<?> serverClass = loader.loadClass("net.minecraft.server.Main");

            serverClass.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (Exception e) {
            LOGGER.info("Server start failed: " + e.getLocalizedMessage());
        }
    }

}

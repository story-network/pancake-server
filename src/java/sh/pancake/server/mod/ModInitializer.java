/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.spongepowered.asm.mixin.Mixins;

import sh.pancake.launcher.classloader.DynamicURLClassLoader;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.extension.ExtensionStore;
import sh.pancake.server.util.ExecutorServiceUtil;

public class ModInitializer {

    private final PancakeServer server;

    private final DynamicURLClassLoader loader;

    public ModInitializer(PancakeServer server, DynamicURLClassLoader loader) {
        this.server = server;
        this.loader = loader;
    }

    public void load(ExtensionStore<ModInfo> store) throws Exception {
        var iterator = store.iterator();

        List<Callable<Void>> taskList = new ArrayList<>();

        while (iterator.hasNext()) {
            final var ext = iterator.next();

            Callable<Void> task = () -> {
                try {
                    loader.addURL(ext.getURL());

                    Class<?> mainClass = loader.loadClass(ext.getMetadata().getEntryClassName());

                    ModEntrypoint entry = mainClass.asSubclass(ModEntrypoint.class).getConstructor().newInstance();
                    entry.modMain(server, ext);

                    if (ext.getMetadata().getMixinConfigName() != null) {
                        Mixins.addConfiguration(ext.getMetadata().getMixinConfigName());
                    }
                } catch (Exception e) {
                    throw new Exception("Error while loading mod. id: " + ext.getId(), e);
                }

                return null;
            };

            taskList.add(task);
        }

        ExecutorServiceUtil.all(server.getExecutorService(), taskList);
    }

}

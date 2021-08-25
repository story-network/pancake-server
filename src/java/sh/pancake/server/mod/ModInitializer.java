/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixins;

import sh.pancake.server.PancakeServer;
import sh.pancake.server.extension.DependencySorter;
import sh.pancake.server.extension.ExtensionStore;
import sh.pancake.server.util.ExecutorServiceUtil;

public class ModInitializer {

    private final PancakeServer server;

    private final ClassLoader classLoader;

    private final Consumer<URL> addURL;

    public ModInitializer(PancakeServer server, ClassLoader classLoader, Consumer<URL> addURL) {
        this.server = server;
        this.classLoader = classLoader;
        this.addURL = addURL;
    }

    public void load(ExtensionStore<ModInfo> store) throws Exception {
        var iterator = new DependencySorter<>(store).sortedList().iterator();

        List<Callable<Void>> taskList = new ArrayList<>();

        while (iterator.hasNext()) {
            final var ext = iterator.next();

            Callable<Void> task = () -> {
                try {
                    addURL.accept(ext.getURL());

                    Class<?> mainClass = classLoader.loadClass(ext.getMetadata().getEntryClassName());

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

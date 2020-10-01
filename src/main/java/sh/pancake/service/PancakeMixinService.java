/*
 * Created on Tue Sep 29 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.util.ReEntranceLock;

import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.transformer.EmptyTransformerProvider;

// Pancake!!
public class PancakeMixinService implements IMixinService, IClassProvider, IClassTracker, IClassBytecodeProvider {

    private static final Logger LOGGER = LogManager.getLogger("PancakeMixinService");

    private ITransformerProvider transformerProvider = new EmptyTransformerProvider();

    private ReEntranceLock lock = new ReEntranceLock(10);

    private boolean valid = true;

    @Override
    public String getName() {
        return "Pancake";
    }

    @Override
    public void prepare() {
        try {
            PancakeLauncher.getLauncher().getLauncherClassLoader().hashCode();
            LOGGER.info("Pancake is prepared");
        } catch (Exception e) {
            LOGGER.error("Pancake failed start!!!");
            valid = false;
        }
    }

    @Override
    public Phase getInitialPhase() {
        return Phase.PREINIT;
    }

    @Override
    public void init() {
        try {
            PancakeLauncher.getLauncher().getLauncherClassLoader().hashCode();
            LOGGER.info("Pancake init");
        } catch (Exception e) {
            LOGGER.error("Pancake failed init!!");
            valid = false;
        }
    }

    @Override
    public void beginPhase() {

    }

    @Override
    public void checkEnv(Object bootSource) {
        LOGGER.info("Pancake should boot with " + PancakeLauncher.getLauncher().getLauncherClassLoader());

        if (PancakeLauncher.getLauncher().getLauncherClassLoader() != bootSource.getClass().getClassLoader()) {
            LOGGER.error("Pancake is booting with wrong ClassLoader!!");
            valid = false;
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return lock;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return transformerProvider;
    }

    @Override
    public IClassTracker getClassTracker() {
        return this;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.singletonList("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        try {
            return new ContainerHandleURI(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return Collections.emptyList();
    }

    @Override
    public String getSideName() {
        return "SERVER";
    }

    @Override
    public CompatibilityLevel getMinCompatibilityLevel() {
        return CompatibilityLevel.JAVA_9;
    }

    @Override
    public CompatibilityLevel getMaxCompatibilityLevel() {
        return CompatibilityLevel.JAVA_11;
    }

    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return PancakeLauncher.getLauncher().getServerClassLoader().getResourceAsStream(name);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, PancakeLauncher.getLauncher().getServerClassLoader());
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, PancakeLauncher.getLauncher().getLauncherClassLoader());
    }

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        ClassReader classReader = new ClassReader(PancakeLauncher.getLauncher().getServerClassLoader().getClassByteArray(name));

        ClassNode classNode = new ClassNode();

        classReader.accept(classNode, 0);

        return classNode;
    }

    @Override
    public void registerInvalidClass(String className) {
        
    }

    @Override
    public boolean isClassLoaded(String className) {
        return PancakeLauncher.getLauncher().getServerClassLoader().isClassLoaded(className);
    }

    @Override
    public String getClassRestrictions(String className) {
        return "";
    }
    
}

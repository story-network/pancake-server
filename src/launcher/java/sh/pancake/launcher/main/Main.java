/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher.main;

import java.io.File;

import sh.pancake.launcher.PancakeLauncher;

public class Main {

    private static final String TARGET_SERVER;

    static {
        try {
            TARGET_SERVER = new String(Main.class.getClassLoader().getResourceAsStream("target_server").readAllBytes());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void main(String[] args) throws Throwable {
        PancakeLauncher.createLauncher(new File(TARGET_SERVER).toURI().toURL(), args).launch();
    }
    
}

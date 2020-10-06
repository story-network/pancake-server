/*
 * Created on Mon Oct 05 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher.mod;

public interface IClassModder {

    boolean isInited();
    void initModder();

    byte[] transformClassData(String name, byte[] data);

}
/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

import java.io.File;

import sh.pancake.common.storage.DiskIOStorage;

public class PluginDataStorage extends DiskIOStorage {

    public PluginDataStorage(File dir) {
        super(dir);
    }
    
}

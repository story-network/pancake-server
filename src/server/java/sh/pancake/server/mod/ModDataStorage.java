/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.io.File;

import sh.pancake.common.storage.DiskIOStorage;

public class ModDataStorage extends DiskIOStorage {

    public ModDataStorage(File dir) {
        super(dir);
    }
    
}

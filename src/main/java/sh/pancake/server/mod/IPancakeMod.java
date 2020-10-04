/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import sh.pancake.server.IPancakeExtra;

public interface IPancakeMod extends IPancakeExtra {

    /*
     *
     * Called right after initialized.
     * 
     * Mod should store ModData or you will never get it again!!
     * 
     */
    void init(ModData data);
    
}

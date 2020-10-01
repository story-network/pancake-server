/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

public interface IPancakePlugin {

    void init(PluginData data);
    
    void onLoad();
    void onUnload();

}

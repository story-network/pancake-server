/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

public interface IPancakeExtra {
    
    /*
     * Invoked after loaded
     */
    default void onLoad() {

    }

    /*
     * Invoked before get unloaded
     */
    default void onUnload() {

    }

    /*
     * Invoked after server initialized
     */
    default void onServerPreInit() {

    }

    /*
     * Invoked after server successfully started
     * It called after a log like "Done (6.755s)!"
     */
    default void onServerPostInit() {

    }

}

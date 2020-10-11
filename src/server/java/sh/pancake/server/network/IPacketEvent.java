/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

public interface IPacketEvent {
    
    boolean isCancelled();
    
    void setCancelled(boolean flag);

}

/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

public interface IEventCancellable extends IEvent {
    
    boolean isCancelled();
    
    void setCancelled(boolean flag);

}

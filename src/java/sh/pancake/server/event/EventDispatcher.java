/*
 * Created on Tue Aug 10 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

public interface EventDispatcher {

    void dispatchEvent(Object event);
    
}

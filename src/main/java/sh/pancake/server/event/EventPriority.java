/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

public enum EventPriority {
    
    // last call
    LATEST,
    LATE,
    // default
    NORMAL,
    EARLY,
    // first call
    EARLIEST

}

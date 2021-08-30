/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event;

public abstract class CancellableEvent implements Cancellable {
    
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        if (!cancelled) {
            cancelled = true;
        }
    }

}

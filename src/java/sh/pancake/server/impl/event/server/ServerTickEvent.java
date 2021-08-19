/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.server;

import java.util.function.BooleanSupplier;

import sh.pancake.server.impl.event.CancellableEvent;

public class ServerTickEvent extends CancellableEvent {

    private final BooleanSupplier timeChecker;
    private final int tickCount;

    public ServerTickEvent(int tickCount, BooleanSupplier timeChecker) {
        this.tickCount = tickCount;
        this.timeChecker = timeChecker;
    }

    public int getTickCount() {
        return tickCount;
    }

    public BooleanSupplier getTimeChecker() {
        return timeChecker;
    }
}

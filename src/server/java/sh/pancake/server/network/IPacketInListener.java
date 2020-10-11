/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

@FunctionalInterface
public interface IPacketInListener {

    void handleIn(AsyncPacketInEvent event);

}

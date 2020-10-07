/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.Iterator;

import com.mojang.brigadier.CommandDispatcher;

public interface IDispatcherSupplier<S> {

    Iterator<CommandDispatcher<S>> getIterator();

}

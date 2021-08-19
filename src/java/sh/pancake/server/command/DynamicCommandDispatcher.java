/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import sh.pancake.server.util.BrigadierUtil;

/**
 * CommandDispatcher with unregister functionality
 */
public class DynamicCommandDispatcher<S> extends CommandDispatcher<S> {

    public void unregister(String literal) {
        BrigadierUtil.unregisterCommand(getRoot(), literal);
    }

    public void unregister(CommandNode<S> node) {
        unregister(node.getName());
    }

}

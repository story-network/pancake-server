/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import com.mojang.brigadier.CommandDispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.CommandSourceStack;

public class CommandManager {

    private static final Logger LOGGER = LogManager.getLogger("CommandManager");
    
    private ServerRootCommandNode<CommandSourceStack> rootNode;

    public CommandManager() {
        this.rootNode = new ServerRootCommandNode<>();
    }

    public ServerRootCommandNode<CommandSourceStack> getRootNode() {
        return rootNode;
    }

    public void registerToServer(CommandDispatcher<CommandSourceStack> serverDispatcher) {
        serverDispatcher.getRoot().addChild(rootNode);
        LOGGER.info("CommandNode successfully registered to " + serverDispatcher);
    }

}

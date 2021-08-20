/*
 * Created on Fri Aug 20 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.SharedSuggestionProvider;

public interface CommandAdvisor {

    void fillSuggestion(CommandNode<SharedSuggestionProvider> suggestion, PancakeCommandStack stack);

}

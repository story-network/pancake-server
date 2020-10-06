/*
 * Created on Tue Oct 06 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import sh.pancake.server.PancakeServer;

public interface ICommandStack extends SharedSuggestionProvider {
    
    PancakeServer getPancakeServer();

    CommandSourceStack getSourceStack();

}

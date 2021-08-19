/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface CommandExecutor {

    CommandResult executeCommand(StringReader reader, PancakeCommandStack stack) throws CommandSyntaxException;
    
}

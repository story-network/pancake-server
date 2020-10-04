/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.CommandSourceStack;
import sh.pancake.server.IPancakeExtra;

public class CommandManager {

    private static final Logger LOGGER = LogManager.getLogger("CommandManager");
    
    private CommandDispatcher<CommandSourceStack> pancakeDispatcher;

    // Separate by IPancakeExtra so commands can unload when they are unloaded
    private WeakHashMap<IPancakeExtra, CommandDispatcher<CommandSourceStack>> extraMap;

    public CommandManager() {
        this.extraMap = new WeakHashMap<>();
        
        this.pancakeDispatcher = new CommandDispatcher<>();

        pancakeDispatcher.register(
        LiteralArgumentBuilder.<CommandSourceStack>literal("pancake")
            .executes((context) -> { LOGGER.info("Command executed!"); return 1; })
        );
    }

    public CommandDispatcher<CommandSourceStack> getDispatcherFor(IPancakeExtra extra) {
        return extraMap.computeIfAbsent(extra, (ex) -> new CommandDispatcher<CommandSourceStack>());
    }

    public void onMCCommandInit(CommandDispatcher<CommandSourceStack> serverDispatcher) {
        
    }

    public List<CommandDispatcher<CommandSourceStack>> getDispatcherList() {
        List<CommandDispatcher<CommandSourceStack>> list = new ArrayList<>();

        list.add(pancakeDispatcher);

        list.addAll(extraMap.values());

        return list;
    }

    // returns execution count
    public int performCommand(CommandSourceStack stack, String rawCommand) {
        return 0;
    }

}

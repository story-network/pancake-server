/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import com.mojang.brigadier.CommandDispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class CommandManager<T> implements IDispatcherSupplier<ICommandStack> {

    private static final Logger LOGGER = LogManager.getLogger("CommandManager");

    // Separate by T so commands can unload when they are unloaded
    private WeakHashMap<T, CommandDispatcher<ICommandStack>> extraMap;

    private CommandDispatcher<ICommandStack> serverDispatcher;

    public CommandManager() {
        this.extraMap = new WeakHashMap<>();

        this.serverDispatcher = new CommandDispatcher<>();
    }

    public CommandDispatcher<ICommandStack> getServerDispatcher() {
        return serverDispatcher;
    }

    public CommandDispatcher<ICommandStack> getDispatcherFor(T extra) {
        return extraMap.computeIfAbsent(extra, (ex) -> new CommandDispatcher<ICommandStack>());
    }

    public void onMCCommandInit(CommandDispatcher<CommandSourceStack> mcDispatcher) {
        LOGGER.info("Initializing commands for " + mcDispatcher);
    }

    public List<CommandDispatcher<ICommandStack>> getDispatcherList() {
        List<CommandDispatcher<ICommandStack>> list = new ArrayList<>();

        list.add(serverDispatcher);

        list.addAll(extraMap.values());

        return list;
    }

    public void sendAllCommandList(ServerPlayer player) {
        player.getServer().getCommands().sendCommands(player);
    }

    @Override
    public Iterator<CommandDispatcher<ICommandStack>> getIterator() {
        return getDispatcherList().iterator();
    }

}

/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.command;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.command.PancakeCommandDispatcher;
import sh.pancake.server.command.PancakeCommandStack;

public class ReloadCommand {

    public static void register(PancakeServer server, PancakeCommandDispatcher<PancakeCommandStack> dispatcher) {
        dispatcher.register(
            PancakeCommandStack.literal("reload")
            .requires((stack) -> stack.getInnerStack().hasPermission(4))
            .then(
                PancakeCommandStack.literal("pancake").executes((ctx) -> reloadPancake(ctx.getSource()))
            ).then(
                PancakeCommandStack.literal("full").executes((ctx) -> reloadFull(ctx.getSource()))
            )
        );
    }

    private static int reloadFull(PancakeCommandStack source) {
        MinecraftServer server = source.getInnerStack().getServer();

        server.submit(() -> server.saveAllChunks(false, true, false))
        .thenApply((v) -> server.reloadResources(server.getPackRepository().getSelectedIds()))
        .thenApply((v) -> source.getServer().reload())
        .exceptionally((t) -> {
            source.getInnerStack().sendFailure(new TranslatableComponent("commands.reload.failure"));

            return null;
        });

        source.getInnerStack().sendSuccess(new TranslatableComponent("commands.reload.success"), true);
        return 1;
    }

    private static int reloadPancake(PancakeCommandStack source) {
        source.getServer().reload()
        .exceptionally((t) -> {
            source.getInnerStack().sendFailure(new TranslatableComponent("commands.reload.failure"));

            return null;
        });

        source.getInnerStack().sendSuccess(new TranslatableComponent("commands.reload.success"), true);
        return 1;
    }

}

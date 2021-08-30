/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.world.entity.Pose;
import sh.pancake.server.command.PancakeCommandDispatcher;
import sh.pancake.server.command.PancakeCommandStack;

public class TestCommand {

    public static void register(PancakeCommandDispatcher<PancakeCommandStack> dispatcher) {
        dispatcher.register(
            PancakeCommandStack.literal("test")
            .then(
                PancakeCommandStack.literal("pose")
                .then(
                    PancakeCommandStack.argument("pose_type", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (var pose : Pose.values()) {
                            builder.suggest(pose.toString());
                        }

                        return builder.buildFuture();
                    })
                    .executes((ctx) -> {
                        var inner = ctx.getSource().getInnerStack();
                        var pose = Pose.valueOf(StringArgumentType.getString(ctx, "pose_type"));

                        if (pose == null) return 0;

                        inner.getEntityOrException().setPose(pose);

                        return 1;
                    })
                )
            )
        );
    }
    
}

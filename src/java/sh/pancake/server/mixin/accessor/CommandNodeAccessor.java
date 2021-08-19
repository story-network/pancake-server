/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.accessor;

import java.util.Map;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(CommandNode.class)
public interface CommandNodeAccessor<S> {

    @Accessor("children")
    Map<String, CommandNode<S>> getChildren();

    @Accessor("literals")
    Map<String, LiteralCommandNode<S>> getLiterals();

    @Accessor("arguments")
    Map<String, ArgumentCommandNode<S, ?>> getArguments();

}

/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.util;

import com.mojang.brigadier.tree.CommandNode;

import sh.pancake.server.mixin.accessor.CommandNodeAccessor;

public class BrigadierUtil {
    
    @SuppressWarnings("unchecked")
    public static <S> void unregisterCommand(CommandNode<S> node, String literal) {
        CommandNodeAccessor<S> nodeAccessor = (CommandNodeAccessor<S>) node;

        nodeAccessor.getChildren().remove(literal);
        nodeAccessor.getLiterals().remove(literal);
        nodeAccessor.getArguments().remove(literal);
    }

}

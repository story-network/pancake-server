/*
 * Created on Tue Aug 24 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

/**
 * CommandDispatcher with Dynamic unregistration, comma separated namespaced command registration functionality
 */
public class PancakeCommandDispatcher<S> extends CommandDispatcher<S> {

    private final String namespace;

    public PancakeCommandDispatcher(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public void unregister(String literal) {
        BrigadierUtil.unregisterCommand(getRoot(), getFullCommand(literal));
        BrigadierUtil.unregisterCommand(getRoot(), literal);
    }

    public void unregister(CommandNode<S> node) {
        unregister(node.getName());
    }

    public String getFullCommand(String command) {
        return namespace + ":" + command;
    }
    
    @Override
    public LiteralCommandNode<S> register(LiteralArgumentBuilder<S> command) {
        LiteralCommandNode<S> node = super.register(command);

        LiteralArgumentBuilder<S> namespaced = LiteralArgumentBuilder.literal(getFullCommand(node.getLiteral()));

        namespaced.requires(node.getRequirement());
        namespaced.forward(node.getRedirect(), node.getRedirectModifier(), node.isFork());

        super.register(namespaced);

        return node;
    }

}

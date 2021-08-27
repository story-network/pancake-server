/*
 * Created on Mon Aug 16 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import sh.pancake.server.PancakeServer;

/**
 * Wrapping minecraft CommandSourceStack and provides additional datas.
 */
public class PancakeCommandStack implements SharedSuggestionProvider {

    private final PancakeServer server;

    private final CommandSourceStack innerStack;
    
    public PancakeCommandStack(PancakeServer server, CommandSourceStack innerStack) {
        this.server = server;
        this.innerStack = innerStack;
    }

    /**
     * Compute inner CommandSourceStack
     *
     * @param function
     * @return New PancakeCommandStack Object with computed CommandSourceStack
     */
    public PancakeCommandStack compute(Function<CommandSourceStack, CommandSourceStack> function) {
        return new PancakeCommandStack(server, function.apply(innerStack));
    }

    public PancakeServer getServer() {
        return server;
    }

    public CommandSourceStack getInnerStack() {
        return innerStack;
    }

    public static LiteralArgumentBuilder<PancakeCommandStack> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<PancakeCommandStack, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(
        CommandContext<SharedSuggestionProvider> ctx,
        SuggestionsBuilder builder
    ) {
        return innerStack.customSuggestion(ctx, builder);
    }

    @Override
    public Collection<String> getAllTeams() {
        return innerStack.getAllTeams();
    }

    @Override
    public Collection<ResourceLocation> getAvailableSoundEvents() {
        return innerStack.getAvailableSoundEvents();
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        return innerStack.getOnlinePlayerNames();
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames() {
        return innerStack.getRecipeNames();
    }

    @Override
    public boolean hasPermission(int level) {
        return innerStack.hasPermission(level);
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return innerStack.levels();
    }

    @Override
    public RegistryAccess registryAccess() {
        return innerStack.registryAccess();
    }
}

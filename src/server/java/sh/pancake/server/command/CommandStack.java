/*
 * Created on Tue Oct 06 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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

public class CommandStack implements ICommandStack {

    private PancakeServer server;
    private CommandSourceStack source;

    public CommandStack(PancakeServer server, CommandSourceStack source) {
        this.server = server;
        this.source = source;
    }

    public PancakeServer getPancakeServer() {
        return server;
    }

    public CommandSourceStack getSourceStack() {
        return source;
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<SharedSuggestionProvider> ctx,
            SuggestionsBuilder builder) {
        return source.customSuggestion(ctx, builder);
    }

    @Override
    public Collection<String> getAllTeams() {
        return source.getAllTeams();
    }

    @Override
    public Collection<ResourceLocation> getAvailableSoundEvents() {
        return source.getAvailableSoundEvents();
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        return source.getOnlinePlayerNames();
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames() {
        return source.getRecipeNames();
    }

    @Override
    public boolean hasPermission(int lvl) {
        return source.hasPermission(lvl);
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return source.levels();
    }

    @Override
    public RegistryAccess registryAccess() {
        return source.registryAccess();
    }

}

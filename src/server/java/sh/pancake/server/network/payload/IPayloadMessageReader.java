/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.payload;

import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface IPayloadMessageReader {
    
    void readPayload(ServerPlayer sender, ResourceLocation location, FriendlyByteBuf byteBuf, Consumer<FriendlyByteBuf> replier);

}

/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPayloadMessageReader {
    
    void readPayload(ResourceLocation location, FriendlyByteBuf byteBuf);

}

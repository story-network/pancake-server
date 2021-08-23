/*
 * Created on Sat Aug 21 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.payload;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public interface PayloadCollector {

    void fillPayloadChannels(Set<ResourceLocation> set);
    
}

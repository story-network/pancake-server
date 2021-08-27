/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public class PlayerOpenMenuPreEvent extends ServerPlayerEventImpl {

    private MenuProvider provider;

    public PlayerOpenMenuPreEvent(ServerPlayer player, @Nullable MenuProvider provider) {
        super(player);
        this.provider = provider;
    }

    @Nullable
    public MenuProvider getProvider() {
        return provider;
    }

    public void setProvider(@Nullable MenuProvider provider) {
        this.provider = provider;
    }

}

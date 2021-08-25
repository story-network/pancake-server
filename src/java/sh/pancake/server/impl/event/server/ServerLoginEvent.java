/*
 * Created on Tue Aug 24 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.server;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import net.minecraft.network.chat.Component;
import sh.pancake.server.impl.event.CancellableEvent;

public class ServerLoginEvent extends CancellableEvent {

    private final Channel channel;
    private final GameProfile profile;

    private State state;

    private Component kickMessage;

    public ServerLoginEvent(Channel channel, GameProfile profile, State state, @Nullable Component kickMessage) {
        this.channel = channel;
        this.profile = profile;
        this.state = state;
        this.kickMessage = kickMessage;
    }

    public Channel getChannel() {
        return channel;
    }

    public GameProfile getProfile() {
        return profile;
    }

    @Nullable
    public Component getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(Component kickMessage) {
        this.kickMessage = kickMessage;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public static enum State {

        ALLOW,
        DENY

    }

}

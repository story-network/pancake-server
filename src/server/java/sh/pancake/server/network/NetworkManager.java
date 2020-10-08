/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import java.io.IOException;
import java.util.List;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.network.AsyncPacketInEvent;
import sh.pancake.server.impl.event.network.AsyncPacketOutEvent;

public class NetworkManager {

    private static final Logger LOGGER = LogManager.getLogger("NetworkManager");
    
    private PancakeServer server;

    private WeakHashMap<Channel, GameProfile> channelMap;

    public NetworkManager(PancakeServer server) {
        this.server = server;
        this.channelMap = new WeakHashMap<>();
    }

    public PancakeServer getServer() {
        return server;
    }

    @Nullable
    public GameProfile getProfile(Channel channel) {
        return channelMap.get(channel);
    }

    @Nullable
    public ServerPlayer getPlayer(Channel channel) {
        GameProfile profile = getProfile(channel);

        if (profile == null) return null;

        return server.getMinecraftServer().getPlayerList().getPlayer(profile.getId());
    }

    public void handleOutPacket(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf buf, PacketFlow flow) throws Exception {
        ConnectionProtocol protocol = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
        if (protocol == null) throw new RuntimeException("Invalid ConnectionProtocol for " + packet);

        FriendlyByteBuf wrapped = new FriendlyByteBuf(buf);

        ServerPlayer player = getPlayer(ctx.channel());

        AsyncPacketOutEvent outEvent = new AsyncPacketOutEvent(packet, player);
        server.getEventManager().callEvent(outEvent);
        if (outEvent.isCancelled()) return;

        Integer packetId;
        if (packet instanceof ICustomPacket) {
            packetId = protocol.getPacketId(flow, ((ICustomPacket<?>) packet).getTargetPacket());
        } else {
            packetId = protocol.getPacketId(flow, packet);
        }
        try {
            outEvent.getSerializer().writeTo(wrapped, packetId, packet);
        } catch (Throwable throwable) {
            LOGGER.error(throwable);
            if (packet.isSkippable()) throw new SkipPacketException(throwable);
            throw throwable;
        }
    }
    
    public void handleInPacket(ChannelHandlerContext ctx, ByteBuf buf, List<Object> packetList, PacketFlow flow) throws Exception {
        if (buf.readableBytes() <= 0) return;

        FriendlyByteBuf wrapped = new FriendlyByteBuf(buf);

        ConnectionProtocol protocol = ((ConnectionProtocol) ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get());
        int packetId = wrapped.readVarInt();

        Packet<?> packet = protocol.createPacket(flow, packetId);

        if (packet == null) {
            throw new IOException("Bad packet " + packetId);
        }

        packet.read(wrapped);

        if (wrapped.readableBytes() > 0) {
            // Skip extra data so we dont lose packet
            LOGGER.warn("Packet " + packetId + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + wrapped.readableBytes() + " bytes extra whilst reading packet " + packet);
            wrapped.readerIndex(wrapped.writerIndex());
        }

        if (protocol == ConnectionProtocol.LOGIN && packet instanceof ServerboundHelloPacket) {
            ServerboundHelloPacket helloPacket = (ServerboundHelloPacket) packet;

            channelMap.put(ctx.channel(), helloPacket.getGameProfile());
        }

        ServerPlayer player = getPlayer(ctx.channel());

        AsyncPacketInEvent inEvent = new AsyncPacketInEvent(packet, player);
        server.getEventManager().callEvent(inEvent);
        if (inEvent.isCancelled()) return;

        packetList.add(inEvent.getPacket());
    }

}

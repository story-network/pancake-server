/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import sh.pancake.server.IPancakeExtra;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.network.DefaultPacketSerializer;
import sh.pancake.server.impl.network.IPacketSerializer;

public class NetworkManager {

    private static final Logger LOGGER = LogManager.getLogger("NetworkManager");
    
    private PancakeServer server;

    private WeakHashMap<Channel, GameProfile> channelMap;

    private ExtraPacketHandlerMap handlerMap;

    public NetworkManager(PancakeServer server) {
        this.server = server;
        this.channelMap = new WeakHashMap<>();

        this.handlerMap = new ExtraPacketHandlerMap();
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

    protected boolean hasHandlerFor(IPancakeExtra extra, Class<? extends Packet<?>> cl) {
        return handlerMap.containsKeyOf(extra, cl);
    }

    protected boolean hasHandler(Class<?> cl) {
        return handlerMap.valuesMap().stream().anyMatch((map) -> map.containsKey(cl));
    }

    protected Collection<PacketHandler<? extends Packet<?>>> getAllHandlerof(Class<?> cl) {
        Iterator<Map<Class<? extends Packet<?>>, PacketHandler<? extends Packet<?>>>> iter = handlerMap.valuesMap().iterator();

        List<PacketHandler<? extends Packet<?>>> list = new ArrayList<>();

        while (iter.hasNext()) {
            Map<Class<? extends Packet<?>>, PacketHandler<? extends Packet<?>>> map = iter.next();
            if (!map.containsKey(cl)) continue;

            list.add(map.get(cl));
        }

        return list;
    }

    public <R extends Packet<?>>PacketHandler<R> getHandlerFor(IPancakeExtra extra, Class<R> cl) {
        return (PacketHandler<R>) handlerMap.computeIfAbsentOf(extra, cl, (clazz) -> new PacketHandler<R>());
    }

    public void isListenerRegistered(IPancakeExtra extra, Class<? extends Packet<?>> targetPacket, IPacketInListener inListener) {
        getHandlerFor(extra, targetPacket).isRegistered(inListener);
    }

    public void isListenerRegistered(IPancakeExtra extra, Class<? extends Packet<?>> targetPacket, IPacketOutListener outListener) {
        getHandlerFor(extra, targetPacket).isRegistered(outListener);
    }

    public void registerListener(IPancakeExtra extra, Class<? extends Packet<?>> targetPacket, IPacketInListener inListener) {
        getHandlerFor(extra, targetPacket).register(inListener);
    }

    public void registerListener(IPancakeExtra extra, Class<? extends Packet<?>> targetPacket, IPacketOutListener outListener) {
        getHandlerFor(extra, targetPacket).register(outListener);
    }

    public void unregisterListener(IPancakeExtra extra, Class<? extends Packet<?>> targetPacket, IPacketInListener inListener) {
        getHandlerFor(extra, targetPacket).unregister(inListener);
    }

    public void unregisterListener(IPancakeExtra extra, Class<? extends Packet<?>> targetPacket, IPacketOutListener outListener) {
        getHandlerFor(extra, targetPacket).unregister(outListener);
    }

    public void callPacketEvent(AsyncPacketInEvent event) {
        getAllHandlerof(event.getPacket().getClass()).forEach((handler) -> {
            Iterator<IPacketInListener> iter = handler.getInIterator();

            while (iter.hasNext()) iter.next().handleIn(event);
        });
    }

    public void callPacketEvent(AsyncPacketOutEvent event) {
        getAllHandlerof(event.getPacket().getClass()).forEach((handler) -> {
            Iterator<IPacketOutListener> iter = handler.getOutIterator();

            while (iter.hasNext()) iter.next().handleOut(event);
        });
    }

    public void handleOutPacket(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf buf, PacketFlow flow) throws Exception {
        ConnectionProtocol protocol = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
        if (protocol == null) throw new RuntimeException("Invalid ConnectionProtocol for " + packet);

        FriendlyByteBuf wrapped = new FriendlyByteBuf(buf);

        ServerPlayer player = getPlayer(ctx.channel());

        Packet<?> writePacket = packet;
        IPacketSerializer serializer = DefaultPacketSerializer.getInstance();
        
        if (hasHandler(packet.getClass())) {
            AsyncPacketOutEvent outEvent = new AsyncPacketOutEvent(packet, player);

            callPacketEvent(outEvent);
            
            if (outEvent.isCancelled()) return;

            if (writePacket != outEvent.getPacket()) writePacket = outEvent.getPacket();
            if (serializer != outEvent.getSerializer()) serializer = outEvent.getSerializer();
        }

        Integer packetId;
        if (writePacket instanceof ICustomPacket) {
            packetId = protocol.getPacketId(flow, ((ICustomPacket<?>) writePacket).getTargetPacket());
        } else {
            packetId = protocol.getPacketId(flow, writePacket);
        }
        try {
            serializer.writeTo(wrapped, packetId, writePacket);
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

        Packet<?> readPacket = packet;
        if (hasHandler(packet.getClass())) {
            AsyncPacketInEvent inEvent = new AsyncPacketInEvent(packet, player);
            
            callPacketEvent(inEvent);
            
            if (inEvent.isCancelled()) return;

            if (readPacket != inEvent.getPacket()) readPacket = inEvent.getPacket();
        }

        packetList.add(readPacket);
    }

}

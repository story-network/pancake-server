/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.payload;

import java.util.Iterator;
import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.IPancakeExtra;
import sh.pancake.server.PancakeServer;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

public class PayloadChannelManager {

    private PancakeServer server;

    private PayloadHandlerMap handlerMap;

    public PayloadChannelManager(PancakeServer server) {
        this.server = server;

        this.handlerMap = new PayloadHandlerMap();
    }

    public void addChannelReader(IPancakeExtra extra, ResourceLocation location, IPayloadMessageReader reader) {
        handlerMap.getInfoFor(extra, location).addReader(reader);
    }

    public void removeChannelReader(IPancakeExtra extra, ResourceLocation location, IPayloadMessageReader reader) {
        handlerMap.getInfoFor(extra, location).removeReader(reader);
    }

    public void clearChannelReader(IPancakeExtra extra, ResourceLocation location) {
        handlerMap.getInfoFor(extra, location).clear();
    }

    public void dispatchMessageRead(ServerPlayer sender, ResourceLocation location, FriendlyByteBuf bytebuf) {
        Iterator<PayloadHandlerMap.Info> infoIter = handlerMap.getAllInfoList(location).iterator();

        Consumer<IPayloadMessageReader> readerConsumer = (reader) -> reader.readPayload(sender, location, bytebuf, (buf) -> sendPayload(sender, location, buf));

        while (infoIter.hasNext()) {
            infoIter.next().forEach(readerConsumer);
        }
    }

    public void broadcastPayload(ResourceLocation location, FriendlyByteBuf bytebuf) {
        server.getNetworkManager().broadcastPacket(new ClientboundCustomPayloadPacket(location, bytebuf));
    }

    public void sendPayload(ServerPlayer player, ResourceLocation location, FriendlyByteBuf bytebuf) {
        server.getNetworkManager().sendPacket(player, new ClientboundCustomPayloadPacket(location, bytebuf));
    }

}

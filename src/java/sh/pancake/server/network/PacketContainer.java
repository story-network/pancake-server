/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

/**
 * Packet manufacturing container
 */
public class PacketContainer {

    private int type;
    private final FriendlyByteBuf dataBuf;

    public PacketContainer(int type) {
        this(type, new FriendlyByteBuf(Unpooled.buffer()));
    }

    private PacketContainer(int type, FriendlyByteBuf dataBuf) {
        this.type = type;
        this.dataBuf = dataBuf;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FriendlyByteBuf getDataBuf() {
        return dataBuf;
    }

    public FriendlyByteBuf getDataView() {
        FriendlyByteBuf view = new FriendlyByteBuf(dataBuf.slice());

        view.readerIndex(dataBuf.readerIndex());
        view.writerIndex(dataBuf.writerIndex());

        return view;
    }

    public int packetSize() {
        return FriendlyByteBuf.getVarIntSize(type) + dataBuf.readableBytes();
    }

    public void writePacketTo(ByteBuf buf) {
        writePacketTo(new FriendlyByteBuf(buf));
    }

    public void writePacketTo(FriendlyByteBuf buf) {
        buf.writeVarInt(type);
        buf.writeBytes(dataBuf.slice());
    }

    public Packet<?> toPacket(PacketFlow flow) {
        ConnectionProtocol protocol = ConnectionProtocol.getById(type);

        return toPacket(protocol, flow);
    }

    public Packet<?> toPacket(ConnectionProtocol protocol, PacketFlow flow) {
        return protocol.createPacket(flow, type, new FriendlyByteBuf(dataBuf.slice()));
    }

    public static PacketContainer readPacketFrom(ByteBuf buf) {
        return readPacketFrom(new FriendlyByteBuf(buf));
    }

    public static PacketContainer readPacketFrom(FriendlyByteBuf buf) {
        int type = buf.readVarInt();
        FriendlyByteBuf dataBuf = new FriendlyByteBuf(Unpooled.buffer(buf.readableBytes()));
        buf.readBytes(dataBuf);

        return new PacketContainer(type, dataBuf);
    }

    public static PacketContainer fromPacket(PacketFlow flow, Packet<?> packet) {
        ConnectionProtocol protocol = ConnectionProtocol.getProtocolForPacket(packet);
        return fromPacket(protocol, flow, packet);
    }

    public static PacketContainer fromPacket(ConnectionProtocol protocol, PacketFlow flow, Packet<?> packet) {
        FriendlyByteBuf dataBuf = new FriendlyByteBuf(Unpooled.buffer());
        int id = protocol.getPacketId(flow, packet);

        packet.write(dataBuf);

        return new PacketContainer(id, dataBuf);
    }

}

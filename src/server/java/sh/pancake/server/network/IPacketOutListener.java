package sh.pancake.server.network;

@FunctionalInterface
public interface IPacketOutListener {

    void handleOut(AsyncPacketOutEvent event);

}

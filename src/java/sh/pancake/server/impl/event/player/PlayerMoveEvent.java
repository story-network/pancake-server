package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class PlayerMoveEvent extends ServerPlayerEventImpl {

    private final double lastX;
    private final double lastY;
    private final double lastZ;

    private Vec3 vec;

    public PlayerMoveEvent(ServerPlayer player, double lastX, double lastY, double lastZ, Vec3 vec) {
        super(player);

        this.lastX = lastX;
        this.lastY = lastY;
        this.lastZ = lastZ;

        this.vec = vec;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public double getLastZ() {
        return lastZ;
    }

    public Vec3 getVec() {
        return vec;
    }

    public void setVec(Vec3 vec) {
        this.vec = vec;
    }

    public void setEndPosition(double x, double y, double z) {
        setVec(vec.subtract(x, y, z));
    }
    
}

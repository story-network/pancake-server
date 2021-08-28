package sh.pancake.server.mixin.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.level.block.ProjectileHitBlockEvent;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    
    @Inject(method = { "onPlace", "updateShape", "tick", "updateNeighbourShapes" }, at = @At("HEAD"), cancellable = true)
    public void onPlacePre(CallbackInfo info) {
        info.cancel();
    }
    
    @Redirect(
        method = "onProjectileHit",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/level/block/Block.onProjectileHit(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/entity/projectile/Projectile;)V"
        )
    )
    public void onProjectileHit_onProjectileHit(Block block, Level level, BlockState state, BlockHitResult result, Projectile projectile) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            block.onProjectileHit(level, state, result, projectile);
            return;
        }

        ProjectileHitBlockEvent event = new ProjectileHitBlockEvent(level, projectile, state, result);
        server.dispatchEvent(event);

        if (event.isCancelled()) return;
        
        block.onProjectileHit(level, event.getBlockState(), event.getResult(), event.getEntity());
    }

}

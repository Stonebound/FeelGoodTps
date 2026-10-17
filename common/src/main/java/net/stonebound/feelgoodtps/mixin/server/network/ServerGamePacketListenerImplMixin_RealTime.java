package net.stonebound.feelgoodtps.mixin.server.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.TickThrottler;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin_RealTime {

    @Shadow public ServerPlayer player;

    // Chat and item drop spam limits
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TickThrottler;tick()V"))
    private void feelgoodtps$throttlerTick(final TickThrottler throttler, final Operation<Void> original) {
        final int ticks = 1 + FeelGoodTps.extraTicks(this.player);
        for (int i = 0; i < ticks; i++) {
            original.call(throttler);
        }
    }
}

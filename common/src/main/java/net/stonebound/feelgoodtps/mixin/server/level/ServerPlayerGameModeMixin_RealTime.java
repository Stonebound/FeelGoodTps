package net.stonebound.feelgoodtps.mixin.server.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin_RealTime {

    @Shadow @Final protected ServerPlayer player;

    // Block breaking progress, otherwise blocks the client finished breaking get rejected while the server lags
    @WrapOperation(method = "tick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;gameTicks:I", opcode = Opcodes.PUTFIELD))
    private void feelgoodtps$gameTicks(final ServerPlayerGameMode self, final int value, final Operation<Void> original) {
        original.call(self, value + FeelGoodTps.extraTicks(this.player));
    }
}

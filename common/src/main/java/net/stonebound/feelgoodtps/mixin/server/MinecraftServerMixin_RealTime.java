package net.stonebound.feelgoodtps.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin_RealTime {

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void feelgoodtps$trackRealTime(final BooleanSupplier haveTime, final CallbackInfo ci) {
        FeelGoodTps.onServerTick((MinecraftServer) (Object) this);
    }
}

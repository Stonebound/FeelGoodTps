package net.stonebound.feelgoodtps.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PortalProcessor.class)
public abstract class PortalProcessorMixin_RealTime {

    @WrapOperation(method = "processPortalTeleportation",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/PortalProcessor;portalTime:I", opcode = Opcodes.PUTFIELD))
    private void feelgoodtps$portalTime(final PortalProcessor self, final int value, final Operation<Void> original,
                                        final ServerLevel serverLevel, final Entity entity) {
        original.call(self, value + FeelGoodTps.extraTicks(entity));
    }
}

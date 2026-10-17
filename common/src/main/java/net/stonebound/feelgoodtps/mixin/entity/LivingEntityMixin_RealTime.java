package net.stonebound.feelgoodtps.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin_RealTime {

    @WrapOperation(method = "tickDeath",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;deathTime:I", opcode = Opcodes.PUTFIELD))
    private void feelgoodtps$deathTime(final LivingEntity self, final int value, final Operation<Void> original) {
        // Don't run past the end of the death animation unless vanilla already has.
        original.call(self, Math.max(value, Math.min(20, value + FeelGoodTps.extraTicks(self.level()))));
    }
}

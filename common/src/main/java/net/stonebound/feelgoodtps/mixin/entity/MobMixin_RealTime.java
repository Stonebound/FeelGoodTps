package net.stonebound.feelgoodtps.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin_RealTime {

    // Despawn timer
    @WrapOperation(method = "serverAiStep",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Mob;noActionTime:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void feelgoodtps$noActionTime(final Mob self, final int value, final Operation<Void> original) {
        original.call(self, value + FeelGoodTps.extraTicks(self.level()));
    }
}

package net.stonebound.feelgoodtps.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ExperienceOrb;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin_RealTime {

    // Despawn timer
    @WrapOperation(method = "tick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/ExperienceOrb;age:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void feelgoodtps$age(final ExperienceOrb self, final int value, final Operation<Void> original) {
        original.call(self, value + FeelGoodTps.extraTicks(self.level()));
    }
}

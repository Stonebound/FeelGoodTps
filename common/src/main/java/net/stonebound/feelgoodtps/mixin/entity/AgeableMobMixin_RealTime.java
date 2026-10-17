package net.stonebound.feelgoodtps.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.AgeableMob;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin_RealTime {

    // Babies count up towards 0, adults count their breeding cooldown down towards 0
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AgeableMob;setAge(I)V"))
    private void feelgoodtps$growingAge(final AgeableMob self, final int age, final Operation<Void> original) {
        final int extra = FeelGoodTps.extraTicks(self.level());
        if (extra == 0) {
            original.call(self, age);
        } else if (age > 0) {
            original.call(self, Math.max(0, age - extra));
        } else {
            original.call(self, Math.min(0, age + extra));
        }
    }
}

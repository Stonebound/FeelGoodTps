package net.stonebound.feelgoodtps.mixin.entity.monster;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin_RealTime {

    // Curing progress, including the iron bars/bed boost
    @WrapOperation(method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/ZombieVillager;getConversionProgress()I"))
    private int feelgoodtps$conversionProgress(final ZombieVillager self, final Operation<Integer> original) {
        return original.call(self) * (1 + FeelGoodTps.extraTicks(self.level()));
    }
}

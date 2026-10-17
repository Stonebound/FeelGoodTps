package net.stonebound.feelgoodtps.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin_RealTime {

    @WrapOperation(method = "serverTick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;brewTime:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private static void feelgoodtps$brewTime(final BrewingStandBlockEntity self, final int value, final Operation<Void> original) {
        original.call(self, Math.max(0, value - FeelGoodTps.extraTicks(self.getLevel())));
    }
}

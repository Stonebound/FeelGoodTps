package net.stonebound.feelgoodtps.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin_RealTime {

    @Shadow private int cookingTotalTime;

    @WrapOperation(method = "serverTick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;litTimeRemaining:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private static void feelgoodtps$litTime(final AbstractFurnaceBlockEntity self, final int value, final Operation<Void> original) {
        original.call(self, Math.max(0, value - FeelGoodTps.extraTicks(self.getLevel())));
    }

    // Vanilla finishes when the timer is exactly equal to the total, so never step past it
    @WrapOperation(method = "serverTick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;cookingTimer:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private static void feelgoodtps$cookingTimer(final AbstractFurnaceBlockEntity self, final int value, final Operation<Void> original) {
        final int total = ((AbstractFurnaceBlockEntityMixin_RealTime) (Object) self).cookingTotalTime;
        original.call(self, Math.max(value, Math.min(total, value + FeelGoodTps.extraTicks(self.getLevel()))));
    }

    // Progress cooling back down once the furnace runs out of fuel
    @WrapOperation(method = "serverTick",
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I")),
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;cookingTimer:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private static void feelgoodtps$cookingCooldown(final AbstractFurnaceBlockEntity self, final int value, final Operation<Void> original) {
        original.call(self, Math.max(0, value - 2 * FeelGoodTps.extraTicks(self.getLevel())));
    }
}

package net.stonebound.feelgoodtps.mixin.entity.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.item.ItemEntity;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin_RealTime {

    @WrapOperation(method = "tick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/item/ItemEntity;pickupDelay:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void feelgoodtps$pickupDelay(final ItemEntity self, final int value, final Operation<Void> original) {
        original.call(self, Math.max(0, value - FeelGoodTps.extraTicks(self.level())));
    }

    // Despawn timer
    @WrapOperation(method = "tick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/item/ItemEntity;age:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void feelgoodtps$age(final ItemEntity self, final int value, final Operation<Void> original) {
        original.call(self, value + FeelGoodTps.extraTicks(self.level()));
    }
}

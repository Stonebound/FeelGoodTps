package net.stonebound.feelgoodtps.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin_RealTime {

    @WrapOperation(method = "baseTick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;boardingCooldown:I", opcode = Opcodes.PUTFIELD))
    private void feelgoodtps$boardingCooldown(final Entity self, final int value, final Operation<Void> original) {
        original.call(self, Math.max(0, value - FeelGoodTps.extraTicks(self.level())));
    }

    @WrapOperation(method = "processPortalCooldown",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;portalCooldown:I", opcode = Opcodes.PUTFIELD))
    private void feelgoodtps$portalCooldown(final Entity self, final int value, final Operation<Void> original) {
        original.call(self, Math.max(0, value - FeelGoodTps.extraTicks(self.level())));
    }
}

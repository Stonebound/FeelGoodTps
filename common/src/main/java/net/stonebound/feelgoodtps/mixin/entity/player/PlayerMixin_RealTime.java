package net.stonebound.feelgoodtps.mixin.entity.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin_RealTime {

    // Delay between picking up experience orbs
    @WrapOperation(method = "tick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void feelgoodtps$takeXpDelay(final Player self, final int value, final Operation<Void> original) {
        original.call(self, Math.max(0, value - FeelGoodTps.extraTicks(self)));
    }
}

/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.stonebound.feelgoodtps.mixin.entity.item;

import net.minecraft.entity.item.ExperienceOrbEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import net.stonebound.feelgoodtps.RealTimeTrackingBridge;
import net.stonebound.feelgoodtps.WorldBridge;
import net.stonebound.feelgoodtps.mixin.entity.EntityMixin_RealTime;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin_RealTime extends EntityMixin_RealTime {

    @Shadow public int delayBeforeCanPickup;
    @Shadow public int xpOrbAge;

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/item/ExperienceOrbEntity;delayBeforeCanPickup:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Entity;tick()V"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/item/ExperienceOrbEntity;hasNoGravity()Z"
            )
        )
    )
    private void realTimeImpl$adjustForRealTimePickupDelay(final ExperienceOrbEntity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake()) {
            this.delayBeforeCanPickup = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        this.delayBeforeCanPickup = Math.max(0, this.delayBeforeCanPickup - ticks);
    }

    @Redirect(
        method = "tick",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/entity/item/ExperienceOrbEntity;xpOrbAge:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/entity/item/ExperienceOrbEntity;xpColor:I",
                opcode = Opcodes.PUTFIELD
            ),
            to = @At(
                value = "CONSTANT",
                args = "intValue=6000"
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeAge(final ExperienceOrbEntity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake()) {
            this.xpOrbAge = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) self.getEntityWorld()).realTimeBridge$getRealTimeTicks();
        this.xpOrbAge += ticks;
    }

}

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
package net.stonebound.feelgoodtps.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import net.stonebound.feelgoodtps.RealTimeTrackingBridge;
import net.stonebound.feelgoodtps.WorldBridge;

@Mixin(Entity.class)
public abstract class EntityMixin_RealTime {

    @Shadow protected int rideCooldown;
    @Shadow public World world;
    @Shadow protected int portalCounter;
    @Shadow public int field_242273_aw;

    @Redirect(method = "baseTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;rideCooldown:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Entity;stopRiding()V"
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/entity/Entity;distanceWalkedModified:F",
                opcode = Opcodes.GETFIELD
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeEntityCooldown(final Entity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake()) {
            this.rideCooldown = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        this.rideCooldown = Math.max(0, this.rideCooldown - ticks);
    }

    @Redirect(method = "updatePortal",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;portalCounter:I",
            opcode = Opcodes.PUTFIELD, ordinal = 0
        ),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getMaxInPortalTime()I"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;func_242279_ag()V")
        )
    )
    private void realTimeImpl$adjustForRealTimePortalCounter(final Entity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake()) {
            this.portalCounter = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        this.portalCounter += ticks;
    }

    @Redirect(
        method = "decrementTimeUntilPortal",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;field_242273_aw:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void realTimeImpl$adjustForRealTimePortalCooldown(final Entity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake() || !(self instanceof ServerPlayerEntity) || FeelGoodTps.isFakePlayer((ServerPlayerEntity) (Object) this)) {
            this.field_242273_aw = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        // The initially apparent function of timeUntilPortal is a cooldown for
        // nether portals. However, there is a much more important use:
        // preventing players from being immediately sent back to the other end
        // of the portal. Since it only checks timeUntilPortal to determine
        // whether the player was just in a portal, if timeUntilPortal gets set
        // to 0, it assumes the player left and reentered the portal (see
        // Entity.setPortal()). To prevent this, "snag" the value of
        // timeUntilPortal at 1. If setPortal() does not reset it (the player
        // exits the portal), modifier will become 0, indicating that it is
        // OK to teleport the player.
        this.field_242273_aw = Math.max(modifier > 0 ? 1 : 0, this.field_242273_aw - ticks);
    }

}

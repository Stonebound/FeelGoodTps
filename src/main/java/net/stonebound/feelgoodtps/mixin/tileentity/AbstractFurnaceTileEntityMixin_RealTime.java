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
package net.stonebound.feelgoodtps.mixin.tileentity;

import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import net.stonebound.feelgoodtps.RealTimeTrackingBridge;
import net.stonebound.feelgoodtps.WorldBridge;
import net.stonebound.feelgoodtps.mixin.tileentity.TileEntityMixin;

@Mixin(value = AbstractFurnaceTileEntity.class, priority = 1001)
public abstract class AbstractFurnaceTileEntityMixin_RealTime extends TileEntityMixin {

    @Shadow private int burnTime;
    @Shadow private int cookTime;
    @Shadow private int cookTimeTotal;

    @Redirect(method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;burnTime:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;isBurning()Z",
                opcode = 1
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;world:Lnet/minecraft/world/World;",
                opcode = Opcodes.GETFIELD,
                ordinal = 0
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeBurnTime(final AbstractFurnaceTileEntity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake()) {
            this.burnTime = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        this.burnTime = Math.max(0, this.burnTime - Math.max(1, ticks - 1));
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;cookTime:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;canSmelt(Lnet/minecraft/item/crafting/IRecipe;)Z",
                ordinal = 1
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;cookTimeTotal:I",
                opcode = Opcodes.GETFIELD,
                ordinal = 0
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeCookTime(final AbstractFurnaceTileEntity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake()) {
            this.cookTime = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        this.cookTime = Math.min(this.cookTimeTotal, this.cookTime + ticks);
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;cookTime:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeCookTimeCooldown(final AbstractFurnaceTileEntity self, final int modifier) {
        if (((WorldBridge) this.world).bridge$isFake()) {
            this.cookTime = modifier;
            return;
        }
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        this.cookTime = MathHelper.clamp(this.cookTime - (2 * ticks), 0, this.cookTimeTotal);
    }

}

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
package net.stonebound.feelgoodtps.mixin.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.stonebound.feelgoodtps.WorldBridge;
import net.stonebound.feelgoodtps.RealTimeTrackingBridge;

import javax.annotation.Nullable;

@Mixin(World.class)
public abstract class WorldMixin_RealTime implements RealTimeTrackingBridge, WorldBridge {

    @Shadow @Nullable public abstract MinecraftServer shadow$getServer();

    @Shadow @Final protected ISpawnWorldInfo worldInfo;

    @Shadow @Final public boolean isRemote;

    private boolean impl$isDefinitelyFake = false;
    private boolean impl$hasChecked = false;

    @Override
    public long realTimeBridge$getRealTimeTicks() {
        if (((WorldBridge) this).bridge$isFake()) {
            return 1;
        }
        if (this.shadow$getServer() != null) {
            return ((RealTimeTrackingBridge) this.shadow$getServer()).realTimeBridge$getRealTimeTicks();
        }
        return 1;
    }

    @Override
    public boolean bridge$isFake() {
        if (this.impl$hasChecked) {
            return this.impl$isDefinitelyFake;
        }
        this.impl$isDefinitelyFake =
                this.isRemote || this.worldInfo == null;
        this.impl$hasChecked = true;
        return this.impl$isDefinitelyFake;
    }
}

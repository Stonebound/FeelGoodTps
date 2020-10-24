package net.stonebound.feelgoodtps.mixin.tileentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.tileentity.TileEntity.class)
public class TileEntityMixin {
    @Shadow protected net.minecraft.world.World world;
}

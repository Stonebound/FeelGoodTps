package net.stonebound.feelgoodtps.mixin.world.clock;

import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.ServerClockManager;
import net.stonebound.feelgoodtps.FeelGoodTps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerClockManager.class)
public abstract class ServerClockManagerMixin_RealTime {

    @Shadow private MinecraftServer server;

    @Shadow public abstract void tick();

    @Shadow public abstract ClientboundSetTimePacket createFullSyncPacket();

    @Unique private boolean feelgoodtps$catchingUp;
    @Unique private boolean feelgoodtps$needsSync;

    // Re-run the whole clock tick rather than touching the clocks directly, so pausing, clock rates and
    // the advance_time rule (and NeoForge's clock tags) behave exactly as they do for a normal tick.
    @Inject(method = "tick", at = @At("TAIL"))
    private void feelgoodtps$catchUpClocks(final CallbackInfo ci) {
        if (this.feelgoodtps$catchingUp) {
            return;
        }
        final int extra = FeelGoodTps.serverTicks() - 1;
        if (extra > 0) {
            this.feelgoodtps$catchingUp = true;
            try {
                for (int i = 0; i < extra; i++) {
                    this.tick();
                }
            } finally {
                this.feelgoodtps$catchingUp = false;
            }
            this.feelgoodtps$needsSync = true;
        }
        // Clients extrapolate clocks locally and are only resynced on join, so push the skipped time
        // to them, at most once a second.
        if (this.feelgoodtps$needsSync && this.server.getTickCount() % 20 == 0) {
            this.feelgoodtps$needsSync = false;
            this.server.getPlayerList().broadcastAll(this.createFullSyncPacket());
        }
    }
}

package net.stonebound.feelgoodtps;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public final class FeelGoodTps {
    public static final String MOD_ID = "feelgoodtps";

    /**
     * Upper bound on ticks credited per server tick, so a long stall (or resuming a paused
     * singleplayer world) doesn't fast-forward everything at once.
     */
    private static final int MAX_TICKS = 20;

    private static Predicate<Entity> fakePlayerCheck = entity -> false;

    private static long lastTickNanos;
    // Time not yet accounted for. Goes negative while vanilla replays backlogged ticks back to back,
    // so a lag spike isn't credited once by us and then a second time by vanilla catching up.
    private static long carryNanos;
    private static int realTimeTicks = 1;

    private FeelGoodTps() {
    }

    public static void init(Predicate<Entity> fakePlayerCheck) {
        FeelGoodTps.fakePlayerCheck = fakePlayerCheck;
    }

    public static void onServerTick(MinecraftServer server) {
        final long now = System.nanoTime();
        final ServerTickRateManager tickRateManager = server.tickRateManager();
        if (lastTickNanos == 0 || tickRateManager.isSprinting() || !tickRateManager.runsNormally()) {
            realTimeTicks = 1;
            carryNanos = 0;
        } else {
            final long nanosPerTick = tickRateManager.nanosecondsPerTick();
            final long elapsed = now - lastTickNanos + carryNanos;
            realTimeTicks = (int) Math.clamp(elapsed / nanosPerTick, 1, MAX_TICKS);
            carryNanos = Math.clamp(elapsed - realTimeTicks * nanosPerTick, -MAX_TICKS * nanosPerTick, nanosPerTick);
        }
        lastTickNanos = now;
    }

    /**
     * Ticks of real time that passed during the current server tick, at least 1.
     */
    public static int serverTicks() {
        return realTimeTicks;
    }

    /**
     * Ticks to add on top of the single tick vanilla already applied, always 0 on the client.
     */
    public static int extraTicks(@Nullable Level level) {
        return level instanceof ServerLevel ? realTimeTicks - 1 : 0;
    }

    public static int extraTicks(Entity entity) {
        return fakePlayerCheck.test(entity) ? 0 : extraTicks(entity.level());
    }
}

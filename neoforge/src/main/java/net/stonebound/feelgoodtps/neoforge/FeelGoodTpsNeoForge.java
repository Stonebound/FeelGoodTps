package net.stonebound.feelgoodtps.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.stonebound.feelgoodtps.FeelGoodTps;

@Mod(FeelGoodTps.MOD_ID)
public final class FeelGoodTpsNeoForge {
    public FeelGoodTpsNeoForge() {
        FeelGoodTps.init(entity -> entity instanceof FakePlayer);
    }
}

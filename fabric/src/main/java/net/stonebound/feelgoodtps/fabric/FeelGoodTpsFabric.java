package net.stonebound.feelgoodtps.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.stonebound.feelgoodtps.FeelGoodTps;

public final class FeelGoodTpsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FeelGoodTps.init(entity -> entity instanceof FakePlayer);
    }
}

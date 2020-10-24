package net.stonebound.feelgoodtps;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("feelgoodtps")
public class FeelGoodTps
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public FeelGoodTps() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    public static boolean isFakePlayer(final Entity entity) {
        return entity instanceof FakePlayer;
    }
}

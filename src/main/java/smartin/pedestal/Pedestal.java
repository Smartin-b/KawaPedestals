package smartin.pedestal;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smartin.pedestal.blocks.ModBlocks;

public class Pedestal implements ModInitializer {

    public static final String MOD_ID = "pedestal";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.registerBlocks();
    }
}

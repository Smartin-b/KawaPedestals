package smartin.pedestal.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import smartin.pedestal.blocks.ModBlocks;
import smartin.pedestal.blocks.Pedestal.PedestalBlockEntityRenderer;
import smartin.pedestal.blocks.WallHanger.WallHangerEntityRenderer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class PedestalClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlocks.PEDESTAL_BLOCK_ENTITY, PedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.WALL_HANGER_BLOCK_ENTITY, WallHangerEntityRenderer::new);
    }
}

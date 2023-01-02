package smartin.pedestal.blocks.Pedestal;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import smartin.pedestal.ModTags;
import smartin.pedestal.Pedestal;
import smartin.pedestal.dataReader.SwordJson;

@Environment(EnvType.CLIENT)
public class PedestalBlockEntityRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

    public PedestalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(PedestalBlockEntity tile, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack sword = tile.getWeapon();
        if (!sword.isEmpty()) {
            this.renderItem(tile, sword, tickDelta, matrices, vertexConsumers, light, overlay);
        }
    }

    public void renderItem(PedestalBlockEntity tile, ItemStack stack, float partialTicks,  MatrixStack matrix, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        matrix.push();
        if(stack.isIn(ModTags.SMALL_SWORD))
            matrix.translate(0.0D, -0.3D, 0.0D);
        if(stack.isIn(ModTags.BIG_SWORD))
            matrix.translate(0.0D, 0.35D, 0.0D);
        matrix.translate(0.5D, 0.6D, 0.5D);
        matrix.scale(0.80F, 0.80F, 0.80F);
        SwordJson.TranslationObject translationObject = Pedestal.SwordJsons.get("pedestal").get(Registry.ITEM.getId(stack.getItem()).toString());
        if(translationObject !=null){
            matrix.translate(translationObject.translation[0],translationObject.translation[1],translationObject.translation[2]);
            matrix.scale(translationObject.scale[0].floatValue(),translationObject.scale[1].floatValue(),translationObject.scale[2].floatValue());
            this.rotateItem(matrix,translationObject.rotation[0].floatValue(),translationObject.rotation[1].floatValue(),translationObject.rotation[2].floatValue());
        }
        switch (tile.getCachedState().get(HorizontalFacingBlock.FACING)) {
            case WEST, EAST -> this.rotateItem(matrix, 180, 90f, -45f); // default values
            case NORTH, SOUTH -> this.rotateItem(matrix, 180, 180f, -45f);
        }
        renderer.renderItem(stack, ModelTransformation.Mode.FIXED, combinedLight, combinedOverlay, matrix, buffer, 1);

        matrix.pop();
    }

    private void rotateItem(MatrixStack matrix, float a, float b, float c) {
        matrix.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(a,b,c)));
    }
}

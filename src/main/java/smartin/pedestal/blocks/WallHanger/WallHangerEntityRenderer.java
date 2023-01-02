package smartin.pedestal.blocks.WallHanger;

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
public class WallHangerEntityRenderer implements BlockEntityRenderer<WallHangerEntity> {

    public WallHangerEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(WallHangerEntity tile, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack sword = tile.getWeapon();
        if (!sword.isEmpty()) {
            this.renderItem(tile, sword, tickDelta, matrices, vertexConsumers, light, overlay);
        }
    }

    public void renderItem(WallHangerEntity tile, ItemStack stack, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        matrix.push();
        switch (tile.getCachedState().get(HorizontalFacingBlock.FACING)) {
            case WEST :
                matrix.translate(15D/16D, 0.65D, 0.5D);
                this.rotateItem(matrix, 180, 90f, -45f);
                break;
            case EAST :
                matrix.translate(1D/16D, 0.65D, 0.5D);
                this.rotateItem(matrix, 180, 90f, -45f);
                break;
            case NORTH :
                matrix.translate(0.5D, 0.65D, 15d/16D);
                this.rotateItem(matrix, 180, 180f, -45f);
                break;
            case SOUTH :
                matrix.translate(0.5D, 0.65D, 1d/16D);
                this.rotateItem(matrix, 180, 180f, -45f);
                break;
        }
        matrix.scale(0.80F, 0.80F, 0.80F);
        if(stack.isIn(ModTags.INVERTED_SWORD))
            this.rotateItem(matrix,0,0,180);
        SwordJson.TranslationObject translationObject = Pedestal.SwordJsons.get("hanger").get(Registry.ITEM.getId(stack.getItem()).toString());
        if(translationObject !=null){
            matrix.translate(translationObject.translation[0],translationObject.translation[1],translationObject.translation[2]);
            matrix.scale(translationObject.scale[0].floatValue(),translationObject.scale[1].floatValue(),translationObject.scale[2].floatValue());
            this.rotateItem(matrix,translationObject.rotation[0].floatValue(),translationObject.rotation[1].floatValue(),translationObject.rotation[2].floatValue());
        }
        renderer.renderItem(stack, ModelTransformation.Mode.FIXED, combinedLight, combinedOverlay, matrix, buffer, 1);

        matrix.pop();
    }

    private void rotateItem(MatrixStack matrix, float a, float b, float c) {
        matrix.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(a,b,c)));
    }
}

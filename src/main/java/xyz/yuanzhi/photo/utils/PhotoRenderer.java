package xyz.yuanzhi.photo.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import xyz.yuanzhi.photo.block.PhotoEntity;
import xyz.yuanzhi.photo.gui.PhotoPosition;

@Environment(EnvType.CLIENT)
public class PhotoRenderer implements BlockEntityRenderer<PhotoEntity> {

    public PhotoRenderer(BlockEntityRendererFactory.Context context){
    }

    @Override
    public void render(PhotoEntity photoEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        String url = photoEntity.getUrl().trim();

        if (url.equals("")) {
            return;
        }

        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "https://" + url;
        }
        if (!url.endsWith(".png") && !url.endsWith(".jpg") && !url.contains(".jpeg")) return;


        float width = photoEntity.getWidth();
        float height = photoEntity.getHeight();
        float x = photoEntity.getXOffset();
        float y = photoEntity.getYOffset() - getYOffset(photoEntity.getPositionType(), height);;
        float z = photoEntity.getZOffset();

        // Download the picture data
        PictureDownloader.PictureData data = PictureDownloader.getInstance().getPicture(url);
        if (data == null || data.identifier == null) {
            return;
        }

        float xOffset = 0.0F;
        float zOffset = 0.0F;

        Quaternion yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(0F);

        if (photoEntity.getCachedState().contains(Properties.HORIZONTAL_FACING)) {
            Direction direction = photoEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
            switch (direction) {
                case NORTH -> {
                    zOffset = 1.01F;
                    xOffset = 1.0F;
                    yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F);
                    x += getXZOffset(photoEntity.getPositionType(), width);
                }
                case SOUTH -> {
                    zOffset = 0.010F;
                    x -= getXZOffset(photoEntity.getPositionType(), width);
                }
                case EAST -> {
                    zOffset = 1.01F;
                    yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F);
                    z += getXZOffset(photoEntity.getPositionType(), width);
                }
                case WEST -> {
                    yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F);
                    xOffset = 1.01F;
                    z -= getXZOffset(photoEntity.getPositionType(), width);
                }
            }
        }


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        matrixStack.push();

        int l;
        if (FabricLoader.getInstance().isModLoaded("iris")) {
            RenderSystem.setShader(GameRenderer::getRenderTypeCutoutShader);
            l = 230;
        }
        else {
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            l = light;
        }
        RenderSystem.setShaderTexture(0, data.identifier);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.translate(xOffset + x, 0.00F + y, zOffset + z);
        matrixStack.multiply(yRotation);

        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);

        buffer.vertex(matrix4f, width, 0.0F, 1.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(l).overlay(overlay)
                .next();

        buffer.vertex(matrix4f, width, height, 1.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(l).overlay(overlay)
                .next();

        buffer.vertex(matrix4f, 0.0F, height, 1.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(l).overlay(overlay)
                .next();

        buffer.vertex(matrix4f, 0.0F, 0.0F, 1.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(l).overlay(overlay)
                .next();

        tessellator.draw();
        matrixStack.pop();


        RenderSystem.disableDepthTest();
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    private static float getXZOffset(int positionType, float width) {
        PhotoPosition position = PhotoPosition.byId(positionType);
        return switch (position) {
            case RIGHT_BOTTOM, RIGHT_CENTER, RIGHT_TOP -> width - 1;
            case CENTER, CENTER_BOTTOM, CENTER_TOP -> width / 2 - 0.5f;
            default -> 0;
        };
    }

    private static float getYOffset(int positionType, float height) {
        PhotoPosition position = PhotoPosition.byId(positionType);
        return switch (position) {
            case LEFT_TOP, CENTER_TOP, RIGHT_TOP -> height - 1;
            case LEFT_CENTER, CENTER, RIGHT_CENTER -> height / 2 - 0.5f;
            default -> 0;
        };
    }

}
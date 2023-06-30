package xyz.yuanzhi.photo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import xyz.yuanzhi.photo.utils.PhotoRenderer;

@Environment(EnvType.CLIENT)
public class ClientInit implements ClientModInitializer {



    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(Photo.PHOTO_ENTITY, PhotoRenderer::new);
    }
}

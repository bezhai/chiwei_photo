package xyz.yuanzhi.photo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import xyz.yuanzhi.photo.utils.PhotoRenderer;

@Environment(EnvType.CLIENT)
public class ClientInit implements ClientModInitializer {



    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(Photo.PHOTO_ENTITY, PhotoRenderer::new);
    }
}

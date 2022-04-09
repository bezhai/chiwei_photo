package xyz.yuanzhi.photo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yuanzhi.photo.block.PhotoBlock;
import xyz.yuanzhi.photo.block.PhotoEntity;

public class Photo implements ModInitializer {

    public static final String MODID = "photo";
    public static final Identifier PHOTO = new Identifier(MODID, "photo");
    public static final Logger LOGGER = LoggerFactory.getLogger("photo");


    // BlockEntity
    public static BlockEntityType<PhotoEntity> PHOTO_ENTITY;

    // Block
    public static final PhotoBlock PHOTO_BLOCK = new PhotoBlock(FabricBlockSettings.of(Material.WOOD)
            .hardness(0.5f).sounds(BlockSoundGroup.WOOD));


    @Override
    public void onInitialize() {

        Registry.register(Registry.BLOCK, PHOTO, PHOTO_BLOCK);

        Registry.register(Registry.ITEM, PHOTO, new BlockItem(PHOTO_BLOCK, new Item.Settings().group(ItemGroup.TOOLS)));

        PHOTO_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "photo:photo_entity",
                FabricBlockEntityTypeBuilder.create(PhotoEntity::new, PHOTO_BLOCK).build(null));

    }

    public static void registryItem(String name, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
    }


}

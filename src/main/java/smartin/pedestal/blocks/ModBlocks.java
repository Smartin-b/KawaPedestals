package smartin.pedestal.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import smartin.pedestal.Pedestal;
import smartin.pedestal.blocks.Pedestal.PedestalBlock;
import smartin.pedestal.blocks.Pedestal.PedestalBlockEntity;
import smartin.pedestal.blocks.WallHanger.WallHanger;
import smartin.pedestal.blocks.WallHanger.WallHangerEntity;


public class ModBlocks {

    public static final Block PedestalBlock = registerBlock("pedestal", new PedestalBlock(),ItemGroup.MISC);
    public static final Block WallHangerBlock = registerBlock("wall_hanger", new WallHanger(),ItemGroup.MISC);

    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Pedestal.MOD_ID, "pedestal_block_entity"),
            FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, PedestalBlock).build()
    );
    public static final BlockEntityType<WallHangerEntity> WALL_HANGER_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Pedestal.MOD_ID, "wall_hanger_entity"),
            FabricBlockEntityTypeBuilder.create(WallHangerEntity::new, WallHangerBlock).build()
    );

    private static Block registerBlock(String name, Block block, ItemGroup group){
        registerBlockItem(name,block,group);
        return Registry.register(Registry.BLOCK,new Identifier(Pedestal.MOD_ID,name),block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group){
        return Registry.register(Registry.ITEM,new Identifier(Pedestal.MOD_ID,name), new BlockItem(block,new FabricItemSettings().group(group)));
    }

    public static void registerBlocks() {
        Pedestal.LOGGER.info("registering Blocks"+ Pedestal.MOD_ID);
    }
}

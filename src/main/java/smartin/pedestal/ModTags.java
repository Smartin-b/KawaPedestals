package smartin.pedestal;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModTags {
    public static final TagKey<Item> BIG_SWORD = TagKey.of(Registry.ITEM_KEY, new Identifier(Pedestal.MOD_ID, "big_sword"));
    public static final TagKey<Item> NORMAL_SWORD = TagKey.of(Registry.ITEM_KEY, new Identifier(Pedestal.MOD_ID, "normal_sword"));
    public static final TagKey<Item> SMALL_SWORD = TagKey.of(Registry.ITEM_KEY, new Identifier(Pedestal.MOD_ID, "small_sword"));
    public static final TagKey<Item> INVERTED_SWORD = TagKey.of(Registry.ITEM_KEY, new Identifier(Pedestal.MOD_ID, "inverted_sword"));
    public static final TagKey<Item> WALL_HANGER = TagKey.of(Registry.ITEM_KEY, new Identifier(Pedestal.MOD_ID, "wall_hanger"));
}

package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemWrench extends Item {

    public ItemWrench() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).durability(1024));
        setRegistryName(new ResourceLocation(Main.MODID, "wrench"));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}

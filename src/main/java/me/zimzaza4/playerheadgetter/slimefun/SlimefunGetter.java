package me.zimzaza4.playerheadgetter.slimefun;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SlimefunGetter {
    public static void getSkull(Set<String> textures, Player p) {
        List<ItemStack> items = new ArrayList<>();
        for (SlimefunItem item : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (item.getItem().getItemMeta() instanceof SkullMeta) {
                items.add(item.getItem());
            }
        }
        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
            if (group.getItem(p).getItemMeta() instanceof SkullMeta) {
                items.add(group.getItem(p));
            }
        }
        for (HeadTexture texture : HeadTexture.values()) {
            items.add(texture.getAsItemStack());
        }
        for (ItemStack item : items) {

            NBTContainer nbt = NBTItem.convertItemtoNBT(item);
            if (nbt.getCompound("tag").hasTag("SkullOwner") && nbt.getCompound("tag").getCompound("SkullOwner").hasTag("Properties")) {
                NBTCompound o = nbt.getCompound("tag").getCompound("SkullOwner").getCompound("Properties");

                o.getCompoundList("textures").forEach(texture -> textures.add(texture.getString("Value")));
            }

        }
    }
}

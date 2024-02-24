package me.zimzaza4.playerheadgetter;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.zimzaza4.playerheadgetter.slimefun.SlimefunGetter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkullCommand implements CommandExecutor {
    public static final Set<String> textures = new HashSet<>();
    public static final Set<Player> users = new HashSet<>();
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("skull.get")) {
            if (!(sender instanceof Player)) {
                return false;
            }
            switch (args[0]) {
                case "slimefun":
                    if (Bukkit.getPluginManager().getPlugin("Slimefun") != null) {
                        SlimefunGetter.getSkull(textures, (Player) sender);
                    }
                    break;

                case "hand":
                    if (((Player) sender).getInventory().getItemInMainHand().getItemMeta() instanceof SkullMeta) {
                        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();

                        NBTContainer nbt = NBTItem.convertItemtoNBT(item);
                        if (nbt.getCompound("tag").hasTag("SkullOwner") && nbt.getCompound("tag").getCompound("SkullOwner").hasTag("Properties")) {
                            NBTCompound p = nbt.getCompound("tag").getCompound("SkullOwner").getCompound("Properties");

                            p.getCompoundList("textures").forEach(texture -> {
                                textures.add(texture.getString("Value"));
                            });
                        }
                    }
                    break;
                case "save":
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            File f = new File(PlayerHeadGetter.plugin.getDataFolder(), "heads.yml");
                            PlayerHeadGetter.plugin.getDataFolder().mkdirs();
                            if (!f.exists()) {
                                try {
                                    f.createNewFile();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                            FileConfiguration cfg = new YamlConfiguration();
                            List<String> strings = new ArrayList<>(textures);
                            cfg.set("textures", strings);
                            try {
                                cfg.save(f);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }.runTaskAsynchronously(PlayerHeadGetter.plugin);
                    break;
                case "inventory":
                    Player player = (Player) sender;
                    if (users.contains(player)) {
                        users.remove(player);
                        sender.sendMessage("OFF");
                    } else {
                        sender.sendMessage("ON");
                        users.add(player);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!users.contains(player)) {
                                    this.cancel();
                                }
                                if (!player.isOnline()) {
                                    this.cancel();
                                }


                                for (ItemStack item : player.getOpenInventory().getTopInventory()) {

                                    NBTContainer nbt = NBTItem.convertItemtoNBT(item);
                                    if (nbt.hasTag("tag") && nbt.getCompound("tag").hasTag("SkullOwner") && nbt.getCompound("tag").getCompound("SkullOwner").hasTag("Properties")) {
                                        NBTCompound p = nbt.getCompound("tag").getCompound("SkullOwner").getCompound("Properties");

                                        p.getCompoundList("textures").forEach(texture -> textures.add(texture.getString("Value")));
                                    }
                                }
                            }
                        }.runTaskTimer(PlayerHeadGetter.plugin, 10, 10);

                    }

                    break;
            }
        }
        return true;

    }
}

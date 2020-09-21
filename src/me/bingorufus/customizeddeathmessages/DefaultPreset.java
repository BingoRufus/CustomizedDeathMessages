package me.bingorufus.customizeddeathmessages;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.bingorufus.customizeddeathmessages.utils.ItemStackReader;

public class DefaultPreset extends Preset {
	CustomizedDeathMessages m;
	ItemStack icon = null;

	public DefaultPreset(String name, CustomizedDeathMessages m) {
		super(name);
		this.m = m;
		icon = getIcon();
	}

	public ItemStack getIcon() {
		if (icon == null)
			icon = createIcon();
		return this.icon;
	}


	public ItemStack createIcon() {
		if (!m.getConfig().isConfigurationSection("default-icon")) {
			Bukkit.getLogger().info("The default preset icon is missing");
		} else {
		try {
				ConfigurationSection iconconfig = m.getConfig().getConfigurationSection("default-icon");
			String material = iconconfig.getString("material");
			int amt = iconconfig.getInt("amount");
			String iconname = iconconfig.getString("name");
			List<String> lore = iconconfig.getStringList("lore");
				return new ItemStackReader().read(material, amt, iconname, lore);

		} catch (NullPointerException e) {
			e.printStackTrace();
			Bukkit.getLogger().severe("The default icon is missing required fields");
		}
		}
		ItemStack item = new ItemStack(Material.GRASS_BLOCK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Default");
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Just the default death messages",
				ChatColor.GRAY + "Click me to select this preset"));
		item.setItemMeta(meta);
		return item;
	}



}

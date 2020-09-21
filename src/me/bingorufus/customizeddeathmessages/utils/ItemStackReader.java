package me.bingorufus.customizeddeathmessages.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackReader {
	public ItemStack read(String material, Integer amount, String name, List<String> lore) {
		material = material.toUpperCase().replaceAll(" ", "_");
		if (amount <= 0)
			amount = 1;
		if (amount > 64)
			amount = 64;
		Material m = Material.STONE;

		if (Material.matchMaterial(material.toUpperCase()) == null) {
			Bukkit.getLogger()
					.severe(ChatColor.RED + "The material (" + material + ") for the default icon does not exist");

		} else {
			m = Material.matchMaterial(material);
		}
		ItemStack item = new ItemStack(m, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		List<String> coloredlore = new ArrayList<>();
		lore.forEach(str -> {
			coloredlore.add(ChatColor.translateAlternateColorCodes('&', str));
		});

		meta.setLore(coloredlore);
		item.setItemMeta(meta);
		return item;

	}
}

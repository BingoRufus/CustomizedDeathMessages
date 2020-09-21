package me.bingorufus.customizeddeathmessages.executors;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;
import me.bingorufus.customizeddeathmessages.DefaultPreset;

public class DeathMessageSelector implements CommandExecutor {


	ItemStack[] icons;
	int rows = 1;
	CustomizedDeathMessages m;
	String title;
	public DeathMessageSelector(CustomizedDeathMessages m) {
		title = ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("selector-title"));
		this.m = m;
		List<ItemStack> icons = new ArrayList<ItemStack>();

		if (m.defaultPreset instanceof DefaultPreset) {
			DefaultPreset dp = (DefaultPreset) m.defaultPreset;
			ItemStack defaultIcon = dp.getIcon();
			icons.add(defaultIcon);
		}


		m.presets.values().forEach(preset -> {
			if (preset instanceof DefaultPreset)
				return;
			ItemStack item = preset.getIcon();
			icons.add(item);
		});

		this.icons = new ItemStack[icons.size() <= 54 ? icons.size() : 54];
		for (int i = 0; i < (icons.size() <= 54 ? icons.size() : 54); i++) {
			this.icons[i] = icons.get(i);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can do this");
			return true;
		}
		Player p = (Player) sender;
		Player selected = p;
		m.selecting.put(p, p.getUniqueId());
		if (p.hasPermission("cdm.other") && args.length > 0 && Bukkit.getPlayer(args[0]) != null) {
			selected = Bukkit.getPlayer(args[0]);
			m.selecting.put(p, selected.getUniqueId());
		}

		rows = (icons.length / 9) + 1;
		String playertitle = title;

		try {
			playertitle = title.replaceAll("%player%", selected.getName());
		} catch (NullPointerException e) {

		}
		Inventory inv = Bukkit.createInventory(null, rows * 9 <= 54 ? rows * 9 : 54, playertitle);
		inv.setContents(icons);
		m.selectorinvs.add(inv);
		p.openInventory(inv);
		return true;
	}



}

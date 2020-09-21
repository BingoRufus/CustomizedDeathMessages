package me.bingorufus.customizeddeathmessages.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;
import me.bingorufus.customizeddeathmessages.Preset;

public class PresetSelector implements Listener {
	CustomizedDeathMessages m;
	String permissionDenied;
	String changeSelf;
	String changeOther;
	public PresetSelector(CustomizedDeathMessages m) {
		permissionDenied = ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("permission-denied"));
		changeOther = ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("change-other"));
		changeSelf = ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("change-self"));

		this.m = m;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!m.selectorinvs.contains(e.getInventory()))
			return;
		if (e.getClickedInventory() == null)
			return;
		if (e.getCurrentItem() == null)
			return;
		if (e.getCurrentItem().getItemMeta() == null)
			return;
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		Preset selectedPreset = m.icons.get(e.getCurrentItem());
		UUID selected = m.selecting.get(p);

		if (!p.getUniqueId().equals(selected) && p.hasPermission("cdm.other")) {
			m.selected.put(selected, selectedPreset);
			p.closeInventory();
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 100, 1);

			p.sendMessage(changeOther.replaceAll("%player%", Bukkit.getOfflinePlayer(selected).getName())
					.replaceAll("%preset%", selectedPreset.getName()));

			return;
		}
		if (!(selectedPreset == null) && !p.hasPermission(selectedPreset.getPermissionString())) {
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 100, 1);
			p.sendMessage(permissionDenied);
			p.closeInventory();

			return;
		}
		m.selected.put(selected, selectedPreset);
		p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 100, 1);
		p.sendMessage(changeSelf.replaceAll("%preset%", selectedPreset.getName()));
		p.closeInventory();
	}

}

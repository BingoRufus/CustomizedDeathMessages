package me.bingorufus.customizeddeathmessages.executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;

public class Reloader implements CommandExecutor {
	CustomizedDeathMessages m;
	String permissionDenied;
	public Reloader(CustomizedDeathMessages m) {
		this.m = m;
		permissionDenied = ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("permission-denied"));

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("cdm.reload")) {
			m.saveSelected();
			m.configInitialize();
			sender.sendMessage(ChatColor.GREEN + "CustomizedDeathMessages Reloaded");
			return true;

		}
		sender.sendMessage(permissionDenied);
		return true;
		
}
}

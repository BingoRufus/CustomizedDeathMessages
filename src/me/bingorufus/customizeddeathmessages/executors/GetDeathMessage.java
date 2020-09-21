package me.bingorufus.customizeddeathmessages.executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;
import me.bingorufus.customizeddeathmessages.Preset;

public class GetDeathMessage implements CommandExecutor {

	CustomizedDeathMessages m;
	String getSelf;
	String getOther;
	public GetDeathMessage(CustomizedDeathMessages m) {

		this.m = m;
		getOther = ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("get-other"));
		getSelf = ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("get-self"));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && (args.length == 0 || Bukkit.getPlayer(args[0]) == null)) {
			sender.sendMessage("You cannot do that to yourself! do /gdm <Player>");
			return true;
		}
		if (args.length > 0 && Bukkit.getPlayer(args[0]) != null) {
			Preset preset = m.getPreset(Bukkit.getPlayer(args[0]).getUniqueId());
			sender.sendMessage(getOther.replaceAll("%player%", Bukkit.getPlayer(args[0]).getName())
					.replaceAll("%preset%", preset.getName()));

			return true;

		}

		Player p = (Player) sender;
		Preset preset = m.getPreset(p.getUniqueId());


		p.sendMessage(getSelf.replaceAll("%preset%", preset.getName()));

		return true;
	}
}

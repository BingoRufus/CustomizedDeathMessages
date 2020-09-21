package me.bingorufus.customizeddeathmessages.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;
import me.bingorufus.customizeddeathmessages.Preset;

public class PresetLoader {
	List<String> damageCauses = Arrays.asList("block_explosion", "contact", "cramming", "custom", "dragon_breath",
			"drowning",
			"entity_attack", "entity_explosion", "entity_sweep_attack", "fall", "falling_block", "fire", "fire_tick",
			"fly_into_wall", "hot_floor", "lava", "lightning", "magic", "projectile", "starvation",
			"suffocation", "suicide", "thorns", "void", "wither");

	private CustomizedDeathMessages m;

	public PresetLoader(CustomizedDeathMessages m) {
		this.m = m;
		Arrays.asList(m.getDataFolder().listFiles()).forEach(file -> {
			if (!file.isDirectory())
				return;
			loadPreset(file);
		});
	}

	public void loadPreset(File presetFolder) {

		File presetfile = new File(presetFolder, "preset.yml");
		if (!presetfile.exists()) {
			Bukkit.getLogger()
					.severe(presetFolder.getName() + " was unable to be loaded as the preset.yml does not exist");
			return;
		}

		YamlConfiguration presetyml = YamlConfiguration.loadConfiguration(presetfile);
		if (!presetyml.contains("name")) {
			Bukkit.getLogger().severe(presetFolder.getName()
					+ " was unable to be loaded as preset.yml does not contain a name");
			return;
		}
		String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', presetyml.getString("name")));
		if (!presetyml.contains("enabled")) {
			Bukkit.getLogger().severe(name
					+ " was unable to be loaded as the preset.yml does not contain the required sections");
			return;
		}
		if (!presetyml.getBoolean("enabled")) {
			Bukkit.getLogger().info(name + " was not loaded as it is disabled in the preset.yml");
			return;
		}
		if (!presetyml.contains("permission")) {
			Bukkit.getLogger().info(name + " was not loaded as it is missing a permission in the preset.yml");
			return;

		}
		if (!new File(presetFolder, "default.yml").exists()) {
			Bukkit.getLogger().info(name + " was not loaded as it is missing a default.yml");
			return;
		}



		Preset preset = new Preset(ChatColor.translateAlternateColorCodes('&', presetyml.getString("name")));

		try {
		if (presetyml.isConfigurationSection("icon")) {
			ConfigurationSection iconconfig = presetyml.getConfigurationSection("icon");
			String material = iconconfig.getString("material");
			int amt = iconconfig.getInt("amount");
			String iconname = iconconfig.getString("name");
				List<String> lore = iconconfig.getStringList("lore");
				preset.setIcon(new ItemStackReader().read(material, amt, iconname, lore));
		}
		} catch (NullPointerException e) {
			Bukkit.getLogger().severe("The icon for " + name + " is missing required fields");
		}
		preset.setPermission(presetyml.getString("permission"));

		for (File file : presetFolder.listFiles()) {
			if (!file.getName().substring(file.getName().length() - 4, file.getName().length()).equals(".yml"))
				continue;
			if (file.getName().equals("preset.yml"))
				continue;
				YamlConfiguration mobPreset = YamlConfiguration.loadConfiguration(file);
			if (mobPreset != null) {
				if (!loadMob(preset, file)) {
					return;
				}

			}

		}
		if (preset.registerPermission()) {
			m.presetPerms.add(preset.getPermission());
			m.presets.put(name.toLowerCase(), preset);
			m.icons.put(preset.getIcon(), preset);
			Bukkit.getLogger().info(name + " was successfully loaded");
		}

	}

	public boolean loadMob(Preset preset, File mobPreset) {
		YamlConfiguration mob = YamlConfiguration.loadConfiguration(mobPreset);
		String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
				mobPreset.getName().substring(0, mobPreset.getName().length() - 4)));

		if (name.toLowerCase().equals("default")) {
			List<String> missing = new ArrayList<>();
			Set<String> keys = mob.getKeys(true);
			damageCauses.forEach(cause -> {
				if (!keys.contains(cause))
					missing.add(cause);

			});
			if (missing.size() > 0) {
			String missinglist = "";
			for (String missed : missing) {

					if (missinglist.length() != 0)
						missinglist += ", ";
					missinglist += "\n";
					if (missing.indexOf(missed) == missing.size() - 1 && missing.size() > 1
							&& missing.indexOf(missed) != -1)
					missinglist += "and ";
				missinglist += missed;
			}
				Bukkit.getLogger().info(preset.getName()
						+ " was not loaded as it is missing the following items in the default.yml: " + missinglist);
				return false;
			}
			keys.forEach(key -> {
				if (!key.equals("default")) {
				preset.addMessage("default" + key.toLowerCase(), mob.getString(key));
				} else {
					preset.addMessage(key.toLowerCase(), mob.getString(key));
				}

			});
			return true;
		}

		mob.getKeys(true).forEach(key -> {
			preset.addMessage(name.toLowerCase().toLowerCase().replaceAll("_", " ").replaceAll(" ", "") + key,
					mob.getString(key));
		});

		return true;
	}


}

package me.bingorufus.customizeddeathmessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.bingorufus.customizeddeathmessages.executors.DeathMessageSelector;
import me.bingorufus.customizeddeathmessages.executors.GetDeathMessage;
import me.bingorufus.customizeddeathmessages.executors.Reloader;
import me.bingorufus.customizeddeathmessages.listeners.PlayerDeathListener;
import me.bingorufus.customizeddeathmessages.listeners.PresetSelector;
import me.bingorufus.customizeddeathmessages.utils.DataManager;
import me.bingorufus.customizeddeathmessages.utils.ExampleSaver;
import me.bingorufus.customizeddeathmessages.utils.PresetLoader;

public class CustomizedDeathMessages extends JavaPlugin {
	public List<Inventory> selectorinvs = new ArrayList<>();
	public HashMap<String, Preset> presets = new HashMap<>();
	public HashMap<UUID, Preset> selected = new HashMap<>();
	public HashMap<ItemStack, Preset> icons = new HashMap<>();
	public HashMap<Player, UUID> selecting = new HashMap<>();
	public Preset defaultPreset = null;
	public List<Permission> presetPerms = new ArrayList<>();

	@Override
	public void onEnable() {
		Bukkit.getWorlds().forEach(w -> {
			w.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
		});

		this.saveDefaultConfig();
		configInitialize();

	}

	@Override
	public void onDisable() {
		saveSelected();

	}

	public void loadSelected() {
		DataManager dm = new DataManager(this, "selected.yml");
		FileConfiguration savedData = dm.getConfig();
		dm.reloadConfig();
		if (savedData.getKeys(false).size() == 0 || presets.isEmpty())
			return;
		savedData.getKeys(false).forEach(key -> {
			String preset = (String) savedData.getConfigurationSection(key).get("preset");
			if (preset == null)
				return;
			if (presets.containsKey(ChatColor.stripColor(preset.toLowerCase())))
				selected.put(UUID.fromString(key), presets.get(ChatColor.stripColor(preset.toLowerCase())));
		});
	}

	public void saveSelected() {
		DataManager dm = new DataManager(this, "selected.yml");
		FileConfiguration savedData = dm.getConfig();

		selected.keySet().forEach(uuid -> {
			if (!savedData.isConfigurationSection(uuid.toString())) {
				savedData.createSection(uuid.toString());
			}

			savedData.getConfigurationSection(uuid.toString()).set("preset",
					selected.get(uuid) == null ? null : ChatColor.stripColor(selected.get(uuid).getName()));
		});
		dm.saveConfig();

	}

	public Preset getPreset(UUID player) {
		Preset pre = selected.get(player);
		if (pre == null)
			return defaultPreset;
		return pre;
	}



	public void configInitialize() {
		if (!selected.isEmpty()) {
			saveSelected();
		}
		HandlerList.unregisterAll(this);
		this.saveDefaultConfig();
		this.reloadConfig();
		presets.clear();
		icons.clear();
		selected.clear();

		new ExampleSaver(this);


		PluginManager pm = getServer().getPluginManager();
		presetPerms.forEach(perm -> {
			pm.removePermission(perm);

		});

		new PresetLoader(this);


		defaultPreset = createDefault();



		getCommand("customizeddeathmessagesreload").setExecutor(new Reloader(this));
		getCommand("sdm").setExecutor(new DeathMessageSelector(this));
		getCommand("gdm").setExecutor(new GetDeathMessage(this));

		Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PresetSelector(this), this);
		loadSelected();

	}

	public Preset createDefault() {
		if (presets.keySet().contains("default")) {
			Bukkit.getLogger()
					.info("A preset with the name \"" + presets.get("default").getName()
							+ "\" already exists, so it will be the default preset");

			return presets.get("default");
		}
		if (getConfig().contains("default-preset")
				&& presets.get(ChatColor.stripColor(getConfig().getString("default-preset")).toLowerCase()) != null) {
			Preset def = presets.get(ChatColor.stripColor(getConfig().getString("default-preset").toLowerCase()));
			if (def != null) {
				return def;
			}
		}
		Bukkit.getLogger().info("The preset " + getConfig().getString("default-preset")
				+ " does not exist, default messages will be used as a default preset");

		DefaultPreset dp = new DefaultPreset("Default", this);
		presets.put("default", dp);
		icons.put(dp.getIcon(), dp);
		return dp;

	}

}

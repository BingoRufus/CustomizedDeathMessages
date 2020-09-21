package me.bingorufus.customizeddeathmessages.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;

public class DataManager {

	private CustomizedDeathMessages m;
	private FileConfiguration dataCfg = null;
	private File cfgFile = null;
	private String filename = "";

	public DataManager(CustomizedDeathMessages m, String filename) {
		this.m = m;
		this.filename = filename;
		saveDefaultConfig();
	}

	public void reloadConfig() {
		if (this.cfgFile == null)
			this.cfgFile = new File(this.m.getDataFolder(), this.filename);
		new YamlConfiguration();
		this.dataCfg = YamlConfiguration.loadConfiguration(this.cfgFile);

		InputStream defaultStream = this.m.getResource(this.filename);
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataCfg.setDefaults(defaultConfig);

		}
	}

	public FileConfiguration getConfig() {
		if (this.dataCfg == null)
			reloadConfig();
		return this.dataCfg;
	}

	public void saveConfig() {
		if (this.dataCfg == null || this.cfgFile == null)
			return;
		try {
			this.getConfig().save(this.cfgFile);
		} catch (IOException e) {
			m.getLogger().log(Level.SEVERE, "Could not save config to " + this.cfgFile, e);
		}
	}

	public void saveDefaultConfig() {
		if (this.cfgFile == null)
			this.cfgFile = new File(this.m.getDataFolder(), this.filename);

		if (!this.cfgFile.exists())
			this.m.saveResource(this.filename, false);
	}
}

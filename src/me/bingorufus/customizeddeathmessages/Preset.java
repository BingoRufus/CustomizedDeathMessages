package me.bingorufus.customizeddeathmessages;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Preset {
	private HashMap<String, String> preset = new HashMap<String, String>();
	private ItemStack icon = new ItemStack(Material.STONE);
	private String name = "";
	private String permission = "";
	boolean isDefault = false;

	public Preset(String name) {
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		ItemMeta iconmeta =icon.getItemMeta();
		iconmeta.setDisplayName("This icon has not been named properly in the preset.yml");
		icon.setItemMeta(iconmeta);
	}

	public String getName() {
		return name;
	}

	public void setIcon(ItemStack item) {
		icon = item;

	}

	public void setPermission(String permission) {
		this.permission = "cdm.presets." + permission;
	}


	public String getPermissionString() {
		return this.permission;
	}

	public Permission getPermission() {
		return Bukkit.getPluginManager().getPermission(getPermissionString());
	}
	public void addMessage(String cause, String message) {
		preset.put(cause, message);
	}

	public String getMessage(EntityType e, DamageCause cause) {

		String damagekey = e != null ? e.getKey().getKey().toLowerCase().replaceAll("_", "") : "default";
		String presetmsg = preset.get(damagekey + cause.name().toLowerCase());
		presetmsg = presetmsg == null ? preset.get("default" + cause.name().toLowerCase()) : presetmsg;
		presetmsg = presetmsg == null ? preset.get("default") : presetmsg;
		presetmsg = presetmsg == null ? "%player% died" : presetmsg;
		return presetmsg;
	}

	public boolean registerPermission() {
		try {
			Permission parent = Bukkit.getPluginManager().getPermission("cdm.presets.*");
		Permission perm = new Permission(permission, PermissionDefault.OP);
		perm.addParent(parent, true);
		Bukkit.getPluginManager().addPermission(perm);
			return true;
		} catch (Exception e) {
			Bukkit.getLogger().severe(getName() + " has been disabled as the permission node " + permission
					+ " already exists elsewhere");
			return false;
		}
	}



	public ItemStack getIcon() {
		return icon;

	}

}

package me.bingorufus.customizeddeathmessages.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;
import me.bingorufus.customizeddeathmessages.DefaultPreset;
import me.bingorufus.customizeddeathmessages.Preset;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerDeathListener implements Listener {
	private CustomizedDeathMessages m;

	public PlayerDeathListener(CustomizedDeathMessages m) {
		this.m = m;

	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();



		String deathmessage = "";
		EntityType damager = null;
		EntityDamageEvent damageEvent = p.getLastDamageCause();
		DamageCause cause = damageEvent.getCause();
		Preset preset = m.getPreset(p.getUniqueId());
		String killer = null;
		if ((m.getPreset(p.getUniqueId()) instanceof DefaultPreset)
				&& !(p.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			broadcastComponent(e.getDeathMessage(), e.getDeathMessage());
			return;
		}

		if (damageEvent instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent ede = (EntityDamageByEntityEvent) damageEvent;
			damager = ede.getDamager().getType();
			Entity klr = ede.getDamager();

			if (klr instanceof EnderCrystal) {
				EnderCrystal ec = (EnderCrystal) klr;
				EntityDamageEvent explode = ec.getLastDamageCause();
				if (explode != null) {

					if (explode instanceof EntityDamageByEntityEvent) {


						EntityDamageByEntityEvent explodeE = (EntityDamageByEntityEvent) explode;
						klr = explodeE.getDamager();
						damager = klr.getType();

					}
				}
			}

			if (klr instanceof TNTPrimed) {
				TNTPrimed tnt = (TNTPrimed) klr;
				if (tnt.getSource().isValid())
					klr = tnt.getSource();
				damager = klr.getType();
			}
			if (klr instanceof EvokerFangs) {
				EvokerFangs ef = (EvokerFangs) klr;
				klr = ef.getOwner();
				damager = klr.getType();

			}

			if (klr instanceof AreaEffectCloud) {
				cause = DamageCause.DRAGON_BREATH;

				AreaEffectCloud cloud = (AreaEffectCloud) klr;
				if (cloud.getSource() instanceof Entity && ((Entity) cloud.getSource()).isValid()) {
					klr = (Entity) cloud.getSource();
					damager = klr.getType();
				}
			}
			
			if (klr instanceof EnderDragonPart || klr instanceof EnderDragon)
				damager = EntityType.ENDER_DRAGON;

			if(klr instanceof Projectile) {
				ProjectileSource shooter = ((Projectile) klr).getShooter();
				if (shooter instanceof Entity) {
					damager = ((Entity) shooter).getType();
					klr = (Entity) shooter;
				}
			}

			killer = klr.getCustomName() == null ? klr.getName() == null ? "" : klr.getName() : klr.getCustomName();

			if (klr instanceof Player) {

				killer = ((Player) klr).getDisplayName();
				preset = m.getPreset(((Player) klr).getUniqueId());
				if (preset instanceof DefaultPreset) {
					broadcastComponent(e.getDeathMessage(), e.getDeathMessage());
					return;
				}
			}

			damager = klr.getType();

		}

		deathmessage = preset.getMessage(damager, cause);
		try {
			deathmessage = deathmessage.replaceAll("%player%", p.getDisplayName());
			if (killer != null) {
				deathmessage = deathmessage.replaceAll("%killer%", killer);
			}
		} catch (NullPointerException npe) {

		}

		broadcastComponent(deathmessage, e.getDeathMessage());
		
	}


	public void broadcastComponent(String deathmessage, String regularmessage) {
		TextComponent tc = new TextComponent(
				ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("prefix")));
		TextComponent hover = new TextComponent(ChatColor.translateAlternateColorCodes('&', deathmessage));
		if (m.getConfig().getBoolean("do-hover-message"))
			hover.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(regularmessage).create()));

			tc.addExtra(hover);
			Bukkit.spigot().broadcast(tc);

	}

}

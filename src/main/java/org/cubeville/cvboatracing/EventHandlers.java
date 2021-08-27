package org.cubeville.cvboatracing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.cubeville.cvboatracing.models.Race;
import org.cubeville.cvboatracing.models.RaceSign;
import org.bukkit.attribute.*;
import java.util.HashMap;
import java.util.UUID;

public class EventHandlers implements Listener {

	JavaPlugin plugin;
	long countdownFreeze;

	public EventHandlers(JavaPlugin plugin) {
		this.plugin = plugin;
	}



	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null &&
			event.getAction() == Action.RIGHT_CLICK_BLOCK &&
			SignManager.signMaterials.contains(event.getClickedBlock().getType())
		) {
			RaceSign sign = SignManager.getSign(event.getClickedBlock().getLocation());
			if (sign == null) {
				return;
			}
			sign.onRightClick(event.getPlayer());
		}
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		if (event.getEntityType() == EntityType.BOAT) {
			if (event.getBlock().getType() == Material.TRIPWIRE
				|| event.getBlock().getType() == Material.TRIPWIRE_HOOK) {
				Boat boat = (Boat) event.getEntity();
				for ( Entity entity : boat.getPassengers()) {
					if (entity.getType() == EntityType.PLAYER) {
						RaceManager.advanceCheckpoints((Player) entity);
					}

				}
			}
		}
	}

	@EventHandler
	public void onExit(VehicleExitEvent e) {
		if (e.getExited().getType() == EntityType.PLAYER && e.getVehicle().getType() == EntityType.BOAT) {
			// will cancel race if the player exited their boat in a race
			Player p = (Player) e.getExited();
			Race r = RaceManager.getRace(p);
			if (r != null) {
				RaceManager.cancelRace(p, "You left the boat during the race.");
				e.getVehicle().remove();
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Race r = RaceManager.getRace(p);
		if (r != null) {
			// remove them from a queue if they're in it
			TrackManager.clearPlayerFromQueues(p);
			RaceManager.cancelRace(p, "You left the game during the race.");
			p.getVehicle().remove();
			p.teleport(r.getTrack().getExit());
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			if (p.getHealth() - e.getDamage() < 1) {
				Race r = RaceManager.getRace(p);
				if (r != null) {
					RaceManager.cancelRace(p, "You died during the race.");
					e.setCancelled(true);
					if (p.getVehicle() != null) {
						p.getVehicle().remove();
					}
					p.setInvulnerable(true);
					countdownFreeze = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						() -> p.setInvulnerable(false), 10L);
					p.teleport(r.getTrack().getExit());
					p.setHealth(20.0);
				}
			}
		}
	}


}

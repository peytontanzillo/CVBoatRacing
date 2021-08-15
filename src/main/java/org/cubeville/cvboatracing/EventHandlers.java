package org.cubeville.cvboatracing;

import org.bukkit.Material;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.cubeville.cvboatracing.models.Race;
import org.cubeville.cvboatracing.models.RaceSign;

public class EventHandlers implements Listener {

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
}

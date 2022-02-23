package org.cubeville.cvracing.models;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Leaderboard {
	private final double LINE_SPACE = .3;
	private Location location;
	private List<ArmorStand> armorStands = new ArrayList<>();

	public Leaderboard(Location location) {
		this.location = location;
		setDisplay(Collections.singletonList("§e§lLoading..."));
	}

	private ArmorStand spawnArmorStand(Location loc, String text) {
		ArmorStand as = (ArmorStand) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setGravity(false);
		as.setVisible(false);
		as.setCanPickupItems(false);
		as.setMarker(true);
		as.setCustomName(text);
		as.setCustomNameVisible(true);
		as.addScoreboardTag("CVBoatRace-LeaderboardArmorStand");
		return as;
	}

	public void setDisplay(List<String> lines) {
		clear();
		Location loc = location.clone();
		for (String line : lines) {
			armorStands.add(spawnArmorStand(loc, line));
			loc.setY(loc.getY() - LINE_SPACE);
		}
	}

	public void clear() {
		List<Entity> nearbyEntities = (List<Entity>) Objects.requireNonNull(location.getWorld())
			.getNearbyEntities(location, 1, 5, 1);

		// Remove entities by selection in case the plugin loses track of the armor stands
		for (Entity ent : nearbyEntities) {
			if (ent.getScoreboardTags().contains("CVBoatRace-LeaderboardArmorStand")) {
				ent.remove();
			}
		}
		armorStands.clear();
	}

	public Location getLocation() {
		return location;
	}
}

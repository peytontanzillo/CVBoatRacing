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
	private Location location;
	private List<String> displayText;

	public Leaderboard(Location location) {
		this.location = location;
		setDisplayText(Collections.singletonList("§e§lLoading..."));
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

	public void display() {
		if (!location.getChunk().isEntitiesLoaded()) { return; }
		clear();
		Location loc = location.clone();
		for (String line : displayText) {
			double LINE_SPACE = .3;
			loc.setY(loc.getY() - LINE_SPACE);
			spawnArmorStand(loc, line);
		}
	}

	public void setDisplayText(List<String> displayText) {
		this.displayText = displayText;
		display();
	}

	public void clear() {
		if (!location.getChunk().isEntitiesLoaded()) { return; }
		List<Entity> nearbyEntities = (List<Entity>) Objects.requireNonNull(location.getWorld())
				.getNearbyEntities(location, 2, 5, 2);
		for (Entity ent : nearbyEntities) {
			if (ent.getScoreboardTags().contains("CVBoatRace-LeaderboardArmorStand")) {
				ent.remove();
			}
		}
	}

	public Location getLocation() {
		return location;
	}
}

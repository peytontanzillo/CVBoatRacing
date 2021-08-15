package org.cubeville.cvboatracing;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.cubeville.cvboatracing.models.RaceSign;
import org.cubeville.cvboatracing.models.Track;

import java.util.*;

public class SignManager {

	public static final Set<Material> signMaterials;

	static {
		signMaterials = new HashSet<>();
		signMaterials.add(Material.SPRUCE_WALL_SIGN);
		signMaterials.add(Material.ACACIA_WALL_SIGN);
		signMaterials.add(Material.BIRCH_WALL_SIGN);
		signMaterials.add(Material.DARK_OAK_WALL_SIGN);
		signMaterials.add(Material.JUNGLE_WALL_SIGN);
		signMaterials.add(Material.OAK_WALL_SIGN);
		signMaterials.add(Material.CRIMSON_WALL_SIGN);
		signMaterials.add(Material.WARPED_WALL_SIGN);
		signMaterials.add(Material.SPRUCE_SIGN);
		signMaterials.add(Material.ACACIA_SIGN);
		signMaterials.add(Material.BIRCH_SIGN);
		signMaterials.add(Material.DARK_OAK_SIGN);
		signMaterials.add(Material.JUNGLE_SIGN);
		signMaterials.add(Material.OAK_SIGN);
		signMaterials.add(Material.CRIMSON_SIGN);
		signMaterials.add(Material.WARPED_SIGN);
	}

	private static HashMap<String, RaceSign> signs = new HashMap<>();

	public static RaceSign addSign(Sign sign, Track track) {
		RaceSign raceSign = new RaceSign(sign, track);
		track.addSign(raceSign);
		signs.put(createKey(sign.getLocation()), raceSign);
		return raceSign;
	}

	public static RaceSign getSign(Location location) {
		return signs.get(createKey(location));
	}

	public static String createKey(Location l) {
		String coordinateString = l.getX() + "," + l.getY() + "," + l.getZ();
		return "#" + l.getWorld().getName() + "~" + coordinateString;
	}
}

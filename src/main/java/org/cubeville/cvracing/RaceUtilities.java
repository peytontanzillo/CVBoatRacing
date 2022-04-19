package org.cubeville.cvracing;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cubeville.cvracing.models.Score;
import org.cubeville.cvracing.models.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RaceUtilities {
	public static String formatTimeString(long time) {
		return String.format("%d:%02d.%03d", (int) time / 60000, (int) (time / 1000) % 60, (int) time % 1000);
	}

	public static List<String> getLeaderboardLines(Track t, int startIndex, int endIndex) {
		List<String> result = new ArrayList<>();
		result.add("§e§lLeaderboard for §6§l" + t.getName());
		result.add("§f§l--------------------------------");
		if (ScoreManager.getTrackScores(t) == null) {
			result.add("§e§lThere are no scores on this track.");
		} else {
			List<Score> sortedTimes = new ArrayList<>(ScoreManager.getTrackScores(t));
			for (int i = startIndex; i <= endIndex; i++) {
				if (i >= sortedTimes.size()) {
					break;
				}
				Score s = sortedTimes.get(i);
				result.add("§e§l#" + (i + 1) + "§b " + s.getPlayerName() + " §f- " + formatTimeString(
					s.getFinalTime()));
			}
			if (result.size() == 2) {
				result.add("§7§oThere are no times for places " + (startIndex + 1) + " - " + (endIndex + 1)
					+ ".");
			}
		}
		result.add("§f§l--------------------------------");
		return result;
	}

	public static ItemStack getLeaveItem() {
		ItemStack item = new ItemStack(Material.COPPER_INGOT, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName("§c§lLeave Race");
		item.setItemMeta(im);
		return item;
	}

	public static ItemStack getCPResetItem() {
		ItemStack item = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName("§e§lGo To Last Checkpoint");
		item.setItemMeta(im);
		return item;
	}

	public static ItemStack getLeaveQueueItem() {
		ItemStack item = new ItemStack(Material.COPPER_INGOT, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName("§c§lLeave Queue");
		item.setItemMeta(im);
		return item;
	}

	public static String formatBlockLocation(Location loc) {
		return "x: " + loc.getBlockX() +
				" y: " + loc.getBlockY() +
				" z: " + loc.getBlockZ();
	}

	public static String blockLocToString(Location loc) {
		List<String> locParameters = new ArrayList<>(
				Arrays.asList(
						loc.getWorld().getName(), // world
						String.valueOf(loc.getBlockX()),
						String.valueOf(loc.getBlockY()),
						String.valueOf(loc.getBlockZ())
				)
		);
		return String.join(",", locParameters);
	}

	public static String tpLocToString(Location loc) {
		List<String> locParameters = new ArrayList<>(
				Arrays.asList(
						loc.getWorld().getName(), // world
						String.valueOf(loc.getX()),
						String.valueOf(loc.getY()),
						String.valueOf(loc.getZ()),
						String.valueOf(loc.getYaw()),
						String.valueOf(loc.getPitch())
				)
		);
		return String.join(",", locParameters);
	}
}

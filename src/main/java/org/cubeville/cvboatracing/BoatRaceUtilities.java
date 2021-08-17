package org.cubeville.cvboatracing;

import org.bukkit.Bukkit;
import org.cubeville.cvboatracing.models.Score;
import org.cubeville.cvboatracing.models.Track;

import java.util.ArrayList;
import java.util.List;

public class BoatRaceUtilities {
	public static String formatTimeString(long time) {
		return String.format("%d:%02d.%03d", (int) time / 60000, (int) (time / 1000) % 60, (int) time % 1000);
	}

	public static List<String> getLeaderboardLines(Track t, int startIndex, int endIndex) {
		List<String> result = new ArrayList<>();
		List<Score> sortedTimes = t.getScores();
		for (int i = startIndex; i <= endIndex; i++) {
			if (i >= sortedTimes.size()) { break; }
			Score s = sortedTimes.get(i);
			String playerName = Bukkit.getOfflinePlayer(s.getPlayerUUID()).getName();
			result.add("§e§l#" + (i + 1) + "§b " + playerName + " §f- " + formatTimeString(s.getFinalTime()));
		}
		return result;
	}
}

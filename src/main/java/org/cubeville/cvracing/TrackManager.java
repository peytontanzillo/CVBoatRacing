package org.cubeville.cvracing;

import org.bukkit.entity.Player;
import org.cubeville.cvracing.models.RaceSign;
import org.cubeville.cvracing.models.Track;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TrackManager {

	public static final List<TrackType> vehicleRaceTypes = Arrays.asList(TrackType.PIG, TrackType.BOAT, TrackType.HORSE, TrackType.STRIDER);

	private static HashMap<String, Track> tracks = new HashMap<>();

	public static Track addTrack(String name) {
		Track newTrack = new Track(name);
		tracks.put(name, newTrack);
		return newTrack;
	}

	public static Track getTrack(String name) {
		return tracks.get(name);
	}

	public static List<Track> getTracks() {
		return new ArrayList<>(tracks.values());
	}

	public static void clearPlayerFromQueues(Player p) { clearPlayerFromQueues(p, null); }

	public static void clearPlayerFromQueues(Player p, @Nullable Track t) {
		for (Track track : getTracks()) {
			if (track.equals(t)) { continue; }
			track.removePlayerFromQueue(p);
			for (RaceSign sign : track.getSigns()) {
				sign.displayQueue();
			}
		}
	}

	public static void clearArmorStands() {
		getTracks().forEach(Track::clearLeaderboards);
	}

	public static void loadLeaderboards() {
		getTracks().forEach(Track::loadLeaderboards);
	}

	public static void deleteTrack(Track track) {
		tracks.remove(track.getName());
		ScoreManager.deleteAllScores(track);
	}

	public static Track getTrackHostedBy(Player player) {
		for (Track track : getTracks()) {
			if (track.getHostedRace() == null) { continue; }
			if (track.getHostedRace().getHostingPlayer().getUniqueId().equals(player.getUniqueId())) {
				return track;
			}
		}
		return null;
	}
}

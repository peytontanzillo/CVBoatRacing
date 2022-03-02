package org.cubeville.cvracing;

import org.bukkit.entity.Player;
import org.cubeville.cvracing.models.RaceSign;
import org.cubeville.cvracing.models.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackManager {

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

	public static void clearPlayerFromQueues(Player p) {
		for (Track track : getTracks()) {
			track.removePlayerFromQueue(p);
			RaceManager.removePlayerFromVersus(track, p);
			for (RaceSign sign : track.getSigns()) {
				sign.displayQueue();
			}
		}
	}

	public static void clearPlayerFromTrialsQueues(Player p, Track t) {
		for (Track track : getTracks()) {
			track.removePlayerFromQueue(p);
			if (t != track) { RaceManager.removePlayerFromVersus(track, p); }
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


}

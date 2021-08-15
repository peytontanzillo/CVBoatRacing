package org.cubeville.cvboatracing;

import org.cubeville.cvboatracing.models.Track;

import java.util.HashMap;

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
}

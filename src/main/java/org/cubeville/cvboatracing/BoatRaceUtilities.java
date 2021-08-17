package org.cubeville.cvboatracing;

public class BoatRaceUtilities {
	public static String formatTimeString(long time) {
		return String.format("%d:%02d.%03d", (int) time / 60000, (int) (time / 1000) % 60, (int) time % 1000);
	}
}

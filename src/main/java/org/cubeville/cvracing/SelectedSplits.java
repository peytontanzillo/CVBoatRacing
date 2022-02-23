package org.cubeville.cvracing;

import java.util.*;

public class SelectedSplits {

	private static Set<UUID> usingWR = new HashSet<>();
	private static HashMap<UUID, UUID> defaultSplits = new HashMap<>();

	public static UUID getSelectedSplitPlayer(UUID player) {
		return defaultSplits.get(player);
	}

	public static void setSelectedSplitPlayer(UUID player, UUID target) {
		defaultSplits.put(player, target);
		deselectWR(player);
	}

	public static void selectWR(UUID player) {
		usingWR.add(player);
		defaultSplits.remove(player);
	}

	public static void deselectWR(UUID player) {
		usingWR.remove(player);
	}

	public static Boolean isUsingWR(UUID player) {
		return usingWR.contains(player);
	}


	public static void deleteSelectedSplitPlayer(UUID player) {
		defaultSplits.remove(player);
		usingWR.remove(player);
	}
}

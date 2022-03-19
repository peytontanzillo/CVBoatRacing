package org.cubeville.cvracing.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.ScoreManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Score;
import org.cubeville.cvracing.models.Track;

import java.util.*;

public class CompareSplits extends Command {

	public CompareSplits() {
		super("splits compare");
		// track
		addBaseParameter(new CommandParameterString());
		// player 1
		addBaseParameter(new CommandParameterString());
		// player 2
		addOptionalBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.splits.compare");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		String trackName = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(trackName);

		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		OfflinePlayer op1 = Bukkit.getOfflinePlayer((String) baseParameters.get(1));
		if (!op1.hasPlayedBefore()) {
			throw new CommandExecutionException("Player " + baseParameters.get(1) + " does not exist.");
		}
		Score s1 = ScoreManager.getScore(op1.getUniqueId(), track);
		if (s1 == null) {
			throw new CommandExecutionException("Player " + op1.getName() + " does not have a registered time on " + track.getName() + ".");
		}
		Score s2;

		if (baseParameters.size() > 2) {
			OfflinePlayer op2 = Bukkit.getOfflinePlayer((String) baseParameters.get(2));
			if (!op2.hasPlayedBefore()) {
				throw new CommandExecutionException("Player " + baseParameters.get(2) + " does not exist.");
			}
			s2 = ScoreManager.getScore(op2.getUniqueId(), track);
			if (s2 == null) {
				throw new CommandExecutionException("Player " + op2.getName() + " does not have a registered time on " + track.getName() + ".");
			}
		} else {
			s2 = s1;
			s1 = ScoreManager.getScore(player.getUniqueId(), track);
			if (s1 == null) {
				throw new CommandExecutionException("You do not have a registered time on " + track.getName() + ".");
			}
		}

		HashMap<Integer, Long> s1Splits = s1.getSplits();
		HashMap<Integer, Long> s2Splits = s2.getSplits();
		CommandResponse result = new CommandResponse();
		result.addMessage("§6§l" + s1.getPlayerName() + " vs " + s2.getPlayerName() + " on " + track.getName());
		for (int splitIndex : s1Splits.keySet()) {
			long s1Time = s1Splits.get(splitIndex);
			long s2Time = s2Splits.get(splitIndex);
			String comparison = getComparison(s1Time, s2Time);
			result.addMessage(
				"§6§lCP" + (splitIndex + 1) + ":§f " + RaceUtilities.formatTimeString(s1Time) + " " +
					comparison + "§f " +
					RaceUtilities.formatTimeString(s2Time)
			);
		}

		result.addMessage(
			"§6§lEND:§f " + RaceUtilities.formatTimeString(s1.getFinalTime()) + " " +
				getComparison(s1.getFinalTime(), s2.getFinalTime()) + "§f " +
				RaceUtilities.formatTimeString(s2.getFinalTime())
		);

		return result;
	}

	private String getComparison(long t1, long t2) {
		if (t1 > t2) {
			return "§c+" + RaceUtilities.formatTimeString(t1 - t2);
		} else if (t1 < t2) {
			return "§a-" + RaceUtilities.formatTimeString(t2 - t1);
		}
		return "§e00:00.00";
	}
}

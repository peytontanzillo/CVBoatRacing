package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.ScoreManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetLeaderboard extends Command {


	public GetLeaderboard() {
		super("leaderboard");
		addBaseParameter(new CommandParameterString());
		addOptionalBaseParameter(new CommandParameterInteger());
		setPermission("cvboatrace.leaderboard");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		Track t = TrackManager.getTrack((String) baseParameters.get(0));
		if (t == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		int startIndex = 0;
		if (baseParameters.size() > 1) {
			if ((int) baseParameters.get(1) < 1) {
				throw new CommandExecutionException("Index out of bounds.");
			}
			startIndex = 10 * (((int) baseParameters.get(1)) - 1);
		}
		int numberOfScores = ScoreManager.getTrackScores(t).size();
		if (numberOfScores < startIndex) {
			throw new CommandExecutionException("Page " + baseParameters.get(1) + " does not exist.");
		}
		CommandResponse cr = new CommandResponse();
		for (String line : RaceUtilities.getLeaderboardLines(t, startIndex, startIndex + 9)) {
			cr.addMessage(line);
		}
		if (numberOfScores > startIndex + 10) {
			if (baseParameters.size() > 1) {
				cr.addMessage("&c/boatrace leaderboard " + baseParameters.get(0) + " " + ((int) baseParameters.get(1) + 1));
			} else {
				cr.addMessage("&c/boatrace leaderboard " + baseParameters.get(0) + " 2");
			}
		}
		return cr;
	}
}

package org.cubeville.cvboatracing.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvboatracing.BoatRaceUtilities;
import org.cubeville.cvboatracing.TrackManager;
import org.cubeville.cvboatracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetLeaderboard extends Command {


	public GetLeaderboard() {
		super("leaderboard");
		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.leaderboard");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		Track t = TrackManager.getTrack((String) baseParameters.get(0));
		if (t == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		CommandResponse cr = new CommandResponse();
		cr.addMessage("&e&lLeaderboard for &6&l" + t.getName());
		cr.addMessage("&f&l--------------------------------");
		for (String line : BoatRaceUtilities.getLeaderboardLines(t, 0, 9)) {
			cr.addMessage(line);
		}
		cr.addMessage("&f&l--------------------------------");
		return cr;
	}
}

package org.cubeville.cvboatracing.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Help extends Command {

	public Help() {
		super("help");
		setPermission("cvboatrace.help");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> list)
		throws CommandExecutionException {
		String[] helpOutput = {
			"&b/boatrace leaderboard <track_id> [page_number]",
			"&3View the leaderboard for a track.",
			"&b/boatrace pb <track_id> [player_name]",
			"&3View the personal best time on a track.",
			"&b/boatrace rank <track_id> [player_name]",
			"&3View the rank on a track.",
			"&b/boatrace splits compare <track_id> <player_name> [player_name]",
			"&3Compare the splits on a track.",
			"&b/boatrace splits use [player_name]",
			"&3Use the splits of another player on every track.",
			"&b/boatrace splits use wr",
			"&3Use the splits of the world record holder on every track."
		};
		CommandResponse cr = new CommandResponse();
		cr.setBaseMessage("&c&lC&9&lV &6&lBoat Racing Commands");
		for (String helpLine : helpOutput) {
			cr.addMessage(helpLine);
		}
		return cr;
	}
}

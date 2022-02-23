package org.cubeville.cvracing.commands;

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
			"&b/race leaderboard <track_id> [page_number]",
			"&3&oView the leaderboard for a track.",
			"&b/race pb <track_id> [player_name]",
			"&3&oView the personal best time on a track.",
			"&b/race rank <track_id> [player_name]",
			"&3&oView the rank on a track.",
			"&b/race splits compare <track_id> <player_name> [player_name]",
			"&3&oCompare the splits on a track.",
			"&b/race splits use [player_name]",
			"&3&oUse the splits of another player on every track.",
			"&b/race splits use wr",
			"&3&oUse the splits of the world record holder on every track."
		};
		CommandResponse cr = new CommandResponse();
		cr.setBaseMessage("&c&lC&9&lV &6&lRacing Commands");
		for (String helpLine : helpOutput) {
			cr.addMessage(helpLine);
		}
		return cr;
	}
}

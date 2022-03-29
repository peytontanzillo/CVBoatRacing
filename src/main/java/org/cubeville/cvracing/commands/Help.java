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
		setPermission("cvracing.citizen.help");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> list)
		throws CommandExecutionException {
		String[] helpOutput = {
			"&6/race leaderboard <track_id> [page_number]",
			"&f&oView the leaderboard for a track.",
			"&6/race pb <track_id> [player_name]",
			"&f&oView the personal best time on a track.",
			"&6/race rank <track_id> [player_name]",
			"&f&oView the rank on a track.",
			"&6/race splits compare <track_id> <player_name> [player_name]",
			"&f&oCompare the splits on a track.",
			"&6/race splits use [player_name]",
			"&f&oUse the splits of another player on every track.",
			"&6/race splits use wr",
			"&f&oUse the splits of the world record holder on every track."
		};
		CommandResponse cr = new CommandResponse();
		cr.setBaseMessage("&c&lC&9&lV &e&lRacing Commands");
		for (String helpLine : helpOutput) {
			cr.addMessage(helpLine);
		}
		return cr;
	}
}

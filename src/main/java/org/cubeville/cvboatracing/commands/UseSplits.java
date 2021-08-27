package org.cubeville.cvboatracing.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvboatracing.ScoreManager;
import org.cubeville.cvboatracing.SelectedSplits;
import org.cubeville.cvboatracing.TrackManager;
import org.cubeville.cvboatracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UseSplits extends Command {

	public UseSplits() {
		super("splits use");
		addOptionalBaseParameter(new CommandParameterString());
		// TODO include parameter for track on splits
		// addOptionalBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.splits.use");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters,
		List<Object> baseParameters) throws CommandExecutionException {
		if (baseParameters.size() == 0) {
			SelectedSplits.deleteSelectedSplitPlayer(player.getUniqueId());
			return new CommandResponse("§6Splits set to your personal best time.");
		}
		String p1String = (String) baseParameters.get(0);
		if (p1String.toLowerCase().equals("wr")) {
			SelectedSplits.selectWR(player.getUniqueId());
			return new CommandResponse("§6Splits set to the world record time on each track.");
		}

		OfflinePlayer op = Bukkit.getOfflinePlayer(p1String);
		if (op.hasPlayedBefore()) {
			SelectedSplits.setSelectedSplitPlayer(player.getUniqueId(), op.getUniqueId());
			return new CommandResponse("§6Splits set to " + op.getName() + "'s personal best time.");
		}
		throw new CommandExecutionException("Cannot find a player with the name " + p1String + ".");
	}
}

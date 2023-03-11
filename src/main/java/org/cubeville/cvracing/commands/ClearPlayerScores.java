package org.cubeville.cvracing.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.ScoreManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClearPlayerScores extends BaseCommand {
	public ClearPlayerScores() {
		super("player scores clear");
		// player
		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.admin.scores");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		OfflinePlayer op = Bukkit.getOfflinePlayer((String) baseParameters.get(0));
		if (!op.hasPlayedBefore()) {
			throw new CommandExecutionException("Player " + baseParameters.get(0) + " does not exist.");
		}

		ScoreManager.deletePlayerScores(op.getUniqueId());
		return new CommandResponse("&dThe scores for player " + op.getName() + " are cleared.");
	}

}

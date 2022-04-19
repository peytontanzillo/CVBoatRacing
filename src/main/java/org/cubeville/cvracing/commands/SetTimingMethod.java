package org.cubeville.cvracing.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.RaceManager;

import java.util.*;

public class SetTimingMethod extends BaseCommand {

	private JavaPlugin plugin;

	public SetTimingMethod(JavaPlugin plugin) {
		super("timing");
		addBaseParameter(new CommandParameterEnumeratedString( new HashSet<>(Arrays.asList("system", "tps")) ));
		setPermission("cvracing.admin.timing");

		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		String timing = ((String) baseParameters.get(0)).toUpperCase();
		RaceManager.setTiming(timing);
		plugin.getConfig().set("timing", timing);
		plugin.saveConfig();
		return new CommandResponse("Set timing to " + timing);
	}
}

package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.RaceSignType;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.SignManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.RaceSign;

import java.util.*;

public class SetSignLaps extends Command {
	private JavaPlugin plugin;

	public SetSignLaps(JavaPlugin plugin) {
		super("track signs setlaps");
		// lap number
		addBaseParameter(new CommandParameterInteger());
		setPermission("cvboatrace.signs.setlaps");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();
		int laps = (int) baseParameters.get(0);

		Block block = player.getTargetBlock(null, 100);
		if (!(SignManager.signMaterials.contains(block.getType()))) {
			throw new CommandExecutionException("You need to be looking at a sign.");
		}

		if (laps < 1) {
			throw new CommandExecutionException("Laps need to be greater than 0");
		}

		Location sLoc = block.getLocation();
		RaceSign sign = SignManager.getSign(sLoc);
		if (sign == null) {
			throw new CommandExecutionException("That sign is not a race sign.");
		}

		String locationsPath = "tracks." + sign.getTrack().getName() + ".signs";

		config.set(locationsPath + "." + RaceUtilities.blockLocToString(sLoc) + ".laps", laps);
		sign.setLaps(laps);

		plugin.saveConfig();

		return new CommandResponse("Successfully set sign to use " + laps + " laps!");
	}
}

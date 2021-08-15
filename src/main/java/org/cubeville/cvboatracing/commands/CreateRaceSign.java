package org.cubeville.cvboatracing.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvboatracing.SignManager;
import org.cubeville.cvboatracing.TrackManager;

import java.util.*;

public class CreateRaceSign extends Command {
	private JavaPlugin plugin;

	public CreateRaceSign(JavaPlugin plugin) {
		super("createsign");

		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.createsign");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();
		String name = baseParameters.get(0).toString().toLowerCase();

		if (!config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		Block block = player.getTargetBlock(null, 100);
		if (!(SignManager.signMaterials.contains(block.getType()))) {
			throw new CommandExecutionException("You need to be looking at a sign.");
		}
		Sign sign = (Sign) block.getState();
		Location sLoc = sign.getLocation();

		String locationsPath = "tracks." + name + ".signs";

		List<String> signLocations = config.getStringList(locationsPath);
		List<String> locParameters = new ArrayList<>(
			Arrays.asList(
				sLoc.getWorld().getName(), // world
				String.valueOf(sLoc.getBlockX()),
				String.valueOf(sLoc.getBlockY()),
				String.valueOf(sLoc.getBlockZ()),
				String.valueOf(sLoc.getYaw()),
				String.valueOf(sLoc.getPitch())
			)
		);
		signLocations.add(String.join(",", locParameters));
		config.set(locationsPath, signLocations);

		SignManager.addSign(sign, TrackManager.getTrack(name));
		plugin.saveConfig();

		return new CommandResponse("Successfully created a sign for the track " + baseParameters.get(0) + "!");
	}
}

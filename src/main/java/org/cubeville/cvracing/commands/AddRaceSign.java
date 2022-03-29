package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.RaceSignType;
import org.cubeville.cvracing.SignManager;
import org.cubeville.cvracing.TrackManager;

import java.util.*;

public class AddRaceSign extends Command {
	private JavaPlugin plugin;

	public AddRaceSign(JavaPlugin plugin) {
		super("track signs add");
		// track
		addBaseParameter(new CommandParameterString());
		// type
		addBaseParameter(new CommandParameterEnum(RaceSignType.class));
		setPermission("cvracing.setup.signs.edit");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();
		String name = baseParameters.get(0).toString().toLowerCase();
		RaceSignType type = (RaceSignType) baseParameters.get(1);

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

		List<String> locParameters = new ArrayList<>(
			Arrays.asList(
				sLoc.getWorld().getName(), // world
				String.valueOf(sLoc.getBlockX()),
				String.valueOf(sLoc.getBlockY()),
				String.valueOf(sLoc.getBlockZ())
			)
		);

		config.set(locationsPath + "." + String.join(",", locParameters) + ".type", type.toString());
		SignManager.addSign(sign, TrackManager.getTrack(name), type);
		plugin.saveConfig();

		return new CommandResponse("Successfully created a sign with type " + type + " for the track " + name + "!");
	}
}

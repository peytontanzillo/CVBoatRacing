package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.RaceSign;
import org.cubeville.cvracing.models.Track;

import java.util.*;

public class AddVersusSpawn extends Command {
    private JavaPlugin plugin;

    public AddVersusSpawn(JavaPlugin plugin) {
        super("track spawns versus add");

        addBaseParameter(new CommandParameterString());
        setPermission("cvboatrace.track.spawns.versus");
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
        String locationsPath = "tracks." + name + ".versusSpawns";
        Location lbLoc = player.getLocation();

        List<String> twLocations = config.getStringList(locationsPath);
        List<String> locParameters = new ArrayList<>(
                Arrays.asList(
                        lbLoc.getWorld().getName(), // world
                        String.valueOf(lbLoc.getX()),
                        String.valueOf(lbLoc.getY()),
                        String.valueOf(lbLoc.getZ()),
                        String.valueOf(lbLoc.getYaw()),
                        String.valueOf(lbLoc.getPitch())
                )
        );
        twLocations.add(String.join(",", locParameters));
        config.set(locationsPath, twLocations);

        Track t = TrackManager.getTrack(name);
        t.addVersusSpawn(lbLoc);

        for (RaceSign sign : t.getSigns()) {
            sign.displayQueue();
        }

        plugin.saveConfig();

        return new CommandResponse("Successfully created a versus spawn for the track " + baseParameters.get(0) + "!");
    }
}

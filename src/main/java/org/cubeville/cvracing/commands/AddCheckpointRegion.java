package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.CPRegion;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddCheckpointRegion extends Command {

    private JavaPlugin plugin;

    public AddCheckpointRegion(JavaPlugin plugin) {
        super("track checkpoints regions add");

        // track
        addBaseParameter(new CommandParameterString());
        // int of cp
        addBaseParameter(new CommandParameterInteger());

        setPermission("cvboatrace.checkpoints.addregion");
        this.plugin = plugin;
    }

    @Override
    public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
            throws CommandExecutionException {

        FileConfiguration config = plugin.getConfig();
        String name = baseParameters.get(0).toString().toLowerCase();
        Track track = TrackManager.getTrack(name);
        int cpIndex = (int) baseParameters.get(1);

        if (!config.contains("tracks." + name)) {
            throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
        }

        Location min, max;

        try {
            min = BlockUtils.getWESelectionMin(player);
            max = BlockUtils.getWESelectionMax(player).add(1.0, 1.0, 1.0);
        }
        catch(IllegalArgumentException e) {
            throw new CommandExecutionException("Please make a cuboid worldedit selection before running this command.");
        }

        Checkpoint cp = TrackManager.getTrack(name).getCheckpoints().get(cpIndex - 1);
        if (cp == null) {
            throw new CommandExecutionException("Index " + cpIndex + " does not exist, please use /race track checkpoints list to view the indexes.");
        }

        CPRegion cpRegion = cp.addRegion(min, max);

        String locationsPath = "tracks." + name + ".checkpoints." + (cpIndex - 1) + "." + cpRegion.getString();
        config.createSection(locationsPath);

        plugin.saveConfig();

        return new CommandResponse("Successfully created a checkpoint for the track " + baseParameters.get(0) + "!");
    }
}

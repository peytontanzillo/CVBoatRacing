package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CenterPosition extends Command {

    public CenterPosition() {
        super("center");
        setPermission("cvracing.setup.center");
    }

    @Override
    public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> list)
            throws CommandExecutionException {
        Location newLoc = player.getLocation();
        newLoc.setX(Math.round(newLoc.getX() * 2.0) / 2.0);
        newLoc.setZ(Math.round(newLoc.getZ() * 2.0) / 2.0);
        newLoc.setPitch((float)(Math.round(newLoc.getPitch() / 45.0) * 45.0));
        newLoc.setYaw((float)(Math.round(newLoc.getYaw() / 45.0) * 45.0));
        player.teleport(newLoc);
        return new CommandResponse("&eCentered your position in the block");
    }
}

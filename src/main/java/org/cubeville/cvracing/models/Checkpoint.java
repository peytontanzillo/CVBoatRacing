package org.cubeville.cvracing.models;


import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Checkpoint {

    private Location min, max;
    private String command;

    public Checkpoint(Location min, Location max) {
        defineBounds(min, max);
    }

    public boolean containsPlayer(Player p) {
        Location pLoc = p.getLocation();
        return isWithin(pLoc.getX(), min.getX(), max.getX())
                && isWithin(pLoc.getZ(), min.getZ(), max.getZ())
                && isWithin(pLoc.getY(), min.getY(), max.getY());
    }

    private boolean isWithin(double loc, double min, double max) {
        return max >= loc && loc >= min;
    }

    public Location getMax() {
        return max;
    }

    public Location getMin() {
        return min;
    }

    public void defineBounds(Location min, Location max) {
        this.min = min;
        this.max = max;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}

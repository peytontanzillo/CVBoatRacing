package org.cubeville.cvracing.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvracing.RaceUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CPRegion {
    private Location min, max, reset;

    public CPRegion(Location min, Location max) {
        defineRegion(min, max);
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

    public String getString() {
        return  RaceUtilities.blockLocToString(min) + "~" +  RaceUtilities.blockLocToString(max);
    }

    public void defineRegion(Location min, Location max) {
        this.min = min;
        this.max = max;
    }

    public Location getReset() {
        return reset;
    }

    public void setReset(Location reset) {
        this.reset = reset;
    }
}

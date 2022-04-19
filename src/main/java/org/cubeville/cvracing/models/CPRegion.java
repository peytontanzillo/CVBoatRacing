package org.cubeville.cvracing.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
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

    public boolean containsPlayer(RaceState rs) {
        Location location = rs.getPlayer().getLocation();
        Location previousLocation = rs.getPreviousTickLocation();
        return isWithin(location.getX(), previousLocation.getX(), min.getX(), max.getX())
                && isWithin(location.getZ(), previousLocation.getZ(), min.getZ(), max.getZ())
                && isWithin(location.getY(), previousLocation.getY(), min.getY(), max.getY());
    }

    private boolean isWithin(double loc, double prevLoc, double min, double max) {
        return (max >= loc && loc >= min) || // if the location is within the bounds, return true
        (prevLoc < min && loc > min) || // if drawing a line from the previous loc to this one
        (loc < min && prevLoc > max); // passes through the rg, also return true
    }

    private double percentOffset(double loc, double prevLoc, double min, double max) {
        if (prevLoc < min) {
            return (loc - min) / Math.abs(loc - prevLoc);
        } else if (prevLoc > max) {
            return (max - loc) / Math.abs(loc - prevLoc);
        }
        return 1.0;
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

    public long getTPSOffset(RaceState rs) {
        Location location = rs.getPlayer().getLocation();
        Location previousLocation = rs.getPreviousTickLocation();
        double pct = Math.min(
                percentOffset(location.getX(), previousLocation.getX(), min.getX(), max.getX()),
                percentOffset(location.getY(), previousLocation.getY(), min.getY(), max.getY())
        );
        pct = Math.min(pct, percentOffset(location.getZ(), previousLocation.getZ(), min.getZ(), max.getZ()));
        System.out.println(pct);
        return Math.round((System.currentTimeMillis() - rs.getPreviousTick()) * pct);
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

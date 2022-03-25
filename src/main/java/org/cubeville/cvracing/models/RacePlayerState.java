package org.cubeville.cvracing.models;

import org.bukkit.inventory.ItemStack;

public class RacePlayerState {
    public Race race;
    public ItemStack[] inventory;

    public RacePlayerState(Race race, ItemStack[] inventory) {
        this.race = race;
        this.inventory = inventory;
    }
}

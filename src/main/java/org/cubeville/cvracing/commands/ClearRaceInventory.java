package org.cubeville.cvracing.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterOnlinePlayer;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.CustomizationManager;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.models.BoatMaterial;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.RaceState;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClearRaceInventory extends BaseCommand {

	public ClearRaceInventory() {
		super("commands clear inventory");
		addBaseParameter(new CommandParameterOnlinePlayer());
		setPermission("cvracing.admin.commands");
	}

	@Override
	public CommandResponse execute(CommandSender sender, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Player p = (Player) baseParameters.get(0);
		Race race = RaceManager.getRace(p);
		if (race == null) {
			throw new Error("This player is not currently in a race");
		}
		// preserve armor the player is wearing
		ItemStack[] armor = p.getInventory().getArmorContents().clone();
		p.getInventory().clear();
		p.getInventory().setArmorContents(armor);
		p.getInventory().setItem(8, RaceUtilities.getLeaveItem());
		if (race.getTrack().isIncludeReset()) {
			p.getInventory().setItem(7, RaceUtilities.getCPResetItem());
		}
		return new CommandResponse("Cleared inventory for " + p.getDisplayName());
	}
}

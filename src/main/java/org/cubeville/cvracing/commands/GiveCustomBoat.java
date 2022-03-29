package org.cubeville.cvracing.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.CustomizationManager;
import org.cubeville.cvracing.models.BoatMaterial;
import org.cubeville.cvracing.models.CustomizationState;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GiveCustomBoat extends BaseCommand {

	public GiveCustomBoat() {
		super("commands give boat");
		addBaseParameter(new CommandParameterOnlinePlayer());
		setPermission("cvracing.admin.commands");
	}

	@Override
	public CommandResponse execute(CommandSender sender, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Player p = (Player) baseParameters.get(0);
		BoatMaterial bm = CustomizationManager.getCustomizationState(p).boatMaterial;
		Material boatType = Material.getMaterial(bm.toString() + "_BOAT");
		if (boatType == null) { throw new Error("Material didn't match"); }
		p.getInventory().setItem(p.getInventory().firstEmpty(), new ItemStack(boatType));
		return new CommandResponse("Gave custom boat type to player " + p.getDisplayName());
	}
}

package org.cubeville.cvracing.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.CustomizationManager;
import org.cubeville.cvracing.models.BoatMaterial;
import org.cubeville.cvracing.models.CustomizationState;

import java.util.*;

public class Customize extends Command {

	public Customize() {
		super("customize");
		addBaseParameter(new CommandParameterString());
		addBaseParameter(new CommandParameterString());
		addOptionalBaseParameter(new CommandParameterOnlinePlayer());
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		String customizing = ((String) baseParameters.get(0)).toUpperCase();
		String customization = ((String) baseParameters.get(1)).toUpperCase();
		if (baseParameters.size() > 2 && !player.hasPermission("cvracing.admin.customization")) {
			throw new CommandExecutionException("You do not have permission to set the customization of others");
		}
		Player applying = baseParameters.size() > 2 ? (Player) baseParameters.get(2) : player;
		CustomizationState cs = CustomizationManager.getCustomizationState(applying);
		String extendedMessage = "";
		try {
			switch(customizing) {
				case "BOAT":
				case "BOATS":
					if (!player.hasPermission("cvracing.customize.boat")) {
						throw new CommandExecutionException("You do not have permission to customize boat color.");
					}
					extendedMessage = Arrays.toString(BoatMaterial.values());
					cs.boatMaterial = BoatMaterial.valueOf(customization);
					break;
				case "HORSE_COLOR":
				case "HORSECOLOR":
					if (!player.hasPermission("cvracing.customize.horse.color")) {
						throw new CommandExecutionException("You do not have permission to customize horse color.");
					}
					extendedMessage = Arrays.toString(Horse.Color.values());
					cs.horseColor = Horse.Color.valueOf(customization);
					break;
				case "HORSE_STYLE":
				case "HORSESTYLE":
					if (!player.hasPermission("cvracing.customize.horse.style")) {
						throw new CommandExecutionException("You do not have permission to customize horse style.");
					}
					extendedMessage = Arrays.toString(Horse.Style.values());
					cs.horseStyle = Horse.Style.valueOf(customization);
					break;
				case "HORSE_ARMOR":
				case "HORSEARMOR":
					if (!player.hasPermission("cvracing.customize.horse.armor")) {
						throw new CommandExecutionException("You do not have permission to customize horse armor.");
					}
					if (customization.charAt(0) == '#' && customization.length() == 7 && !player.hasPermission("cvracing.customize.horse.armor.hex")) {
						throw new CommandExecutionException("You do not have permission to set horse armor to a hexcode color.");
					}
					extendedMessage = "[IRON, GOLD, DIAMOND, LEATHER, NONE] or any hexcode value";
					if (!cs.setArmorFromString(customization)) {
						player.sendMessage(ChatColor.RED + customization + " is not a proper value for " + customizing);
						throw new CommandExecutionException("Correct values for " + customizing + " are: " + extendedMessage);
					}
					cs.horseArmorString = customization;
					break;
				default:
					throw new CommandExecutionException(customizing + " is not a valid customization. Correct values are: [BOAT, HORSE_COLOR, HORSE_STYLE, HORSE_ARMOR]");
			}
		} catch (IllegalArgumentException | NullPointerException e) {
			player.sendMessage(ChatColor.RED + customization + " is not a proper value for " + customizing);
			throw new CommandExecutionException("Correct values for " + customizing + " are: " + extendedMessage);
		}

		CustomizationManager.updatePlayerEntry(applying);

		return new CommandResponse("&aSet the customization value of " + customizing + " to " + customization);
	}


}

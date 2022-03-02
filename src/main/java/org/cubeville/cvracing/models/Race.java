package org.cubeville.cvracing.models;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.*;

import java.util.HashMap;
import java.util.Objects;

public abstract class Race {
	protected JavaPlugin plugin;
	protected Track track;
	protected HashMap<Player, RaceState> raceStates = new HashMap<>();
	private int minuteCap;
	private HashMap<Player, ArmorStand > armorStands = new HashMap<>();

	public Race(Track track, JavaPlugin plugin) {
		this.track = track;
 		this.plugin = plugin;
		this.minuteCap = 10; // The player can go for x minutes before they are kicked out of the game
	}

	public void setupPlayerOnTrack(Player player, Location location) {
		raceStates.put(player, new RaceState());
		TrackManager.clearPlayerFromTrialsQueues(player, track);
		if (!location.getChunk().isLoaded()) {
			location.getChunk().load();
		}
		player.teleport(location);
		Vehicle v = null;
		switch (this.track.getType()) {
			case BOAT:
				v = (Vehicle) player.getWorld().spawnEntity(location, EntityType.BOAT);
				break;
			case PIG:
				Pig p = (Pig) player.getWorld().spawnEntity(location, EntityType.PIG);
				p.setSaddle(true);
				ItemStack carrotOnStick = new ItemStack(Material.CARROT_ON_A_STICK, 1);
				ItemMeta stickMeta = carrotOnStick.getItemMeta();
				stickMeta.setDisplayName("§6§lSpeedy Carrot on a Stick");
				stickMeta.addEnchant(Enchantment.DURABILITY, 10, true);
				player.getInventory().setItem(0, new ItemStack(Material.CARROT_ON_A_STICK));
				v = p;
				break;
			case HORSE:
				Horse h = (Horse) player.getWorld().spawnEntity(location, EntityType.HORSE);
				h.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
				h.setTamed(true);
				h.setOwner(player);
				h.setJumpStrength(.8);
				h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.5);
				v = h;
				break;
			case ELYTRA:
				player.getInventory().setChestplate(new ItemStack(Material.ELYTRA, 1));
				player.getInventory().setItem(0, new ItemStack(Material.FIREWORK_ROCKET, 1));
				break;
			case PARKOUR:
				break;
			case TRIDENT:
				ItemStack trident = new ItemStack(Material.TRIDENT, 1);
				ItemMeta tridentMeta = trident.getItemMeta();
				tridentMeta.addEnchant(Enchantment.RIPTIDE, 3, false);
				tridentMeta.setDisplayName("§b§lSpeed Trident");
				trident.setItemMeta(tridentMeta);
				player.getInventory().setItem(0, trident);
				break;
		}
		player.getInventory().setItem(8, RaceUtilities.getLeaveItem());
		player.setCollidable(false);

		ArmorStand as = (ArmorStand) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setGravity(false);
		as.setCanPickupItems(false);
		as.setMarker(true);
		as.addScoreboardTag("CVBoatRace-LeaderboardArmorStand");
		if (v != null) {
			v.addPassenger(player);
			as.addPassenger(v);
		} else {
			as.addPassenger(player);
		}
		this.armorStands.put(player, as);
	}

	private Checkpoint getCurrentCheckpoint(Player p) {
		return this.track.getCheckpoints().get(this.raceStates.get(p).getCheckpointIndex());
	}

	protected void advanceCheckpoint(Player p) {
		if (!this.getCurrentCheckpoint(p).containsPlayer(p)) { return; }
		int playerCheckpointIndex = this.raceStates.get(p).getCheckpointIndex();
		if (playerCheckpointIndex == this.track.getCheckpoints().size() - 1) {
			stopStopwatch(p);
			playerCheckpointIndex++;
			this.raceStates.get(p).setCheckpointIndex(playerCheckpointIndex);
			this.raceStates.get(p).setPlacement(computePlacement(playerCheckpointIndex));
			this.raceStates.get(p).setEndTime();
			this.completeRace(p);
			return;
		}

		p.playSound(p.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 2F, 1F);
		if (track.getType() == TrackType.ELYTRA) {
			p.getInventory().setItem(0, new ItemStack(Material.FIREWORK_ROCKET, 1));
		}
		long elapsed = raceStates.get(p).getElapsed();
		this.raceStates.get(p).addSplit(playerCheckpointIndex, elapsed);
		playerCheckpointIndex++;

		this.raceStates.get(p).setPlacement(computePlacement(playerCheckpointIndex));
		this.raceStates.get(p).setCheckpointIndex(playerCheckpointIndex);
		p.sendMessage("§6CP" + playerCheckpointIndex + ": " + RaceUtilities.formatTimeString(elapsed) + getSplitString(p, elapsed));
	}

	private int computePlacement(int index) {
		int placement = 0;
		for (RaceState rs : this.raceStates.values()) {
			if (rs.getCheckpointIndex() >= index) { placement++; }
		}
		return placement;
	}

	public void cancelRace(Player p, String subtitle) {
		if (raceStates.get(p).getEndTime() != 0) { return; }
		p.sendTitle( ChatColor.RED + "Race ended", ChatColor.DARK_RED + subtitle, 5, 90, 5);
		RaceState rs = raceStates.get(p);
		if (rs.getCountdown() != 0) { endCountdown(p); }
		if (rs.getStopwatch() != 0) { stopStopwatch(p); }
		this.raceStates.remove(p);
		this.endPlayerRace(p);
	}

	public abstract void completeRace(Player p);

	protected abstract String getSplitString(Player p, long elapsed);

	protected abstract void endPlayerRace(Player p);

	protected void runCountdown(Player p, int startCount) {
		int countdown = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			int counter = startCount;
			@Override
			public void run() {
				if (counter > 0) {
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3.0F, 0.7F);
					p.sendTitle(ChatColor.RED + String.valueOf(counter), null, 1, 18, 1);
					counter--;
				} else {
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3.0F, 1.4F);
					p.sendTitle(ChatColor.GREEN + "Go!", null, 1, 38, 1);
					endCountdown(p);
				}
			}
		}, 0L, 20L);
		this.raceStates.get(p).setCountdown(countdown);
	}

	protected void endCountdown(Player p) {
		Bukkit.getScheduler().cancelTask(this.raceStates.get(p).getCountdown());
		this.raceStates.get(p).setCountdown(0);
		ArmorStand as = this.armorStands.get(p);
		as.eject();
		as.remove();
		this.armorStands.remove(p);
		if (track.getType() == TrackType.ELYTRA) {
			p.setGliding(true);
		}
		startStopwatch(p);
	}

	public boolean isCountingDown(Player p) {
		return this.raceStates.get(p).getCountdown() != 0;
	}

	private void startStopwatch(Player p) {
		raceStates.get(p).setElapsed(0);
		int stopwatch = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
			long elapsed = raceStates.get(p).getElapsed();
			raceStates.get(p).setElapsed(elapsed + 50);
			advanceCheckpoint(p);
			if ((int) elapsed / 60000 >= minuteCap) { cancelRace(p, "You took too long to finish.");}
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a§l" + RaceUtilities.formatTimeString(elapsed)));
		}, 0L, 1L);
		raceStates.get(p).setStopwatch(stopwatch);
	}

	private void stopStopwatch(Player p) {
		Bukkit.getScheduler().cancelTask(raceStates.get(p).getStopwatch());
		raceStates.get(p).setStopwatch(0);
	}

	protected void removePlayerFromRaceAndSendToLoc(Player p, Location loc) {
		p.getInventory().clear();
		Entity v = p.getVehicle();
		p.setGliding(false);
		p.setCollidable(true);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (!loc.getChunk().isLoaded()) {
				loc.getChunk().load();
			}
			p.teleport(loc);
			if (v != null) { v.remove(); }
		}, 1L);
	}

	public Track getTrack() {
		return track;
	}
}

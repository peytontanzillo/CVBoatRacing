package org.cubeville.cvracing.models;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
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
		TrackManager.clearPlayerFromTrialsQueues(player);
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
		}
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

	public Location getCurrentCheckpoint(Player p) {
		return this.track.getCheckpoints().get(this.raceStates.get(p).getCheckpointIndex());
	}

	public void advanceCheckpointIfShould(Player p) {
		Block b = this.getCurrentCheckpoint(p).getBlock();
		if (b.getBlockData() instanceof TripwireHook) {
			if (((Powerable) b.getBlockData()).isPowered()) {
				advanceCheckpoint(p);
			}
		} else {
			if (p.getLocation().distance(b.getLocation()) < 1.5) {
				advanceCheckpoint(p);
			}
		}
	}

	protected void advanceCheckpoint(Player p) {
		int playerCheckpointIndex = this.raceStates.get(p).getCheckpointIndex();
		if (playerCheckpointIndex == this.track.getCheckpoints().size() - 1) {
			stopStopwatch(p);
			this.completeRace(p);
			return;
		}
		long elapsed = raceStates.get(p).getElapsed();
		this.raceStates.get(p).addSplit(playerCheckpointIndex, elapsed);
		playerCheckpointIndex++;
		int placement = 0;
		for (RaceState rs : this.raceStates.values()) {
			if (rs.getCheckpointIndex() >= playerCheckpointIndex) { placement++; }
		}
		this.raceStates.get(p).setCheckpointIndex(playerCheckpointIndex);
		this.raceStates.get(p).setPlacement(placement);
		p.sendMessage("§6CP" + playerCheckpointIndex + ": " + RaceUtilities.formatTimeString(elapsed) + getSplitString(p, elapsed));


	}

	public void cancelRace(Player p, String subtitle) {
		p.sendTitle( ChatColor.RED + "Race ended", ChatColor.DARK_RED + subtitle, 5, 90, 5);
		RaceState rs = raceStates.get(p);
		if (rs.getCountdown() != 0) { endCountdown(p); }
		if (rs.getStopwatch() != 0) { stopStopwatch(p); }
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
		raceStates.remove(p);
		p.getInventory().clear();
		Entity v = p.getVehicle();
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

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

import java.util.*;

public abstract class Race {
	protected JavaPlugin plugin;
	protected Track track;
	protected HashMap<Player, RaceState> raceStates = new HashMap<>();
	private final int minuteCap = 10; // The player can go for x minutes before they are kicked out of the game
	int laps;
	boolean hasStarted = false;

	public Race(Track track, JavaPlugin plugin, int laps) {
		this.track = track;
		this.plugin = plugin;
		this.laps = laps;
	}

	public void setupPlayerOnTrack(Player player, Location location) {
		TrackManager.clearPlayerFromQueues(player, track);
		if (!location.getChunk().isLoaded()) {
			location.getChunk().load();
		}
		// preserve armor the player is wearing
		ItemStack[] armor = player.getInventory().getArmorContents().clone();
		player.getInventory().clear();
		player.getInventory().setArmorContents(armor);
		Location tpLoc = location.clone().add(0, 1, 0);
		player.teleport(tpLoc);
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
				stickMeta.setDisplayName("§6§lSpeedy Carrot Stick");
				stickMeta.addEnchant(Enchantment.DURABILITY, 10, true);
				carrotOnStick.setItemMeta(stickMeta);
				player.getInventory().setItem(0, carrotOnStick);
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
				tridentMeta.addEnchant(Enchantment.DURABILITY, 10, true);
				tridentMeta.setDisplayName("§b§lSpeed Trident");
				trident.setItemMeta(tridentMeta);
				player.getInventory().setItem(0, trident);
				break;
			case STRIDER:
				Strider s = (Strider) player.getWorld().spawnEntity(location, EntityType.STRIDER);
				s.setSaddle(true);
				ItemStack fungusOnStick = new ItemStack(Material.WARPED_FUNGUS_ON_A_STICK, 1);
				ItemMeta fungusMeta = fungusOnStick.getItemMeta();
				fungusMeta.setDisplayName("§b§lSpeedy Fungus Stick");
				fungusMeta.addEnchant(Enchantment.DURABILITY, 10, true);
				fungusOnStick.setItemMeta(fungusMeta);
				player.getInventory().setItem(0, fungusOnStick);
				v = s;
				break;
		}
		player.getInventory().setItem(8, RaceUtilities.getLeaveItem());

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
		this.raceStates.get(player).setArmorStand(as);
		this.raceStates.get(player).setResetLocation(location);
	}

	private Checkpoint getCurrentCheckpoint(Player p) {
		return this.track.getCheckpoints().get(this.raceStates.get(p).getCheckpointIndex());
	}

	protected void advanceCheckpoint(Player p) {
		CPRegion regionWithin = this.getCurrentCheckpoint(p).getRegionContaining(p);
		if (regionWithin == null) { return; }

		RaceState newRaceState = this.raceStates.get(p);

		int playerCheckpointIndex = newRaceState.getCheckpointIndex() + 1;
		int lapIndex = newRaceState.getLapIndex();
		if (playerCheckpointIndex == this.track.getCheckpoints().size()) {
			playerCheckpointIndex = 0;
			lapIndex += 1;
		}
		if (regionWithin.getReset() != null) {
			newRaceState.setResetLocation(regionWithin.getReset());
		}
		newRaceState.setCheckpointIndex(playerCheckpointIndex);
		newRaceState.setLapIndex(lapIndex);


		if (lapIndex == this.laps) {
			stopStopwatch(p);
			p.playSound(this.track.getExit(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 1F);
			newRaceState.setEndTime();
			this.raceStates.put(p, newRaceState);
			this.completeRace(p);
			return;
		}

		p.playSound(p.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 2F, 1F);
		if (track.getType() == TrackType.ELYTRA) {
			p.getInventory().setItem(0, new ItemStack(Material.FIREWORK_ROCKET, 1));
		}
		int splitIndex = (lapIndex * this.track.getCheckpoints().size()) + playerCheckpointIndex - 1;
		long elapsed = raceStates.get(p).getElapsed();
		newRaceState.addSplit(splitIndex, elapsed);
		this.raceStates.put(p, newRaceState);
		p.sendMessage("§6CP" + playerCheckpointIndex + ": " + RaceUtilities.formatTimeString(elapsed) + getSplitString(p, elapsed));
	}

	public void cancelRace(Player p, String subtitle) {
		if (!raceStates.containsKey(p)) { return; }
		RaceState rs = raceStates.get(p);
		if (rs.getEndTime() != 0) { return; }
		p.sendTitle( ChatColor.RED + "Race ended", ChatColor.DARK_RED + subtitle, 5, 90, 5);
		if (rs.getCountdown() != 0) { endCountdown(p); }
		if (rs.getStopwatch() != 0) { stopStopwatch(p); }
		this.raceStates.get(p).setCanceled(true);
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
		RaceState rs = this.raceStates.get(p);
		Bukkit.getScheduler().cancelTask(rs.getCountdown());
		rs.setCountdown(0);
		ArmorStand as = rs.getArmorStand();
		as.eject();
		as.remove();
		rs.setArmorStand(null);
		if (track.getType() == TrackType.ELYTRA) {
			p.setGliding(true);
		}
		if (track.isIncludeReset()) {
			p.getInventory().setItem(7, RaceUtilities.getCPResetItem());
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
		Entity v = p.getVehicle();
		p.setGliding(false);
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

	public void tpPlayerToReset(Player p) {
		p.teleport(raceStates.get(p).getResetLocation());
	}

	public HashMap<Player, RaceState> getRaceStates() {
		return raceStates;
	}

	public void setLaps(int laps) {
		this.laps = laps;
	}
}

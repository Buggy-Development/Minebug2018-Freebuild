package de.minebug.freebuild.Events;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.StructureGrowEvent;

import de.minebug.freebuild.Data.SQLData;
import de.minebug.freebuild.Utils.Region;
import de.minebug.freebuild.Utils.utils;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 25.10.2018 - 22:10:19
 */

public class regionEvents implements Listener {
	
	public regionEvents() {
		utils.registerListener(this);
	}
	
	
	//Abfrage ob der Spieler an der Angegebenen Position berechtigt ist zu interagieren.
	private boolean isPermitted (Player player, Location loc) {
		if (SQLData.ChunkExist(loc.getChunk())) {
			Region region = SQLData.getRegion(SQLData.getRegionIDbyChunk(SQLData.getChunkID(loc.getChunk())));
			if (region.getUserdata().containsKey(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}
	
	//Funktion um die Region an der Aktuellen position auszugeben.
	private Region getRegionOnLocation(Location loc) {
		if (SQLData.ChunkExist(loc.getChunk())) {
			return SQLData.getRegion(SQLData.getRegionIDbyChunk(SQLData.getChunkID(loc.getChunk())));
		}
		return null;
	}
	
	//Abfrage ob in der Region eine Einstellung mit entsprechendem Wert vorhanden ist.
	private boolean isFlag(Region region, String flag, String value) {
		if (containsFlag(region, flag)) {
			if (region.getFlags().get(flag).equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean containsFlag(Region region, String flag) {
		return region.getFlags().containsKey(flag);
	}
	
	private String getFlagValue(Region region, String flag) {
		if (containsFlag(region, flag)) {
			return region.getFlags().get(flag);
		}
		return null;
	}
	
	//Funktion die ausgeführt wird wenn der Spieler ein Block abbaut.
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Region region = getRegionOnLocation(event.getBlock().getLocation());
		if (region != null) {
			if (!isPermitted(event.getPlayer(), event.getBlock().getLocation())) {
				event.setCancelled(true);
			}
			if (isFlag(region, "build", "allow")) {
				event.setCancelled(false);
			}
		}
	}
	
	//Funktion die ausgeführt wird wenn der Spieler ein Block platiert.
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Region region = getRegionOnLocation(event.getBlock().getLocation());
		if (region != null) {
			if (!isPermitted(event.getPlayer(), event.getBlock().getLocation())) {
				event.setCancelled(true);
			}
			if (isFlag(region, "build", "allow")) {
				event.setCancelled(false);
			}
		}
	}
	
	//Funktion die ausgeführt wird wenn eine Entität Explodiert.
	@EventHandler
	public void onEntityExplode (EntityExplodeEvent event) {
		Region region = getRegionOnLocation(event.getEntity().getLocation());
		if (region != null) {
			if (isFlag(region, "explosions", "deny")) {
				event.setCancelled(true);
				return;
			} else {
				event.setCancelled(false);
			}
		}
		
		//Verhindert den Schaden an blöcken die gesichert sind.
		ArrayList<Block> remove = new ArrayList<>(); //Verhindert die ConcurrentModificationException
		for (Block block : event.blockList()) {
			Region regiononblock = getRegionOnLocation(block.getLocation());
			if (regiononblock != null && isFlag(regiononblock, "explosions", "deny")) {
				remove.add(block);
			}
		}
		for (Block block : remove) {
			event.blockList().remove(block);
		}
	}
	
	
	//Funktion die ausgeführt wird wenn ein Block sich ausbreitet.
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		Region fromregion = getRegionOnLocation(event.getBlock().getLocation());
		Region toregion = getRegionOnLocation(event.getToBlock().getLocation());
		if (toregion != null) {
			Material type = event.getBlock().getType();
			
			if (fromregion == null || fromregion.getRegionID() != toregion.getRegionID()) {
				if (type.equals(Material.WATER)) {
					if (isFlag(toregion, "water-flow-in", "deny")) {
						event.setCancelled(true);
					} else {
						event.setCancelled(false);
					}
					return;
				}
				if (type.equals(Material.LAVA)) {
					if (isFlag(toregion, "lava-flow-in", "deny")) {
						event.setCancelled(true);
					} else {
						event.setCancelled(false);
					}
					return;
				}
			}
			
			if (type.equals(Material.WATER)) {
				if (isFlag(toregion, "water-flow", "deny")) {
					event.setCancelled(true);
				} else {
					event.setCancelled(false);
				}
			}
			if (type.equals(Material.LAVA)) {
				if (isFlag(toregion, "lava-flow", "deny")) {
					event.setCancelled(true);
				} else {
					event.setCancelled(false);
				}
			}
		}
	}
	
	//Funktion die ausgeführt wird wenn Strukturen wachsen.
	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		BlockState startBlock = event.getBlocks().get(0);
		Region startBlockRegion = getRegionOnLocation(startBlock.getLocation());
		if (startBlockRegion != null && isFlag(startBlockRegion, "tree-grow", "deny")) {
			event.setCancelled(true);
			return;
		}
		//Listet alle Blöcke auf die generiert werden und löscht sie wenn sie in eine Region wachsen die davor gesichert ist.
		for (BlockState state : event.getBlocks()) {
			Region region = getRegionOnLocation(state.getLocation());
			if (region != null) {
				if (startBlockRegion == null || startBlockRegion.getRegionID() != region.getRegionID()) {
					if (state.getType().toString().contains("LEAVES") || state.getType().toString().contains("LOG")) {
						if (isFlag(region, "tree-grow-in", "deny")) {
							state.setType(Material.AIR);
						}
					}
				}
			}
		}
	}
	
	//Funktion die ausgeführt wird wenn Blätter zerfallen.
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		Region region = getRegionOnLocation(event.getBlock().getLocation());
		if (region != null) {
			if (isFlag(region, "leaves-decay", "deny")) {
				event.setCancelled(true);
			} else {
				event.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void onPlayerCombat(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getDamager() instanceof Player) {
				Region regiona = getRegionOnLocation(event.getEntity().getLocation());
				Region regionb = getRegionOnLocation(event.getDamager().getLocation());
				if (isFlag(regiona, "pvp", "deny") || isFlag(regionb, "pvp", "deny")) {
					event.setCancelled(true);
				} else {
					event.setCancelled(false);
				}
			}
			
			if (event.getCause().equals(DamageCause.BLOCK_EXPLOSION) || event.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
				Region regiona = getRegionOnLocation(event.getEntity().getLocation());
				Region regionb = getRegionOnLocation(event.getDamager().getLocation());
				if (isFlag(regiona, "explosions", "deny") || isFlag(regionb, "explosions", "deny")) {
					event.setCancelled(true);
				} else {
					event.setCancelled(false);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntitySpawning(EntitySpawnEvent event) {
		if (event.getEntity() instanceof Monster) {
			if (getRegionOnLocation(event.getEntity().getLocation()) != null) {
				if (isFlag(getRegionOnLocation(event.getEntity().getLocation()), "mob-spawning", "deny")) {
					event.setCancelled(true);
				} else {
					event.setCancelled(false);
				}
			}
		}
		
		if (event.getEntity() instanceof Animals) {
			if (getRegionOnLocation(event.getEntity().getLocation()) != null) {
				if (isFlag(getRegionOnLocation(event.getEntity().getLocation()), "animal-spawning", "deny")) {
					event.setCancelled(true);
				} else {
					event.setCancelled(false);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location movedFrom = event.getFrom();
        Location movedTo = event.getTo();
        Player player = event.getPlayer();
        if ((movedFrom.getX() != movedTo.getX()) && (movedFrom.getY() != movedTo.getY()) && (movedFrom.getZ() != movedTo.getZ())) {
        	Region regionfrom = getRegionOnLocation(movedFrom);
        	Region regionto = getRegionOnLocation(movedTo);
        	if (regionto != null) {
        		if (regionfrom == null || regionfrom != regionto) {
        			if (containsFlag(regionto, "greeting")) {
            			player.sendMessage(getFlagValue(regionto, "greeting").replaceAll("&", "§"));
            		}
        		}
        	}
        	if (regionfrom != null) {
        		if (regionto == null || regionfrom != regionto) {
        			if (containsFlag(regionfrom, "farewell")) {
            			player.sendMessage(getFlagValue(regionfrom, "farewell").replaceAll("&", "§"));
            		}
        		}
        	}
        }
	}
}

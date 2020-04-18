package de.minebug.freebuild.Commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Data.SQLData;
import de.minebug.freebuild.Utils.Permissions;
import de.minebug.freebuild.Utils.Region;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 24.10.2018 - 19:12:10
 */

public class region extends SQLData implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		
		
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		
		//Abfrage ob die länge der Argumente gleich Null ist.
		if (args.length == 0) {

			//Sendet dem Spieler einen Hilfetext.
			player.sendMessage("§7§l§m-------------------------------");
			player.sendMessage("§6/region info {regionnummer} §8- §7Zeige informationen deiner Region.");
			player.sendMessage("§6/region claim §8- §7Sichere den Aktuellen Chunk.");
			player.sendMessage("§6/region unclaim §8- §7Entsichere den Aktuellen Chunk.");
			player.sendMessage("§6/region chunkinfo §8- §7Zeige informationen über den Chunk auf dem du dich befindest.");
			player.sendMessage("§6/region add <Spieler> §8- §7Füge einen Spieler zu der Region hinzu.");
			player.sendMessage("§6/region remove <Spieler> §8- §7Entferne einen Spieler von der Region.");
			player.sendMessage("§6/region promote <Spieler> §8- §7Erhöhe die Berechtigungen des Spielers in der Region.");
			player.sendMessage("§6/region demote <Spieler> §8- §7Senke die Berechtigungen des Spielers in der Region.");
			player.sendMessage("§7§l§m-------------------------------");
			
		}
		
		//Abfrage ob die länge der Argumente eins oder zwei ist.
		if (args.length == 1 || args.length == 2) {
			//Abfrage ob das erste Argument "info" ist.
			if (args[0].equalsIgnoreCase("info")) {
				ArrayList<Region> regions = getPlayerRegions(uuid);
				//Abfrage ob die länge der Argumente eins ist.
				if (args.length == 1) {
					//Abfrage ob die größe der Liste der Regionen in der sich der Spieler befindet 0 ist
					if (regions.size() == 0) {
						player.sendMessage("§cDu bist in keiner Region.");
					//Abfrage ob die größe der Liste der Regionen in der sich der Spieler befindet 1 ist
					} else if (regions.size() == 1) {
						sendRegionInfo(player,getRegion(getPlayersRegion(uuid)));
					//Abfrage ob die größe der Liste der Regionen in der sich der Spieler befindet größer als 1 ist
					} else if (regions.size() > 1) {
						player.sendMessage("§eDu bist in mehreren Regionen, Bitte wähle eine aus:");
						int i = 0;
						//Listet dem Spieler alle Regionen auf.
						for (Region region : regions) {
							player.sendMessage("§6"+(i+1)+" §7- §bID: §7" + region.getRegionID());
							i++;
						}
						player.sendMessage("§eNutze §7/region info §8<§eregionnummer§8>");
					}
				}
				//Abfrage ob die länge der Argumente zwei ist.
				if (args.length == 2) {
					if (regions.size() > 1) {
						int arg1;
						
						//Versucht aus dem zweiten Argument eine Zahl zu bilden.
						try {
							arg1 = Integer.valueOf(args[1]);
						} catch (NumberFormatException ex) {
							player.sendMessage("§cDu musst eine Nummer angeben.");
							return true;
						}
						
						if (arg1 <= 0 || arg1 > regions.size()) {
							player.sendMessage("§cUngültiger wert.");
							return true;
						}
						
						//Abfrage ob die Angegebene Region existiert.
						if (regions.get(arg1-1) != null) {
							Region region = regions.get(arg1-1);
							sendRegionInfo(player, region);
						} else {
							player.sendMessage("§cUngültie Regionnummer");
						}
					} else {
						sendRegionInfo(player, getRegion(getPlayersRegion(uuid)));
					}
				}
			}
		}
		

		//Abfrage ob die länge der Argumente gleich eins ist.
		if (args.length == 1) {
			
			if (args[0].equalsIgnoreCase("chunkinfo")) {
				Chunk chunk = player.getLocation().getChunk();
				//Sendet dem Spieler Informationen über den Chunk in dem er sich befindet.
				player.sendMessage("§6X§7: " + chunk.getX());
				player.sendMessage("§6Z§7: " + chunk.getZ());
				player.sendMessage("§6Claimed§7: " + (ChunkExist(chunk) ? "§aTrue" : "§cFalse"));
				player.sendMessage("§6Claimed by§7: " + (ChunkExist(chunk) ? getRegionIDbyChunk(getChunkID(chunk)) : "§cNAN"));
			}
			
			if (args[0].equalsIgnoreCase("claim")) {
				Region region;
				//Abfrage ob der Spieler sich in einer Region befindet.
				if (isPlayerInRegion(uuid)) {
					region = getRegion(getPlayersRegion(uuid));
				} else {
					int i = registerRegion();
					if (i == -1) {
						player.sendMessage("Error");
						return true;
					}
					region = getRegion(i);
					addUser(region, uuid, Permissions.Besitzer.getPermissiondata());
					region = getRegion(getPlayersRegion(uuid)); //Update region Values
				}
				
				//Abfrage ob der Spieler eine Region besitzt.
				if (region.getUserdata().get(uuid) == Permissions.Besitzer.getPermissiondata()) {
					Chunk chunk = player.getLocation().getChunk();
					if (region.getChunks().contains(chunk)) {
						player.sendMessage("§cDu hast diesen Chunk bereits gesichert.");
						return true;
					}
					//Abfrage ob der Angegebene Chunk existiert.
					if (!ChunkExist(chunk)) {
						addChunk(region, chunk);
						player.sendMessage("§eDu hast diesen Chunk gesichert.");
					} else {
						if (getRegionIDbyChunk(getChunkID(chunk)) != -1) {
							player.sendMessage("§cDieser Chunk gehört zu einer anderen Region.");
						}
					}
				} else player.sendMessage("§cDu bist nicht der Besitzer der Region.");
			}
			
			
			if (args[0].equalsIgnoreCase("unclaim")) {

				//Abfrage ob der Spieler sich in einer Region befindet.
				if (isPlayerInRegion(uuid)) {
					Chunk chunk = player.getLocation().getChunk();
					//Abfrage ob der Chunk existiert.
					if (ChunkExist(chunk)) {
						Region region = getRegion(getRegionIDbyChunk(getChunkID(player.getLocation().getChunk())));
						if (region.getUserdata().containsKey(uuid)) {
							if (region.getUserdata().get(uuid) == Permissions.Besitzer.getPermissiondata()) {
								removeChunk(region, chunk);
								player.sendMessage("§eDu hast diesen Chunk entsichert.");
							} else player.sendMessage("§cDu bist nicht der Besitzer der Region.");
						} else player.sendMessage("§cDu bist nicht in dieser Region.");
					} else player.sendMessage("§cDieser Chunk ist nicht Gesichert.");
				}
			}
		}

		//Abfrage ob die länge der Argumente gleich zwei ist.
		if (args.length == 2) {
			
			if (args[0].equalsIgnoreCase("info")) {
				return true;
			}
			
			//Abfrage ob der Spieler in einer Region ist.
			if (isPlayerInRegion(uuid)) {
				Region region = getRegion(getPlayersRegion(uuid));
				//Abfrage ob der Spieler Besitzer der Region ist.
				if (region.getUserdata().get(uuid) == Permissions.Besitzer.getPermissiondata()) {
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
					UUID tuuid;
					
					//Versucht die uuid der angegebenen Spielers zu bekommen.
					try {
						tuuid = target.getUniqueId(); 
					} catch (NullPointerException ex) {
						player.sendMessage("§cDieser Spieler ist nicht in der Region.");
						return true;
					}
					
					//Abfrage ob der Angegebene Spieler der Spieler ist der den Befehl ausgeführt hat.
					if (tuuid == uuid) {
						player.sendMessage("§cDu kannst nicht mit dir selbst interagieren.");
						return true;
					}
					
					if (args[0].equalsIgnoreCase("add")) {
						if (!region.getUserdata().containsKey(tuuid)) {
							//Fügt einen Spieler zur Region hinzu.
							addUser(region, tuuid, 0);
						} else player.sendMessage("§cDieser Spieler ist bereits in der Region.");
					} else player.sendMessage("§cDieser Spieler ist nicht in der Region.");
						
					if (args[0].equalsIgnoreCase("remove")) {
						if (region.getUserdata().containsKey(tuuid)) {
							//Enfernt einen Spieler von einer Region.
							removeUser(region, tuuid);
						} else player.sendMessage("§cDieser Spieler ist nicht in der Region.");
					} else player.sendMessage("§cDieser Spieler ist nicht in der Region.");
					
					if (args[0].equalsIgnoreCase("promote")) {
						if (region.getUserdata().containsKey(tuuid)) {
							if (region.getUserdata().get(tuuid) == Permissions.Normal.getPermissiondata()) {
								//Erhöht die Rechte eines Spielers.
								setPlayerPermissions(region, tuuid, Permissions.Besitzer.getPermissiondata());
								player.sendMessage("§eDer Spieler " + target.getName() + " ist nun §cBesitzer");
							} else player.sendMessage("§cDer Spieler kann nicht weiter Promotet werden.");
						} else player.sendMessage("§cDer Spieler ist nicht in der Region.");
					}
					
					if (args[0].equalsIgnoreCase("demote")) {
						if (region.getUserdata().containsKey(tuuid)) {
							if (region.getUserdata().get(tuuid) == Permissions.Besitzer.getPermissiondata()) {
								//Senkt die Rechte eines Spielers.
								setPlayerPermissions(region, tuuid, Permissions.Normal.getPermissiondata());
								player.sendMessage("§eDer Spieler " + target.getName() + " ist nun §cMitglied");
							} else player.sendMessage("§cDer Spieler kann nicht weiter Demotet werden.");
						} else player.sendMessage("§cDieser Spieler ist nicht in der Region.");
					}
				}
			}
		}
		return true;
	}
	
	//Sendet dem Spieler informationen über eine Region.
	private void sendRegionInfo(Player player, Region region) {
		StringBuilder sbowner = new StringBuilder();
		StringBuilder sbmoderator = new StringBuilder();
		StringBuilder sbnormal = new StringBuilder();
		
		StringBuilder flags = new StringBuilder();

		int i_00 = 0;
		int i_01 = 0;
		int i_02 = 0;
		
		//Listet alle Spieler in der angegebenen Region auf.
		for (UUID uuid2 : region.getUserdata().keySet()) {
			try {
				OfflinePlayer target = Bukkit.getPlayer(uuid2);
				int permlevel = region.getUserdata().get(uuid2);
				if (permlevel == Permissions.Besitzer.getPermissiondata()) {
					if (i_00 == 0) {
						sbowner.append("" + target.getName());
						i_00 ++;
					} else {
						sbowner.append(", " + target.getName());
					}
				} else if (permlevel == Permissions.Moderator.getPermissiondata()) {
					if (i_01 == 0) {
						sbmoderator.append("" + target.getName());
						i_01 ++;
					} else {
						sbmoderator.append(", " + target.getName());
					}
				} else if (permlevel == Permissions.Normal.getPermissiondata()) {
					if (i_02 == 0) {
						sbnormal.append("" + target.getName());
						i_02 ++;
					} else {
						sbnormal.append(", " + target.getName());
					}
				} else {
					continue;
				}
			} catch (RuntimeException ex) {
				continue;
			} 
		}
		
		if (!region.getFlags().isEmpty()) {
			for (String flag : region.getFlags().keySet()) {
				flags.append(", "+flag+"="+region.getFlags().get(flag));
			}
		}
		
		player.sendMessage("§6Region Informationen: ");
		player.sendMessage("§7§l§m========================================");
		player.sendMessage("§6Besitzer: §7" + sbowner.toString());
		player.sendMessage("§6Moderatoren: §7" + sbmoderator.toString());
		player.sendMessage("§6Mitglieder: §7" + sbnormal.toString());
		player.sendMessage("§7§l§m========================================");
		player.sendMessage("§6Gesicherte Chunks: §7" + region.getChunks().size());
		player.sendMessage("§6Gesetzte Flaggen: §7" + flags.toString());
		player.sendMessage("§7§l§m========================================");
		
	}

}

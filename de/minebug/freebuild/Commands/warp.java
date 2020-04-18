package de.minebug.freebuild.Commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Data.SQLData;
import de.minebug.freebuild.Utils.utils;
import net.md_5.bungee.api.chat.TextComponent;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 26.10.2018 - 16:36:01
 */

public class warp implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("warp")) {
			//Abfrage ob die länge der Argumenten gleich null ist.
			if (args.length == 0) {
				
				HashMap<String, Location> warps = SQLData.getWarps();
				ArrayList<TextComponent> tc = new ArrayList<>();
				
				int i = 0;
				//Konstruiert eine Liste mit allen Warp-Namen.
				for (String warpname : warps.keySet()) {
					if (warpname.startsWith(".")) {
						continue;
					}
					
					try {
						if (i == 1) {
							tc.add(new TextComponent("§7, "));
						} else {
							i = 1;
						}
						tc.add(utils.createCommandText(utils.createHoverText("§a" + warpname, "§cKlick mich zum Teleportieren."), "/warp " + warpname));
						
					} catch (RuntimeException ex) {
						ex.printStackTrace();
					}
				}
				
				TextComponent warplist = new TextComponent("§6§lHomes: \n");
				for (TextComponent tc2 : tc) {
					warplist.addExtra(tc2);
				}
				//Sendet dem Spieler die Home-Liste.
				player.spigot().sendMessage(warplist);

			//Abfrage ob die länge der Argumenten gleich eins ist.
			} else if (args.length == 1) {
				
				if (args[0].equalsIgnoreCase("list")) {
					
					HashMap<String, Location> warps = SQLData.getWarps();
					ArrayList<TextComponent> tc = new ArrayList<>();
					
					int i = 0;
					//Konstruiert eine Liste mit allen Warp-Namen.
					for (String warpname : warps.keySet()) {
						if (warpname.startsWith(".")) {
							continue;
						}
						
						try {
							if (i == 1) {
								tc.add(new TextComponent("§7, "));
							} else {
								i = 1;
							}
							tc.add(utils.createCommandText(utils.createHoverText("§a" + warpname, "§cKlick mich zum Teleportieren."), "/warp " + warpname));
							
						} catch (RuntimeException ex) {
							ex.printStackTrace();
						}
					}
					
					TextComponent warplist = new TextComponent("§6§lWarps §8[§a" + warps.size() + "§8]\n");
					for (TextComponent tc2 : tc) {
						warplist.addExtra(tc2);
					}
					
					//Sendet dem Spieler die Warp-Liste.
					player.spigot().sendMessage(warplist);
					return true;
				}
				
				//Abfrage ob der Warp existiert.
				if (SQLData.WarpExists(args[0])) {
					try {
						Location loc = SQLData.getWarp(args[0]);
						player.teleport(loc);
						player.sendMessage("§6Teleportiere...");
					} catch (RuntimeException ex) {
						player.sendMessage("§cUngültiger Warppunkt.");
						ex.printStackTrace();
						return true;
					}
				}

			//Abfrage ob die länge der Argumenten gleich zwei ist.
			} else if (args.length == 2) {
				
				//Abfrage ob der Spieler die Berechtigung hat.
				if (player.hasPermission("essentials.warp.otherplayers")) {
					if (Bukkit.getOfflinePlayer(args[0]).isOnline()) {
						if (SQLData.WarpExists(args[1])) {
							Player target = Bukkit.getPlayer(args[0]);
							try {
								target.teleport(SQLData.getWarp(args[1]));
								target.sendMessage("§6Teleportiere.");
								player.sendMessage("§6Du hast " + target.getName() + " Teleportiert");
							} catch (RuntimeException ex) {
								player.sendMessage("§cUngültiger Warppunkt.");
								return true;
							}
						}
					}
				}
			} else player.sendMessage("§cSyntax Error.");
		} else if (cmd.getName().equalsIgnoreCase("setwarp")) {
			
			//Abfrage ob der Spieler die Berechtigung hat.
			if (player.hasPermission("essentials.setwarp")) {
				if (args.length == 1) {
					String name = args[0].toLowerCase();
					name.replaceAll("[^A-Za-z0-9_]", "_");
					if (name.equalsIgnoreCase("list")) {
						player.sendMessage("§cDieser Warp name ist Ungültig");
						return true;
					}
					
					//Setzt den Warp.
					SQLData.setWarp(name, player.getLocation());
					player.sendMessage("§6Warppunkt erstellt");
				} else player.sendMessage("§cSyntax Error.");
			}
			
		} else if (cmd.getName().equalsIgnoreCase("delwarp")) {

			//Abfrage ob der Spieler die Berechtigung hat.
			if (player.hasPermission("essentials.delwarp")) {
				if (args.length == 1) {
					String name = args[0].toLowerCase();
					name.replaceAll("[^A-Za-z0-9_]", "_");
					if (name.equalsIgnoreCase("list")) {
						player.sendMessage("§cDieser Warp name ist Ungültig");
						return true;
					}
					
					if (!SQLData.WarpExists(name)) {
						player.sendMessage("§cDieser Warp existiert nicht.");
						return true;
					}
					
					//Löscht den Warp.
					SQLData.removeWarp(name);
					player.sendMessage("§6Warppunkt gelöscht");
				} else player.sendMessage("§cSyntax Error.");
			}
		} else if (cmd.getName().equalsIgnoreCase("warps")) {
			if (args.length == 0) {
				HashMap<String, Location> warps = SQLData.getWarps();
				ArrayList<TextComponent> tc = new ArrayList<>();
				
				int i = 0;
				//Konstruiert eine Liste mit allen Warp-Namen.
				for (String warpname : warps.keySet()) {
					if (warpname.startsWith(".")) {
						continue;
					}
					try {
						if (i == 1) {
							tc.add(new TextComponent("§7, "));
						} else {
							i = 1;
						}
						tc.add(utils.createCommandText(utils.createHoverText("§a" + warpname, "§cKlick mich zum Teleportieren."), "/home " + warpname));
						
					} catch (RuntimeException ex) {
						ex.printStackTrace();
					}
				}
				
				TextComponent warplist = new TextComponent("§6§lHomes: \n");
				for (TextComponent tc2 : tc) {
					warplist.addExtra(tc2);
				}
				//Sendet dem Spieler die Warp-Liste.
				player.spigot().sendMessage(warplist);
			} else player.sendMessage("§cSyntax Error.");
		}
		return true;
	}

}

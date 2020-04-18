package de.minebug.freebuild.Commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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
 * Created by TheHolyException and Julian4060206 at 26.10.2018 - 17:41:46
 */

public class home implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = (Player) sender;
		
		//Abfrage ob der Befehl 'homes' lautet.
		if (cmd.getName().equalsIgnoreCase("homes")) {
			if (args.length == 0) {
				
				HashMap<String, Location> homes = SQLData.getHomes(player.getUniqueId());
				ArrayList<TextComponent> tc = new ArrayList<>();
				
				int i = 0;
				//Setzt alle Home-Namen in eine Liste.
				for (String homename : homes.keySet()) {
					try {
						if (i == 1) {
							tc.add(new TextComponent("§7, "));
						} else {
							i = 1;
						}
						tc.add(utils.createCommandText(utils.createHoverText("§a" + homename, "§cKlick mich zum Teleportieren."), "/home " + homename));
						
					} catch (RuntimeException ex) {
						ex.printStackTrace();
					}
				}
				
				TextComponent homelist = new TextComponent("§6§lHomes: \n");
				for (TextComponent tc2 : tc) {
					homelist.addExtra(tc2);
				}
				//Sendet dem Spieler die liste der Homes.
				player.spigot().sendMessage(homelist);
			} else player.sendMessage("§cSyntax Error.");
			
		} else if (cmd.getName().equalsIgnoreCase("home")) {
			if (args.length == 0) {
				
				HashMap<String, Location> homes = SQLData.getHomes(player.getUniqueId());
				ArrayList<TextComponent> tc = new ArrayList<>();
				
				int i = 0;
				//Erstelle eine Liste aller Home-Namen.
				for (String homename : homes.keySet()) {
					try {
						if (i == 1) {
							tc.add(new TextComponent("§7, "));
						} else {
							i = 1;
						}
						tc.add(utils.createCommandText(utils.createHoverText("§a" + homename, "§cKlick mich zum Teleportieren."), "/home " + homename));
						
					} catch (RuntimeException ex) {
						ex.printStackTrace();
					}
				}
				
				TextComponent homelist = new TextComponent("§6§lHomes: \n");
				for (TextComponent tc2 : tc) {
					homelist.addExtra(tc2);
				}
				//Sendet dem Spieler die Home-Liste.
				player.spigot().sendMessage(homelist);
				
			} else if (args.length == 1) {
				
				//Abfrage ob im ersten argument das zeichen ':' vorhanden ist.
				if (args[0].contains(":")) {
					if (player.hasPermission("essentials.home.others")) {
						//Splittet und wandelt das erste argument bei ':' in einen Array um.
						String[] splited = args[0].split(":");
						
						//Abfrage ob die länge des Arrays "splitted" gleich eins ist.
						if (splited.length == 1) {
							//Abfrage ob der Spieler die Berechtigung hat.
							if (player.hasPermission("essentials.home.others")) {
								try {
									OfflinePlayer target = Bukkit.getOfflinePlayer(splited[0]);
									
									HashMap<String, Location> homes = SQLData.getHomes(target.getUniqueId());
									ArrayList<TextComponent> tc = new ArrayList<>();
									
									int i = 0;
									//Erstelle eine Liste aller Home-Namen.
									for (String name : homes.keySet()) {
										if (i == 1) {
											tc.add(new TextComponent("§7, "));
										} else {
											i = 1;
										}
										tc.add(utils.createCommandText(utils.createHoverText("§a" + name, "§cKlick mich zum Teleportieren."), "/home " + target.getName() + ":" + name));
									}
									
									TextComponent homelist = new TextComponent("§6§lHomes von §c"+target.getName()+": \n");
									for (TextComponent tc2 : tc) {
										homelist.addExtra(tc2);
									}
									//Sendet dem Spieler die Home-Liste.
									player.spigot().sendMessage(homelist);
								} catch (RuntimeException ex) {
									player.sendMessage("§cDieser Spieler hat keine Home Punkte.");
								}
							}

						//Abfrage ob die länge des Arrays "splitted" gleich zwei ist.	
						} else if (splited.length == 2) {
							//Abfrage ob der Spieler die Berechtigung hat.
							if (player.hasPermission("essentials.home.others")) {
								try {
									//Abfrage ob der angegebene Home-Punk existiert.
									if (SQLData.HomeExists(Bukkit.getOfflinePlayer(splited[0]).getUniqueId(), splited[1])) {
										Location home = SQLData.getHome(Bukkit.getOfflinePlayer(splited[0]).getUniqueId(), splited[1]);
										//Teleportiere den Spieler zu dem angegebenen Home-Punkt.
										player.teleport(home);
										player.sendMessage("§6Teleporting...");
									} else {
										player.sendMessage("§cDer Spieler besitzt diesen Home Punkt nicht.");
									}
								} catch (RuntimeException ex) {
									player.sendMessage("§cDieser Spieler hat keine Home Punkte.");
								}
							}
						}
					}
				} else {
					if (SQLData.getHomes(player.getUniqueId()).containsKey(args[0].toLowerCase())) {
						Location home = SQLData.getHome(player.getUniqueId(), args[0].toLowerCase());
						player.teleport(home);
					} else {
						player.sendMessage("§cDieser Home Punkt existiert nicht.");
					}
				}
			} else player.sendMessage("§cSyntax Error.");
			
		} else if (cmd.getName().equalsIgnoreCase("delhome")) {
			
			if (args.length == 1) {
				//Abfrage ob im ersten argument das zeichen ':' vorhanden ist.
				if (args[0].contains(":")) {
					//Abfrage ob der Spieler die Berechtigung hat.
					if (player.hasPermission("essentials.home.others")) {
						String[] splited = args[0].split(":");
						if (splited.length == 2) {
							try {
								//Abfrage ob der angegebene Home-Punk existiert.
								if (SQLData.HomeExists(Bukkit.getOfflinePlayer(splited[0]).getUniqueId(), splited[1])) {
									SQLData.removeHome(Bukkit.getOfflinePlayer(splited[0]).getUniqueId(), splited[1]);
									player.sendMessage("§6Home Punkt Gelöscht.");
								} else {
									player.sendMessage("§cDer Spieler besitzt diesen Home Punkt nicht.");
								}
							} catch (RuntimeException ex) {
								player.sendMessage("§cDieser Spieler hat keine Home Punkte.");
							}
						}
					}
				} else {
					//Ersetzt alle Buchstaben die nicht im Bereich von A-Z, a-z oder 0-9 mit '_'
					String name = args[0].replaceAll("[^A-Za-z0-9_]", "_");
					
					//Abfrage ob der angegebene Home-Punk existiert.
					if (!SQLData.HomeExists(player.getUniqueId(), name)) {
						player.sendMessage("§cDieser Home Punkt existiert nicht.");
						return true;
					}
					
					SQLData.setHome(player.getUniqueId(), name, player.getLocation());
					player.sendMessage("§6Home Punkt wurde gelöscht.");
				}
			} else player.sendMessage("§cSyntax Error.");
			
		} else if (cmd.getName().equalsIgnoreCase("sethome")) {
			
			if (args.length == 1) {
				//Ersetzt alle Buchstaben die nicht im Bereich von A-Z, a-z oder 0-9 mit '_'
				String name = args[0].replaceAll("[^A-Za-z0-9_]", "_");
				int limit = 5;
				//Setzt das Home-Limit anhand der berechtigung.
				if (player.hasPermission("homes.vip")) {limit = 10;}
				if (player.hasPermission("homes.ultra")) {limit = 20;}
				if (player.hasPermission("homes.legende")) {limit = 30;}
				if (player.hasPermission("homes.staff")) {limit = 40;}
				if (player.hasPermission("homes.inf")) {limit = 9999;}

				//Abfrage ob der angegebene Home-Punk existiert.
				if (SQLData.HomeExists(player.getUniqueId(), name)) {
					player.sendMessage("§6Dieser Home existiert bereits.");
					return true;
				}
				
				//Abfrage ob der Spieler das limit an maximal gesetzten Home-Punkten erreicht hat.
				if (SQLData.getHomes(player.getUniqueId()).size() >= limit) {
					player.sendMessage("§6Du kannst keine weiteren Home Punkte setzen. §7(§eMax §c"+limit+"§7)");
					return true;
				}
				
				//Setzt den Home-Punkt-
				SQLData.setHome(player.getUniqueId(), name, player.getLocation());
				player.sendMessage("§6Home Punkt wurde gesetzt.");
			} else player.sendMessage("§cSyntax Error.");
		}
		return true;
	}
}

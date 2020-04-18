package de.minebug.freebuild.Commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.main;
import de.minebug.freebuild.Data.MySQL;
import de.minebug.freebuild.Data.SQLData;
import de.minebug.freebuild.Utils.utils;
import net.md_5.bungee.api.chat.TextComponent;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 27.10.2018 - 17:37:29
 */

public class system implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		
		Player player = (Player) sender;
		if (player.hasPermission("mb.cmd.system")) {
			
			//Abfrage ob die Länge der Argumenten gleich null ist.
			if (args.length == 0) {
				//Sendet dem Spieler einen Hilfetext.
				player.sendMessage("§7§l§m-------------------------------");
				player.sendMessage("§c/system info §8- §7Zeige wichtige Server Informationen");
				player.sendMessage("§c/system database info §8- §7Zeie datenbank Informationen");
				player.sendMessage("§c/system database clearcache §8- §7Lösche den Cache der Datenbank");
				player.sendMessage("§7§l§m-------------------------------");
			}
			//Abfrage ob die Länge der Argumenten gleich eins ist.
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("info")) {
					SimpleDateFormat sdf = new SimpleDateFormat("d M yyyy HH:mm");
					
					int survival = 0;
					int creative = 0;
					int adventure = 0;
					int spectator = 0;
					int ops = Bukkit.getOperators().size();
					long freemem = (Runtime.getRuntime().freeMemory() / 1000000);
					long maxmem = (Runtime.getRuntime().maxMemory()  / 1000000);
					long usedmem = maxmem - freemem;
					
					for (Player global : Bukkit.getOnlinePlayers()) {
						if (global.getGameMode().equals(GameMode.SURVIVAL)) {survival ++;}
						if (global.getGameMode().equals(GameMode.CREATIVE)) {creative ++;}
						if (global.getGameMode().equals(GameMode.ADVENTURE)) {adventure ++;}
						if (global.getGameMode().equals(GameMode.SPECTATOR)) {spectator ++;}
					}
					
					String opers = "§bOperators\n";
					for (OfflinePlayer p : Bukkit.getOperators()) {
						opers = opers + "§a" + p.getName() + "\n";
					}
					
					String playerinfo = "§9Player Types\n§6Survival: §f" + survival + "\n§6Creative: §f" + creative + "\n§6Adventure: §f" + adventure + "\n§6Spectator: §f" + spectator + "\n§6Operators: §f" + ops; 
					
					String head = "";
					player.sendMessage(head);
					player.sendMessage("§8Last restart: §7" + sdf.format(new Date(main.started)));
					player.spigot().sendMessage(utils.stackComponent
							(utils.stackComponent
									(new TextComponent("§8Players: "), utils.createHoverText("§7" + Bukkit.getOnlinePlayers().size(), playerinfo)), new TextComponent("§8/" + main.getInstance().getServer().getMaxPlayers())));
					player.sendMessage("§8Memory usage: §a" + usedmem + " MB §8/ " + maxmem + " MB");
					player.spigot().sendMessage(utils.createHoverText("§8Operators", opers));
				}
			}

			//Abfrage ob die Länge der Argumenten gleich zwei ist.
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("database")) {
					if (args[1].equalsIgnoreCase("info")) {
						//Sendet dem Spieler die Information ob die verbindung mit dem MySQL server besteht.
						player.sendMessage("§7§l§m-------------------------------");
						player.sendMessage("§7Verbindung: " + (MySQL.isConnected() ? "§aVerbunden" : "§cKeine Verbindung"));
						player.sendMessage("§7§l§m-------------------------------");
					}
					if (args[1].equalsIgnoreCase("clearcache")) {
						SQLData.clearCache();
					}
				}
			}
		}
		return true;
	}

}

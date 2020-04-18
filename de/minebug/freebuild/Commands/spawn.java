package de.minebug.freebuild.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Data.SQLData;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException at 05.11.2018 - 00:41:44
 */

public class spawn implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("spawn")) {
			if (SQLData.WarpExists(".spawnpoint")) {
				player.teleport(SQLData.getWarp(".spawnpoint"));
			} else {
				player.sendMessage("§cDer Spawn punkt ist derzeit nicht erreichbar.");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("setspawn")) {
			if (player.hasPermission("essentials.setspawn")) {
				SQLData.setWarp(".spawnpoint", player.getLocation());
				player.sendMessage("§eSpawn punkt gesetzt.");
			}
		}
		
		return true;
	}
}

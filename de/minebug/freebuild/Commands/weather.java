package de.minebug.freebuild.Commands;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Utils.messages;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException at 05.11.2018 - 16:10:29
 */

public class weather implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		
		if (!sender.hasPermission("essentials.weather")) {
			sender.sendMessage(messages.no_perm);
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("weather")) {
			if (args.length == 1 || args.length == 2) {
				int time = 12000;
				if (args.length == 2) {
					try {
						time = Integer.valueOf(args[1]);
					} catch (NumberFormatException ex) {
						player.sendMessage("§cUngültige Nummer");
						ex.printStackTrace();
						return true;
					}
				}

				player.getWorld().setWeatherDuration(time);
				
				World world = player.getWorld();
				
				switch (args[0]) {
				case "storm": 
					world.setStorm(true);
					world.setThundering(true);
					player.sendMessage("§eIn §c" + world.getName() + " §estürmt es nun.");
					break;
				default:
					world.setStorm(false);
					world.setThundering(false);
					player.sendMessage("§eIn §c" + world.getName() + " §escheint nun die §cSonne§e.");
					break;
				}
			}
		}
		return true;
	}

}

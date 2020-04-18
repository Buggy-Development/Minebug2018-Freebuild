package de.minebug.freebuild.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Utils.messages;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException at 05.11.2018 - 16:43:28
 */

public class fly implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		
		if (!sender.hasPermission("essentials.fly")) {
			sender.sendMessage(messages.no_perm);
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("fly")) {
			if (args.length == 0) {
				if (player.getAllowFlight()) {
					player.setAllowFlight(false);
					player.sendMessage("§eFlugmodus deaktiviert.");
				} else {
					player.setAllowFlight(true);
					player.sendMessage("§eFlugmodus aktiviert.");
				}
			}
			
			if (args.length == 1) {
				if (player.hasPermission("essentials.fly.others")) {
					Player target = Bukkit.getPlayer(args[0]);
					if (target != null && target.isOnline()) {
						if (target.getAllowFlight()) {
							target.setAllowFlight(false);
							target.sendMessage("§eFlugmodus deaktiviert.");
							player.sendMessage("§eFlugmodus von §c" + target.getName() + " §eDeaktiviert.");
						} else {
							target.setAllowFlight(true);
							target.sendMessage("§eFlugmodus aktiviert.");
							player.sendMessage("§eFlugmodus von §c" + target.getName() + " §eAktiviert.");
						}
					}
				} else player.sendMessage(messages.no_perm);
			}
		}
		
		return true;
	}

}

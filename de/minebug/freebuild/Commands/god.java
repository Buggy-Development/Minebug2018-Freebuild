package de.minebug.freebuild.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Utils.messages;
import de.minebug.freebuild.Utils.utils;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException at 05.11.2018 - 17:01:33
 */

public class god implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		
		if (!sender.hasPermission("essentials.god")) {
			sender.sendMessage(messages.no_perm);
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length == 0) {
			if (utils.god_player.contains(player)) {
				utils.god_player.remove(player);
				player.sendMessage("§eDu bist nicht länger Unverwundbar.");
			} else {
				utils.god_player.add(player);
				player.sendMessage("§eDu bist nun Unverwundbar.");
			}
		}
		
		if (args.length == 1) {
			if (player.hasPermission("essentials.god.others")) {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null && target.isOnline()) {
					if (utils.god_player.contains(target)) {
						utils.god_player.remove(target);
						target.sendMessage("§eDu bist nicht länger Unverwundbar.");
						player.sendMessage("§eDer Spieler §c" + target.getName() + " §eist nicht länger Unverwundbar.");
					} else {
						utils.god_player.add(target);
						target.sendMessage("§eDu bist nun Unverwundbar.");
						player.sendMessage("§eDer Spieler §c" + target.getName() + " §eist nun Unverwundbar.");
					}
				}
			}
		}
		
		return true;
	}

}

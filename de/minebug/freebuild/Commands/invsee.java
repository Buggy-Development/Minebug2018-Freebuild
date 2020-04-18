package de.minebug.freebuild.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Utils.messages;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException at 05.11.2018 - 16:50:00
 */

public class invsee implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		
		if (!sender.hasPermission("essentials.invsee")) {
			sender.sendMessage(messages.no_perm);
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("invsee")) {
			
			if (args.length == 1) {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null && target.isOnline()) {
					player.openInventory(target.getInventory());
					player.playSound(player.getEyeLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
				}
			}
			
		}	
		
		return true;
	}

}

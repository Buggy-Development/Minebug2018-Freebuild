package de.minebug.freebuild.Commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minebug.freebuild.Utils.messages;
import de.minebug.freebuild.Utils.utils;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 23.10.2018 - 21:06:19
 */

public class gamemode implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//Abfrage ob der Spieler nicht null ist.
		if ((Player) sender != null) {
			
			Player player = (Player) sender;
			
			//Abfrage ob der Spieler die Berechtigung hat.
			if (player.hasPermission("essentials.gamemode")) {
				//Abfrage ob die Argumenten länge eins ist.
				if (args.length == 1) {
					GameMode mode = getMode(args[0]);
					//Abfrage ob der Spielmodus null ist.
					if (mode != null) {
						//Setzt den Spielmodus des Spielers.
						player.setGameMode(mode);
						player.sendMessage("§6Spielmodus §c"+ utils.setFirstLetterUpperCase(mode.name()) + " §6Für " + player.getName() + " gesetzt.");
					} else player.sendMessage("§cSyntax Error.");
				} else if (args.length == 2 && player.hasPermission("essentials.gamemode.others")) {
					if (Bukkit.getOfflinePlayer(args[0]).isOnline()) {
						Player target = Bukkit.getPlayer(args[0]);
						GameMode mode = getMode(args[1]);
						if (mode != null) {
							target.setGameMode(mode);
							player.sendMessage("§6Spielmodus §c"+ utils.setFirstLetterUpperCase(mode.name()) + " §6Für " + target.getName() + " gesetzt.");
							target.sendMessage("§6Dein Spielmodus wurde geändert auf §c" + utils.setFirstLetterUpperCase(mode.name()));
						} else player.sendMessage("§cSyntax Error.");
					}
				} else player.sendMessage(messages.no_perm);
			} else player.sendMessage(messages.no_perm);
		}
		return true;
	}
	
	//Wandelt ein String in einen GameMode um
	private GameMode getMode(String arg) {
		GameMode mode;
		switch(arg) {
		case "0": mode = GameMode.SURVIVAL; break;
		case "1": mode = GameMode.CREATIVE; break;
		case "2": mode = GameMode.ADVENTURE; break;
		case "3": mode = GameMode.SPECTATOR; break;
		case "survival": mode = GameMode.SURVIVAL; break;
		case "creative": mode = GameMode.CREATIVE; break;
		case "adventure": mode = GameMode.ADVENTURE; break;
		case "spectator": mode = GameMode.SPECTATOR; break;
		case "s": mode = GameMode.SURVIVAL; break;
		case "c": mode = GameMode.CREATIVE; break;
		case "a": mode = GameMode.ADVENTURE; break;
		default: mode = null; break;
		}
		return mode;
	}

}

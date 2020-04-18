package de.minebug.freebuild.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.minebug.freebuild.Data.SQLData;
import de.minebug.freebuild.Utils.utils;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 27.10.2018 - 17:15:01
 */

public class PlayerJoinLeave implements Listener {
	
	public PlayerJoinLeave() {
		utils.registerListener(this);
	}
	
	//Event das ausgeführt wird wenn ein Spieler den Server verlässt.
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		//Löscht den HomeCache des Spielers.
		SQLData.clearUserHomeCache(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()) {
			event.getPlayer().teleport(SQLData.getWarp(".spawnpoint"));
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(SQLData.getWarp(".spawnpoint"));
	}
}

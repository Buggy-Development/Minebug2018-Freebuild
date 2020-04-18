package de.minebug.freebuild.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import de.minebug.freebuild.Utils.utils;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException at 05.11.2018 - 17:07:39
 */

public class EntityDamage implements Listener {
	
	public EntityDamage() {
		utils.registerListener(this);
	}
	
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (utils.god_player.contains((Player)event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}

}

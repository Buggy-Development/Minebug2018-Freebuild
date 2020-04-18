package de.minebug.freebuild.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.minebug.freebuild.Utils.utils;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException at 05.11.2018 - 16:53:17
 */

public class InventoryClick implements Listener {
	
	public InventoryClick() {
		utils.registerListener(this);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		try {
			if (event.getClickedInventory().getHolder() instanceof Player) {
				if (event.getWhoClicked().hasPermission("essentials.invsee.modify")) {
					event.setCancelled(true);
				}
			}
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
		
	}

}

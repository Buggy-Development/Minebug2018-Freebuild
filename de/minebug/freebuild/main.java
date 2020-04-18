package de.minebug.freebuild;

import org.bukkit.plugin.java.JavaPlugin;

import de.minebug.freebuild.Commands.fly;
import de.minebug.freebuild.Commands.gamemode;
import de.minebug.freebuild.Commands.home;
import de.minebug.freebuild.Commands.invsee;
import de.minebug.freebuild.Commands.region;
import de.minebug.freebuild.Commands.spawn;
import de.minebug.freebuild.Commands.system;
import de.minebug.freebuild.Commands.warp;
import de.minebug.freebuild.Commands.weather;
import de.minebug.freebuild.Data.MySQL;
import de.minebug.freebuild.Events.EntityDamage;
import de.minebug.freebuild.Events.InventoryClick;
import de.minebug.freebuild.Events.PlayerJoinLeave;
import de.minebug.freebuild.Events.regionEvents;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 21.10.2018 - 15:04:00
 */

public class main extends JavaPlugin {
	
	private static main instance;
	public static long started;
	
	//Funktion die aufgerufen wird wenn das Plugin geladen wird.
	public void onEnable() {
		started = System.currentTimeMillis(); //Setze startzeit des Plugins.
		main.instance = this;
		MySQL.loadConfiguration();
		MySQL.connect();
		MySQL.createTable();
		
		initializeCommands();
		initializeEvents();
	}
	
	
	//Registriere alle Befehle.
	private void initializeCommands() {
		getCommand("gamemode").setExecutor(new gamemode());
		getCommand("warp").setExecutor(new warp());
		getCommand("warps").setExecutor(new warp());
		getCommand("setwarp").setExecutor(new warp());
		getCommand("delwarp").setExecutor(new warp());
		getCommand("home").setExecutor(new home());
		getCommand("homes").setExecutor(new home());
		getCommand("sethome").setExecutor(new home());
		getCommand("delhome").setExecutor(new home());
		getCommand("system").setExecutor(new system());
		getCommand("region").setExecutor(new region());
		getCommand("spawn").setExecutor(new spawn());
		getCommand("setspawn").setExecutor(new spawn());
		getCommand("weather").setExecutor(new weather());
		getCommand("fly").setExecutor(new fly());
		getCommand("invsee").setExecutor(new invsee());
	}
	
	//Registriere alle Events.
	private void initializeEvents() {
		new regionEvents();
		new PlayerJoinLeave();
		new InventoryClick();
		new EntityDamage();
	}
	
	public static main getInstance() {
		return main.instance;
	}

}

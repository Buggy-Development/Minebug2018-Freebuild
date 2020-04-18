package de.minebug.freebuild.Utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.minebug.freebuild.main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 23.10.2018 - 21:21:28
 */

public class utils {
	
	public static final boolean debug = true;
	public static ArrayList<Player> god_player = new ArrayList<>();
	
	
	//Klasse die aufgerufen wird um Events zu registrieren.
	public static void registerListener(Listener listener) {
		main.getInstance().getServer().getPluginManager().registerEvents(listener, main.getInstance());
	}
	
	//Funktion um den Ersten Buchstaben eines Wortest groß schreiben zu lassen.
	public static String setFirstLetterUpperCase(String text) {

		char firstLetter = text.charAt(0);
		String firstLetterString = firstLetter + "";

		firstLetterString.toUpperCase();
		String endText = text.substring(1, text.length()).toLowerCase();

		String returnStatment = firstLetterString + endText;
		if (returnStatment.contains("_")) {
			returnStatment.replace((CharSequence) "_", (CharSequence) " ");
		}

		return returnStatment;

	}

	//Sende eine Debug Nachricht (für testzwecke)
	public static void sendDeugmsg(String msg) {
		if (debug) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("ssr.owner")) {
					player.sendMessage("§bDEBUG: §c" + msg);
				}
			}
		}
	}
	
	//Erstellet einen Hover text
	public static TextComponent createHoverText(String text, String hovertext) {

		TextComponent t1 = new TextComponent();

		t1.setText(text);
		t1.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(hovertext).create()));

		return t1;
	}

	//Erstellt einen text der einen Befehl ausführen kann.
	public static TextComponent createCommandText(String text, String command) {

		TextComponent t1 = new TextComponent();

		t1.setText(text);
		t1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, command));

		return t1;
	}

	//Erstellt einen text der einen Befehl ausführen kann.
	public static TextComponent createCommandText(TextComponent t1, String command) {

		t1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, command));

		return t1;
	}
	
	//Kombiniert TextComponenten
	public static TextComponent stackComponent(TextComponent t1, TextComponent t2) {
		t1.addExtra(t2);
		return t1;
	}

}

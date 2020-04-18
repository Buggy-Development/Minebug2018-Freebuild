package de.minebug.freebuild.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Chunk;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 21.10.2018 - 18:20:37
 */

public class Region {

	private int RegionID;
	private HashMap<UUID, Integer> userdata;
	private ArrayList<Chunk> chunks;
	private HashMap<String,String> flags;
	
	public Region(int id, HashMap<UUID, Integer> userswithperm, ArrayList<Chunk> chunks, HashMap<String,String> flags) {
		this.RegionID = id;
		this.userdata = userswithperm;
		this.chunks = chunks;
		this.flags = flags;
	}
	
	
	//Gibt die RegionID zurück.
	public int getRegionID() {
		return RegionID;
	}
	
	//Gibt eine Map mit allen Einstellungen zurück.
	public HashMap<String,String> getFlags() {
		return flags;
	}
	
	//Gibt eine Liste aller Chunks zurück.
	public ArrayList<Chunk> getChunks() {
		return chunks;
	}
	
	//Gibt eine Map mit allen Benutzer zurück.
	public HashMap<UUID, Integer> getUserdata() {
		return userdata;
	}
	
}

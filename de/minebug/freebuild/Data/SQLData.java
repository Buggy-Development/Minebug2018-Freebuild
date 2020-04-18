package de.minebug.freebuild.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import de.minebug.freebuild.Utils.Region;
import de.minebug.freebuild.Utils.utils;

/*
 * @author MineBug.de Development
 * Original by: TheHolyException and Julian4060206 at 21.10.2018 - 18:13:53
 * Editded by: Julian4060206 
 */

public class SQLData {


	private static HashMap<Integer, Region> getRegionCache = new HashMap<>();
	
	private static HashMap<Chunk, Integer> getChunkIDCache = new HashMap<>();
	private static HashMap<String, Location> getWarpsCache = new HashMap<>();
	private static HashMap<UUID, HashMap<String,Location>> getHomesCache = new HashMap<>();

	//Abfrage ob die angegebene RegionID in der Datenbank vorhanden ist.
	public static boolean RegionExist(int RegionID) {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Regions WHERE RegionID= '" + RegionID + "'");
			if (rs.next()) {
				return rs.getString("RegionID") != null;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	//Abfrage ob die angegebene ChunkID in der Datenbank vorhanden ist.
	public static boolean ChunkExist(int ChunkID) {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionChunks WHERE ChunkID= '" + ChunkID + "'");
			if (rs.next()) {
				return rs.getString("ChunkID") != null;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	//Abfrage ob der angegebene Chunk in der Datenbank vorhanden ist.
	//Abfrage erfolgt über die Chunk koordinaten.
	public static boolean ChunkExist(Chunk chunk) {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionChunks WHERE WORLD= '"+chunk.getWorld().getName()+"' "
					+ "AND CHUNK_X= '"+chunk.getX()+"' AND CHUNK_Z= '"+chunk.getZ()+"'");
			if (rs.next()) {
				return rs.getString("ChunkID") != null;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	//Registriere eine Region in der Datenbank und gibt die RegionID der erstellten Region zurück.
	public static int registerRegion() {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Regions ORDER BY RegionID DESC LIMIT 1");
			int newregionid;
			
			/*
			 Abfrage ob die Tabelle Leer ist
			 Wenn leer ist die RegionID = 0
		 	 sonst wird die RegionID mit der Höchsten um eins addiert.
			*/
			if (!rs.first()) {
				newregionid = 0;
			} else {
				newregionid = rs.getInt("RegionID")+1;
			}
			
			//Überprüfe ob die RegionID durch Fehler bereits vorhanden ist.
			if (!RegionExist(newregionid)) {
				MySQL.update("INSERT INTO "+MySQL.TABLE_PREFIX+"Regions (RegionID, ChunkID) VALUES ('"+(newregionid)+"', '-1');");
				return newregionid;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	//Registriere einen Chunk in der Datenbank und gibt die ChunkID des erstellten Chunks zurück.
	public static int registerChunk() {
		MySQL.update("INSERT INTO "+MySQL.TABLE_PREFIX+"RegionChunks (WORLD, CHUNK_X, CHUNK_Z) VALUES ('default-world','0','0')");
		ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionChunks ORDER BY ChunkID DESC LIMIT 1");
		try {
			rs.next();
			int i = rs.getInt("ChunkID");
			return i;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//Gibt die ChunkID anhand des Chunks zurück.
	public static Integer getChunkID(Chunk chunk) {
		 
		if (getChunkIDCache.containsKey(chunk)) {
			return getChunkIDCache.get(chunk);
		}
		
		int ID = -1;
		
		if (ChunkExist(chunk)) {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionChunks WHERE WORLD= '"+chunk.getWorld().getName()+"' "
					+ "AND CHUNK_X= '"+chunk.getX()+"' AND CHUNK_Z= '"+chunk.getZ()+"'");
			try {
				rs.next();
				ID = rs.getInt("ChunkID");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ID;
	}
	
	//Gibt die Region anhand der RegionID zurück.
	public static Region getRegion(int RegionID) {
		
		if (getRegionCache.containsKey(RegionID)) {
			return getRegionCache.get(RegionID);
		}
		
		Region rg = null;
		if (RegionExist(RegionID)) {
			try {
				ResultSet rsm = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionMembers WHERE RegionID= '" + RegionID + "'");
				ResultSet rsf = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionFlags WHERE RegionID= '" + RegionID + "'");
				ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Regions WHERE RegionID='" + RegionID + "'");
				
				
				
				HashMap<UUID, Integer> userwithperm = new HashMap<>();
				ArrayList<Chunk> claimedchunks = new ArrayList<>();
				HashMap<String,String> flags = new HashMap<String,String>();
				
				ResultSet rscc = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionChunks");
				
				//Überprüfe ob die Tabelle RegionChunks leer ist.
				if (rscc.first()) {
					while (rs.next()) {
						Chunk chunk = null;
						try {
							int ChunkID = rs.getInt("ChunkID");
							//Überprüfe ob werte in der Tabelle RegionChunks vorhanden sind mit der angegebenen ChunkID.
							ResultSet rsc = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionChunks WHERE ChunkID='" + ChunkID + "'");
							if (rsc.first()) {
								int x = rsc.getInt("CHUNK_X");
								int z = rsc.getInt("CHUNK_Z");
								World world = Bukkit.getWorld(rsc.getString("WORLD"));
								chunk = world.getChunkAt(x, z);
							}
						} catch (RuntimeException e) {
							continue;
						}
						claimedchunks.add(chunk);
					}
				}
				
				if (rsm.first()) {
					rsm.previous();
					while (rsm.next()) {
						UUID uuid;
						Integer perm;
						try {
							uuid = UUID.fromString(rsm.getString("USER"));
							perm = rsm.getInt("PERMISSIONS");
						} catch (RuntimeException e) {
							continue;
						}
						userwithperm.put(uuid, perm);
					}
				}
				
				if (rsf.first()) {
					rsf.previous();
					while (rsf.next()) {
						String flag;
						String value;
						try {
							flag = rsf.getString("FLAG");
							value = rsf.getString("VALUE");
						} catch (RuntimeException e) {
							continue;
						}
						flags.put(flag,value);
					}
				}
				
				rg = new Region(RegionID, userwithperm, claimedchunks, flags);
				
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} 
		getRegionCache.put(RegionID, rg);
		return rg;
	}
	
	
	public static boolean addChunk(Region region, Chunk chunk) {
		
		if (getRegionCache.containsKey(region.getRegionID())) {
			getRegionCache.remove(region.getRegionID());
		}
		
		System.out.println(RegionExist(region.getRegionID()) + " - " + ChunkExist(chunk));
		if (RegionExist(region.getRegionID()) && !ChunkExist(chunk)) {
			if (region.getChunks().contains(chunk)) {
				return false;
			}
			
			int ChunkID = registerChunk();
			MySQL.update("UPDATE "+MySQL.TABLE_PREFIX+"RegionChunks SET CHUNK_X='"+chunk.getX()+"', CHUNK_Z='"+chunk.getZ()+"', WORLD='"+chunk.getWorld().getName()+"' WHERE ChunkID='"+ChunkID+"'");
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Regions WHERE RegionID='"+region.getRegionID()+"' AND ChunkID='-1'");
			try {
				if (rs.first()) {
					MySQL.update("UPDATE "+MySQL.TABLE_PREFIX+"Regions SET ChunkID='"+ChunkID+"' WHERE RegionID='"+region.getRegionID()+"'");
				} else {
					MySQL.update("INSERT INTO "+MySQL.TABLE_PREFIX+"Regions (RegionID, ChunkID) VALUES ('"+region.getRegionID()+"','"+ChunkID+"')");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}	
		
		return true;
	}
	
	//Löscht eine Region.
	public static boolean removeChunk(Region region, Chunk chunk) {
		
		if (getRegionCache.containsKey(region.getRegionID())) {
			getRegionCache.remove(region.getRegionID());
		}
		
		System.out.println(RegionExist(region.getRegionID()) + " - " + ChunkExist(chunk));
		if (RegionExist(region.getRegionID()) && ChunkExist(chunk)) {
			if (!region.getChunks().contains(chunk)) {
				return false;
			}
			
			MySQL.update("DELETE FROM "+MySQL.TABLE_PREFIX+"RegionChunks WHERE ChunkID='"+getChunkID(chunk)+"'");
		} else {
			return false;
		}
		return true;
	}
	
	//Fügt einen Spieler zu einer REgion hinzu.
	public static boolean addUser(Region region, UUID player, int PermissionValue) {
		
		if (getRegionCache.containsKey(region.getRegionID())) {
			getRegionCache.remove(region.getRegionID());
		}
		
		if (RegionExist(region.getRegionID())) {
			if (region.getUserdata().containsKey(player)) {
				return false;
			}
			
			MySQL.update("INSERT INTO "+MySQL.TABLE_PREFIX+"RegionMembers (RegionID, USER, PERMISSIONS) VALUES ("+region.getRegionID()+", '"+player.toString()+"', '"+PermissionValue+"')");
		}
		return true;
	}
	
	//Entfernt einen Spieler von einer Region.
	public static boolean removeUser(Region region, UUID player) {
		
		if (getRegionCache.containsKey(region.getRegionID())) {
			getRegionCache.remove(region.getRegionID());
		}
		
		if (RegionExist(region.getRegionID())) {
			if (!region.getUserdata().containsKey(player)) {
				return false;
			}
			MySQL.update("DELETE FROM "+MySQL.TABLE_PREFIX+"RegionMembers WHERE USER='"+player.toString()+"'");
		}
		return true;
	}
	
	//Setzt die Einstellungen einer Region.
	public static boolean setFlags(Region region, HashMap<String,String> flags) {
		
		if (getRegionCache.containsKey(region.getRegionID())) {
			getRegionCache.remove(region.getRegionID());
		}
		
		if (RegionExist(region.getRegionID())) {
			int RegionID = region.getRegionID();
			MySQL.update("DELETE FROM "+MySQL.TABLE_PREFIX+"RegionFlags WHERE RegionID='"+RegionID+"'");
			if (flags != null) {
				for (String flag : flags.keySet()) {
					MySQL.update("INSERT INTO "+MySQL.TABLE_PREFIX+"RegionFlags (RegionID, FLAG, VALUE) VALUES ('"+RegionID+"','"+flag+"','"+flags.get(flag)+"')");
				}
			}
		}
		return true;
	}
	
	//Abfrage ob ein Spieler in irgendeiner einer Region hinzugefügt ist.
	public static boolean isPlayerInRegion(UUID uuid) {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM " + MySQL.TABLE_PREFIX + "RegionMembers WHERE USER='" + uuid.toString() + "'");
			if (rs.next()) {
				return rs.getString("USER") != null;
			}
		} catch (Exception ex) {
		}
		return false;
	}
	
	//Abfrage ob ein Spieler in der angegebenen Region hinzugefügt ist.
	public static boolean isPlayerInRegion(UUID uuid, int RegionID) {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM " + MySQL.TABLE_PREFIX + "RegionMembers WHERE USER='" + uuid.toString() + "' AND RegionID='"+RegionID+"'");
			if (rs.next()) {
				return rs.getString("USER") != null;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	//Gibt die Region zurück die dem Spieler gehört,
	//wenn der Spieler keine besitzt gibt er diese zurück in die er hinzugefügt wurde
	public static int getPlayersRegion(UUID uuid) {
		int region = -1;
		if (isPlayerInRegion(uuid)) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM " + MySQL.TABLE_PREFIX + "RegionMembers WHERE USER='" + uuid.toString() + "' ORDER BY PERMISSIONS DESC LIMIT 1");
				rs.next();
				region = rs.getInt("RegionID");
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return region;
	}
	
	//Gibt alle Regionen zurück in die der Spieler hinzugefügt wurde/besitzt.
	public static ArrayList<Region> getPlayerRegions(UUID uuid) {
		ArrayList<Region> regions = new ArrayList<>();
		if (isPlayerInRegion(uuid)) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"RegionMembers WHERE USER='"+uuid.toString()+"'");
				if (rs.first()) {
					rs.previous();
					while (rs.next()) {
						regions.add(getRegion(rs.getInt("RegionID")));
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		return regions;
	}
	
	//Setze die Rechte eines Spielers in einer Region.
	public static void setPlayerPermissions(Region region, UUID uuid, int perm) {
		if (isPlayerInRegion(uuid)) {
			MySQL.update("UPDATE "+MySQL.TABLE_PREFIX+"RegionMembers SET PERMISSIONS='"+perm+"' WHERE RegionID='"+region.getRegionID()+"' AND USER='"+uuid.toString()+"'");
		}
	}
	
	//Gibt die RegionID anhand der ChunkID wieder.
	public static int getRegionIDbyChunk(int ChunkID) {
		int id = -1;
		if (ChunkExist(ChunkID)) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Regions WHERE ChunkID='"+ChunkID+"'");
				rs.next();
				id = rs.getInt("RegionID");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}
	
	//Abfrage ob der angegebene Warp-Punkt existiert.
	public static boolean WarpExists(String warpname) {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Warps WHERE NAME='"+warpname.toLowerCase()+"'");
			if (rs.next()) {
				return rs.getString("NAME") != null;
			}
		} catch (SQLException ex)  {
			ex.printStackTrace();
		}
		return false;
	}
	
	//Setzt einen Warppunkt.
	public static void setWarp(String warpname, Location loc) {
		
		getWarpsCache = new HashMap<>();
		
		if (WarpExists(warpname.toLowerCase())) {
			return;
		}
		MySQL.update("INSERT INTO "+MySQL.TABLE_PREFIX+"Warps (NAME, WORLD, X, Y, Z, YAW, PITCH) VALUES"
				+ " ('"+warpname.toLowerCase()+"', '"+loc.getWorld().getName().toLowerCase()+"', '"+loc.getX()+"', '"+loc.getY()+"', '"+loc.getZ()+"', '"+loc.getYaw()+"', '"+loc.getPitch()+"')");
	}
	
	//Lösche einen Warppunkt.
	public static void removeWarp(String warpname) {
		
		getWarpsCache = new HashMap<>();
		
		if (WarpExists(warpname.toLowerCase())) {
			MySQL.update("DELETE FROM "+MySQL.TABLE_PREFIX+"Warps WHERE NAME='"+warpname.toLowerCase()+"'");
		}
	}
	
	//Gibt den Standort des angegebenen warpnames wieder.
	public static Location getWarp(String warpname) {
		
		warpname = warpname.toLowerCase();
		if (getWarpsCache.containsKey(warpname)) {
			return getWarpsCache.get(warpname);
		}
		return getWarps().get(warpname);
	}
	
	//Gibt alle Warps die erstellt wurden wieder.
	public static HashMap<String, Location> getWarps() {
		
		if (!getWarpsCache.isEmpty()) {
			return getWarpsCache;
		}
		
		HashMap<String, Location> locs = new HashMap<>();
		
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Warps");
			while (rs.next()) {
				Location loc = new Location(Bukkit.getWorld(rs.getString("WORLD")), rs.getFloat("X"), rs.getFloat("Y"), rs.getFloat("Z"), rs.getFloat("YAW"), rs.getFloat("PITCH"));
				locs.put(rs.getString("NAME"), loc);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return locs;
	}
	
	
	//Abfrage ob der Spieler den angegebenen Home-Punkt besitzt.
	public static boolean HomeExists(UUID uuid, String name) {
		
		if (getHomesCache.containsKey(uuid) && getHomesCache.get(uuid).containsKey(name)) {
			return getHomesCache.get(uuid).containsKey(name);
		}
		
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Homes WHERE UUID='"+uuid.toString()+"' AND NAME='"+name.toLowerCase()+"'");
			if (rs.next()) {
				return rs.getString("NAME") != null;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	//Setzt einen HomePunkt für einen Spieler.
	public static void setHome(UUID uuid, String name, Location loc) {
		
		if (getHomesCache.containsKey(uuid)) {
			getHomesCache.remove(uuid);
		}
		
		if (HomeExists(uuid, name.toLowerCase())) {
			return;
		}
		MySQL.update("INSERT INTO "+MySQL.TABLE_PREFIX+"Homes (NAME, UUID, WORLD, X, Y, Z, YAW, PITCH) VALUES"
				+ " ('"+name.toLowerCase()+"', '"+uuid.toString()+"','"+loc.getWorld().getName().toLowerCase()+"', '"+loc.getX()+"', '"+loc.getY()+"', '"+loc.getZ()+"', '"+loc.getYaw()+"', '"+loc.getPitch()+"')");
	}
	
	//Lösche einen Home-Punkt 
	public static void removeHome(UUID uuid, String name) {
		
		if (getHomesCache.containsKey(uuid)) {
			getHomesCache.remove(uuid);
		}
		
		if (HomeExists(uuid, name.toLowerCase())) {
			MySQL.update("DELETE FROM "+MySQL.TABLE_PREFIX+"Homes WHERE NAME='"+name.toLowerCase()+"' AND UUID='"+uuid.toString()+"'");
		}
	}
	
	//Gibt den Home-Punkt eines Spielers zurück.
	public static Location getHome(UUID uuid, String name) {
		
		name = name.toLowerCase();
		
		if (getHomesCache.containsKey(uuid) && getHomesCache.get(uuid).containsKey(name)) {
			return getHomesCache.get(uuid).get(name);
		}		
		return getHomes(uuid).get(name);
	}

	//Gibt alle Home-Punkte eines Spielers zurück.
	public static HashMap<String, Location> getHomes(UUID uuid) {
		
		if (getHomesCache.containsKey(uuid)) {
			return getHomesCache.get(uuid);
		}
		
		HashMap<String, Location> locs = new HashMap<>();
		
		try {
			ResultSet rs = MySQL.query("SELECT * FROM "+MySQL.TABLE_PREFIX+"Homes WHERE UUID='"+uuid.toString()+"'");
			while (rs.next()) {
				Location loc = new Location(Bukkit.getWorld(rs.getString("WORLD")), rs.getFloat("X"), rs.getFloat("Y"), rs.getFloat("Z"), rs.getFloat("YAW"), rs.getFloat("PITCH"));
				locs.put(rs.getString("NAME"), loc);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		getHomesCache.put(uuid, locs);
		
		return locs;
	}
	
	
	//Lösche den Home-Cache eines Spielers.
	public static void clearUserHomeCache(UUID uuid) {
		if (getHomesCache.containsKey(uuid)) {
			getHomesCache.remove(uuid);
		}
	}
	
	//Lösche den Cache Vollständig.
	public static void clearCache() {
		getChunkIDCache = new HashMap<>();
		getHomesCache = new HashMap<>();
		getWarpsCache = new HashMap<>();
		getRegionCache = new HashMap<>();
	}
	
}

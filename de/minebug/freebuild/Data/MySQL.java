package de.minebug.freebuild.Data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/*
 * @author MineBug.de Development
 * Original by: TheHolyException and Julian4060206 at 21.10.2018 - 15:07:12
 * Editded by: Julian4060206 
 */

public class MySQL {

	//Lade die MySQL Anmeldedaten.
	public static void loadConfiguration() {
		File file = new File("plugins//FreebuildSystem//MySQL.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage(prefix + "§eError while creating §eMySQL.yml §e:" + e.getMessage());
				return;
			}
		}
		
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		
		try {
			HOST = cfg.getString("HOST");
			PORT = cfg.getString("PORT");
			DATABASE = cfg.getString("DATABASE");
			USER = cfg.getString("USER");
			PASSWORD = cfg.getString("PASSWORD");
			TABLE_PREFIX = cfg.getString("TABLE_PREFIX");
		} catch (NullPointerException e) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§eError while reading out §eMySQL.yml §e:" + e.getMessage());
		}
		
	}

	private static String HOST;
	private static String PORT;
	private static String DATABASE;
	private static String USER;
	private static String PASSWORD;
	protected static String TABLE_PREFIX;

	private static Connection con;

	private static String prefix = "§7[§cMySQL§7] §e";

	//Versuche die verbindung mit der Datenbank aufzubauen.
	public static void connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE, USER, PASSWORD);
			Bukkit.getConsoleSender().sendMessage(prefix + "Die Verbindung zur MySQL wurde aufgebaut!");
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage(prefix + "Die Verbindung zur MySQL ist fehlgeschlagen §4Fehler: §c" + e.getMessage() + "§e.");
		}
	}

	//Versuche die verbindung mit der Datenbank zu trennen.
	public static void close() {
		try {
			if (con != null) {
				con.close();
				Bukkit.getConsoleSender().sendMessage(prefix + "Die Verbindung zur MySQL wurde abgebrochen!");
			}
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage(prefix + "Fehler beim beenden der MySQL Verbindung §4Fehler: §c" + e.getMessage() + "§e.");
		}
	}

	//Sendet einen SQL Befehl an die Datenbank.
	public static void update(String qry) {
		try {
			Statement st = con.createStatement();
			st.executeUpdate(qry);
			st.close();
		} catch (SQLException e) {
			connect();
			System.err.println(e);
		}
	}

	//Abfrage ob Verbindung mit der Datenbank besteht.
	public static boolean isConnected() {
		return con != null;
	}

	//Versuche die Tabellen zu erstellen.
	public static void createTable() {
		if (isConnected()) {
			try {
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PREFIX + "Regions (RegionID INT(11), ChunkID INT(11), PRIMARY KEY(ChunkID))");
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PREFIX + "RegionChunks (ChunkID INT(11)PRIMARY KEY NOT NULL AUTO_INCREMENT, WORLD VARCHAR(64), CHUNK_X INT(11), CHUNK_Z INT(11))");
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PREFIX + "RegionMembers (RegionID INT(11), USER VARCHAR(64), PERMISSIONS INT(11))");
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PREFIX + "RegionFlags (RegionID INT(11), FLAG VARCHAR(64), VALUE VARCHAR(64))");
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PREFIX + "RegionData (RegionID INT(11), NAME VARCHAR(64), CREATOR VARCHAR(64))");
				
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PREFIX + "Warps (NAME VARCHAR(255), WORLD VARCHAR(255), X FLOAT(30), Y FLOAT(30), Z FLOAT(30), YAW FLOAT(30), PITCH FLOAT(30))");
				con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PREFIX + "Homes (NAME VARCHAR(255), UUID VARCHAR(64), WORLD VARCHAR(255), X FLOAT(30), Y FLOAT(30), Z FLOAT(30), YAW FLOAT(30), PITCH FLOAT(30))");
				
				
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	//Sendet einen SQL Befehl mit Rückgabe an die Datenbank.
	public static ResultSet query(String qry) {
		ResultSet rs = null;

		try {
			Statement st = con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			connect();
			System.err.println(e);
		}
		return rs;
	}
}

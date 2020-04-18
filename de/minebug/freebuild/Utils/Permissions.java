package de.minebug.freebuild.Utils;

/*
 * @author MineBug.de Development
 *  
 * Created by TheHolyException and Julian4060206 at 25.10.2018 - 19:20:44
 */

public enum Permissions {
	
	Besitzer(99),
	Moderator(80),
	Normal(0);
	
	int permissiondata;
	
	Permissions(int permissiondata) {
		this.permissiondata = permissiondata;
	}
	
	//Gibt die Berechtigungsdaten zurück.
	public int getPermissiondata() {
		return permissiondata;
	}
	
}

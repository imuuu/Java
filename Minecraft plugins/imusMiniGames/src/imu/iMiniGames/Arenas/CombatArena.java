package imu.iMiniGames.Arenas;

import org.bukkit.Location;

public class CombatArena extends Arena
{
	Location arenas_middleloc = null;

	
	public CombatArena(String name) 
	{
		super(name);
	}
	
	public Location getArenas_middleloc() {
		return arenas_middleloc;
	}

	public void setArenas_middleloc(Location arenas_middleloc) {
		this.arenas_middleloc = arenas_middleloc;
	}

}

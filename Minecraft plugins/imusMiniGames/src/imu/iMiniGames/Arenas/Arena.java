package imu.iMiniGames.Arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class Arena 
{
	String _name;
	String _displayName;
	String _description = "";
	
	

	int _maxPlayers = 0;
	
	ArrayList<Location> _spawnPositions = new ArrayList<>();
	
	public Arena(String name) 
	{	
		_name = name;
		_displayName = name;
		
	}
	
	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_displayName() {
		return _displayName;
	}

	public void set_displayName(String _displayName) {
		this._displayName = _displayName;
	}

	public int get_maxPlayers() {
		return _maxPlayers;
	}

	public void set_maxPlayers(int _maxPlayers) 
	{
		this._maxPlayers = _maxPlayers;
	}
	
	public void addSpawnPosition(Location loc)
	{
		_spawnPositions.add(loc);

	}
	public int getTotalSpawnPositions()
	{
		return _spawnPositions.size();
	}
	
	public Location getSpawnpointLoc(int idx)
	{
		if(_spawnPositions.isEmpty())
		{
			return new Location(Bukkit.getWorlds().get(0),0,0,0);
		}
				
		return _spawnPositions.get(idx);
	}
	
	public void clearSpawnPositions()
	{
		_spawnPositions = new ArrayList<>();
		_maxPlayers = 0;
	}

	public String get_description() {
		return _description;
	}

	public void set_description(String _description) {
		this._description = _description;
	}
	
	
}

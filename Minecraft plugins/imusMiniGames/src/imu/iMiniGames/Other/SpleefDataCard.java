package imu.iMiniGames.Other;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class SpleefDataCard 
{
	Player _owner;
	

	HashMap<Integer, String> _invDataValues = new HashMap<>();

	
	public SpleefDataCard(Player owner)
	{
		_owner = owner;
	}
	public HashMap<Integer, String> get_invDataValues() {
		return _invDataValues;
	}
	
	public void removeDataValue(int i)
	{
		_invDataValues.remove(i);
	}
	
	public void putDataValue(int i, String s)
	{
		_invDataValues.put(i, s);
	}
	
	public String getDataValue(int i)
	{
		return _invDataValues.get(i);
	}
	
	public Player get_owner() {
		return _owner;
	}
}

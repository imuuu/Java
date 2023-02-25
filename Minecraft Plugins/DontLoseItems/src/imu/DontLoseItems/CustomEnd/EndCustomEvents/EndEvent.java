package imu.DontLoseItems.CustomEnd.EndCustomEvents;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;

import imu.iAPI.Other.Metods;
import net.md_5.bungee.api.ChatColor;

public abstract class EndEvent
{
	private String _name;
	private boolean _stay = false;
	private double _duration = 0;
	
	private HashSet<UUID> _activePlayer;
	public EndEvent(String name, double duration)
	{
		_name = name;
		_activePlayer = new HashSet<>();
	}
	
	public void AddPlayer(Player player)
	{
		if(_activePlayer.contains(player.getUniqueId()));
		_activePlayer.add(player.getUniqueId());
	}
	
	public void RemovePlayer(Player player)
	{
		if(!_activePlayer.contains(player.getUniqueId())) return;
		
		_activePlayer.remove(player.getUniqueId());
 	}
	
	public void ClearPlayers()
	{
		_activePlayer.clear();
	}
	public boolean HasPlayer(Player player)
	{
		return _activePlayer.contains(player.getUniqueId());
	}
	public UUID[] GetPlayers()
	{
		return _activePlayer.toArray(new UUID[_activePlayer.size()]);
	}
	public EndEvent SetStay(boolean b)
	{
		_stay = b;
		return this;
	}
	
	public String GetName()
	{
		return _name;
	}
	public abstract void OnEventStart();
	public abstract void OnEventEnd();
	public abstract String GetEventName();
	public abstract String GetRewardInfo();
	
	public void PrintToPlayer(Player player)
	{
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage(ChatColor.BLUE+"======================");
		
		if(GetEventName() != null) 	player.sendMessage(Metods.msgC("&4Name &2"+GetEventName()));
		if(_duration >= 0) 		   	player.sendMessage(Metods.msgC("&4Duration: &2"+(int)_duration)); 
		
		
		player.sendMessage(ChatColor.BLUE+"======================");
	}
}

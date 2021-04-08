package imu.iMiniGames.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import imu.iMiniGames.Arenas.Arena;

public class GameCard 
{
	protected Player _maker;
	protected HashMap<UUID,Boolean> _players_accept = new HashMap<>();
	protected double _bet;
	protected double _total_bet;
	
	protected Arena _arena;

	public Player get_maker() {
		return _maker;
	}
	public void set_maker(Player _maker) {
		this._maker = _maker;
	}

	public HashMap<UUID, Boolean> get_players_accept() {
		return _players_accept;
	}
	
	public double get_bet() {
		return _bet;
	}
	public void set_bet(double _bet) {
		this._bet = _bet;
	}
	
	public double get_total_bet() {
		return _total_bet;
	}
	public void set_total_bet(double _total_bet) {
		this._total_bet = _total_bet;
	}
	public void putPlayer(UUID uuid)
	{
		_players_accept.put(uuid, false);
	}
	
	public boolean isPlayerInThisCard(UUID uuid)
	{
		if(_players_accept.get(uuid) != null)
		{
			return true;
		}
		return false;
	}
		
	/*
	 * Return true if all players has accepted
	 */
	public boolean putPlayerAccept(UUID uuid)
	{
		_players_accept.put(uuid, true);
		return isAllAccepted();
	}
	
	public String getPlayersString()
	{
		String str ="";
		for(Map.Entry<UUID,Boolean> entry : _players_accept.entrySet() )
		{
			Player p = Bukkit.getPlayer(entry.getKey());
			if(p != null)
			{
				str+=p.getName()+" ";
			}
			
		}
		return str;
	}
	
	boolean isAllAccepted()
	{
		for(Map.Entry<UUID,Boolean> entry : _players_accept.entrySet() )
		{
			if(!entry.getValue())
				return false;
		}
		
		return true;
	}
	
	public boolean isEveryPlayerAvailable()
	{
		for(Map.Entry<UUID,Boolean> entry : _players_accept.entrySet() )
		{
			Player p = Bukkit.getPlayer(entry.getKey());
			if(p == null)
			{
				return false;
			}
		}
		return true;
	}
	
	public void sendMessageToALL(String str)
	{
		for(Map.Entry<UUID,Boolean> entry : _players_accept.entrySet() )
		{
			Player p = Bukkit.getPlayer(entry.getKey());
			if(p != null)
			{
				p.sendMessage(str);
			}
			
		}
	}
	public void sendMessageToALL(String str, ArrayList<Player> but_not_for_them)
	{
		for(Map.Entry<UUID,Boolean> entry : _players_accept.entrySet() )
		{
			Player p = Bukkit.getPlayer(entry.getKey());
			if(p != null)
			{
				
				if(!but_not_for_them.contains(p))
				{
					p.sendMessage(str);
				}
					
			}
				
		}
	}
	public void sendMessageToALL(String str, Player but_not_for)
	{
		for(Map.Entry<UUID,Boolean> entry : _players_accept.entrySet() )
		{
			Player p = Bukkit.getPlayer(entry.getKey());
			if(p != null)
			{
				if(but_not_for != p)
				{
					p.sendMessage(str);
				}
			}
			
				
		}
	}
	
	/*
	 * Return true if all players has accepted
	 */
	public boolean putPlayerAccept(Player p)
	{
		_players_accept.put(p.getUniqueId(), true);
		return isAllAccepted();
	}
}

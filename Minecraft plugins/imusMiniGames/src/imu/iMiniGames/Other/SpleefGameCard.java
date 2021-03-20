package imu.iMiniGames.Other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import imu.iMiniGames.Arenas.SpleefArena;

public class SpleefGameCard 
{
	Player _maker;
	SpleefDataCard _spleefDataCard;
	
	SpleefArena _arena = null;
	
	double _bet;
	double _total_bet;

	HashMap<Player,Boolean> _players_accept = new HashMap<>();
	
	public Player get_maker() {
		return _maker;
	}
	public void set_maker(Player _maker) {
		this._maker = _maker;
	}

	public HashMap<Player, Boolean> get_players_accept() {
		return _players_accept;
	}
	public SpleefArena get_arena() {
		return _arena;
	}
	public void set_arena(SpleefArena _arena) {
		this._arena = _arena;
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
	
	public void putPlayer(Player p)
	{
		_players_accept.put(p, false);
	}
	public SpleefDataCard get_spleefDataCard() {
		return _spleefDataCard;
	}
	public void set_spleefDataCard(SpleefDataCard _spleefDataCard) {
		this._spleefDataCard = _spleefDataCard;
	}
	
	public boolean isPlayerInThisCard(Player p)
	{
		if(_players_accept.get(p) != null)
		{
			return true;
		}
		return false;
	}
	
	/*
	 * Return true if all players has accepted
	 */
	public boolean putPlayerAccept(Player p)
	{
		_players_accept.put(p, true);
		return isAllAccepted();
	}
	
	public String getPlayersString()
	{
		String str ="";
		for(Map.Entry<Player,Boolean> entry : _players_accept.entrySet() )
		{
			str+=entry.getKey().getName()+" ";
		}
		return str;
	}
	
	boolean isAllAccepted()
	{
		for(Map.Entry<Player,Boolean> entry : _players_accept.entrySet() )
		{
			if(!entry.getValue())
				return false;
		}
		
		return true;
	}
	
	public boolean isEveryPlayerAvailable()
	{
		for(Map.Entry<Player,Boolean> entry : _players_accept.entrySet() )
		{
			Player p = Bukkit.getPlayer(entry.getKey().getUniqueId());
			if(p == null)
			{
				return false;
			}
		}
		return true;
	}
	
	public void sendMessageToALL(String str)
	{
		for(Map.Entry<Player,Boolean> entry : _players_accept.entrySet() )
		{
			entry.getKey().sendMessage(str);
		}
	}
	public void sendMessageToALL(String str, ArrayList<Player> but_not_for_them)
	{
		for(Map.Entry<Player,Boolean> entry : _players_accept.entrySet() )
		{
			if(!but_not_for_them.contains(entry.getKey()))
			{
				entry.getKey().sendMessage(str);
			}
				
		}
	}
	public void sendMessageToALL(String str, Player but_not_for)
	{
		for(Map.Entry<Player,Boolean> entry : _players_accept.entrySet() )
		{
			if(but_not_for != entry.getKey())
			{
				entry.getKey().sendMessage(str);
			}
				
		}
	}
	
	
	
	
}

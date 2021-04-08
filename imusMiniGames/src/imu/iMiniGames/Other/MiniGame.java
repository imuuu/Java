package imu.iMiniGames.Other;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import imu.iMiniGames.Main.Main;
import net.md_5.bungee.api.ChatColor;

public class MiniGame 
{
	protected Main _main;
	protected ItemMetods _itemM;
	String _miniGameName;
	
	int _roundTime = 0;
	protected int _round = 0;
	
	HashMap<Player, Integer> _players_score = new HashMap<>();
	HashMap<Player, Integer> _players_lobby = new HashMap<>();
	
	HashMap<Player, PlayerDataCard> _players_spectators = new HashMap<>();
	
	protected Location _spectator_loc = null;
	
	public MiniGame(Main main, String minigameName)
	{
		_main = main;
		_itemM = main.get_itemM();
		_miniGameName= minigameName;
	}
	
	public void addPlayer(Player p)
	{
		_players_score.put(p, 0);
	}
	
	public int get_roundTime() {
		return _roundTime;
	}

	public void set_roundTime(int _roundTime) 
	{
		this._roundTime = _roundTime;
	}
	
	public void movePlayerToLobbyHash(Player p)
	{
		_players_lobby.put(p, _players_score.get(p));
		_players_score.remove(p);
	}
	
	public void addPointsPlayer(Player p,int amount)
	{
		int score = _players_score.get(p) + amount;
		_players_score.put(p, score);
	}
	
	public void addLobbyPlayersToScore()
	{
		for(Entry<Player,Integer> p : _players_lobby.entrySet())
		{
			_players_score.put(p.getKey(), p.getValue());
		}
		_players_lobby.clear();
	}
	
	public void addSpectator(Main main, Player p)
	{
		_players_spectators.put(p, new PlayerDataCard(main, p, "null"));
	}
	
	public void teleportSpectatorToSpectate(Player p)
	{
		if(_players_spectators.containsKey(p) && _spectator_loc != null)
		{
			p.teleport(_spectator_loc);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9You have teleported to spectate! You need to watch whole match order to leave :)"));
		}
	}
	
	public void teleportSpectatorToBack(Player p)
	{
		if(_players_spectators.containsKey(p))
		{
			p.teleport(_players_spectators.get(p).get_location());
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9You have teleported to back!"));
			_players_spectators.remove(p);
		}
	}
	public void teleportSpectatorAllBack()
	{
		for(Entry<Player,PlayerDataCard> entry : _players_spectators.entrySet())
		{
			entry.getKey().teleport(entry.getValue().get_location());
		}
		_players_spectators.clear();
	}
	
	public Collection<PlayerDataCard> getSpectators()
	{
		return _players_spectators.values();
	}
	
}

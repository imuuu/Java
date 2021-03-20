package imu.iMiniGames.Other;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import imu.iMiniGames.Main.Main;

public class MiniGame 
{
	protected Main _main;
	protected ItemMetods _itemM;
	String _miniGameName;
	
	int _roundTime = 0;
	protected int _round = 0;
	
	HashMap<Player, Integer> _players_score = new HashMap<>();
	HashMap<Player, Integer> _players_lobby = new HashMap<>();
	
	
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
	
	public void movePlayerToLobby(Player p)
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
}

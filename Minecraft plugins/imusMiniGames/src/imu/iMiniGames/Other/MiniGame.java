package imu.iMiniGames.Other;

import java.util.HashMap;

import org.bukkit.entity.Player;

import imu.iMiniGames.Main.Main;

public class MiniGame 
{
	protected Main _main;
	protected ItemMetods _itemM;
	String _miniGameName;
	
	int _roundTime = 0;
	
	

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

	public void set_roundTime(int _roundTime) {
		this._roundTime = _roundTime;
	}
}

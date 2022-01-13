package imu.iMiniGames.Leaderbords;

import java.util.HashMap;
import java.util.UUID;

import imu.iMiniGames.Main.Main;

public abstract class Leaderboard
{
	Main _main;
	String _name = "";
	String _path = "";
	
	HashMap<UUID, PlayerBoard> _boards_alltime = new HashMap<>();
	LeaderboardUUIDData _uuidData;
	
	public Leaderboard(Main main, String name) 
	{
		_name = name;
		_main = main;
		_uuidData = main.get_leaderboardUUIDData();
	}
	
	public String getPlayerName(UUID uuid)
	{
		return _uuidData.getName(uuid);
	}
	
	public HashMap<UUID, PlayerBoard> getBoards()
	{
		return _boards_alltime;
	}
}

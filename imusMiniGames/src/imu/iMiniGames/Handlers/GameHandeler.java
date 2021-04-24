package imu.iMiniGames.Handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import imu.iMiniGames.Interfaces.IGameHandeler;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.CombatManager;
import imu.iMiniGames.Other.Cooldowns;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.Other.PlayerDataCard;
import net.milkbowl.vault.economy.Economy;

public abstract class GameHandeler implements IGameHandeler
{
	Main _main;
	ItemMetods _itemM;
	CombatManager _combatManager;
	Economy _econ;
	Cooldowns _cd;
	
	HashMap<UUID,String> _request_arenas = new HashMap<>();
	HashMap<UUID,Boolean> _hasAccepted = new HashMap<>();
	HashMap<UUID,PlayerDataCard> _player_datas = new HashMap<>();	

	boolean _enable_broadcast = true;
	double _bet_fee_percent = 0.05;
	
	String _cd_invite = "invite_";
	int _cd_invite_time = 10; //seconds
	int _roundTime = 600;
	String _playerDataFolderName="Combat";
		
	public GameHandeler(Main main) 
	{
		_main = main;
		_itemM = main.get_itemM();
		_combatManager = main.get_combatManager();
		_cd = new Cooldowns();
		_econ = main.get_econ();

	}
	
	public boolean isAccepted(Player p)
	{
		return _hasAccepted.containsKey(p.getUniqueId());
	}
	
	public boolean is_enable_broadcast() {
		return _enable_broadcast;
	}
	
	public double getBet_fee_percent() {
		return _bet_fee_percent;
	}


	public void setBet_fee_percent(double bet_fee_percent) {
		this._bet_fee_percent = bet_fee_percent;
	}


	public int getCd_invite_time() {
		return _cd_invite_time;
	}


	public void setCd_invite_time(int cd_invite_time) {
		this._cd_invite_time = cd_invite_time;
	}


	public int getRoundTime() {
		return _roundTime;
	}


	public void setRoundTime(int roundTime) {
		this._roundTime = roundTime;
	}
}

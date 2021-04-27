package imu.iMiniGames.Handlers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.Cooldowns;
import imu.iMiniGames.Other.MiniGame;
import imu.iMiniGames.Other.MiniGameSpleef;
import imu.iMiniGames.Other.PlayerDataCard;
import imu.iMiniGames.Other.SpleefGameCard;

public class SpleefGameHandler extends GameHandeler implements Listener
{
	SpleefManager _spleefManager;

	int _anti_block_time = 7;

	public SpleefGameHandler(Main main)
	{
		super(main, "Spleef");
		_main = main;
		_itemM = main.get_itemM();
		_spleefManager = main.get_spleefManager();
		_cd = new Cooldowns();
		_econ = main.get_econ();
		_main.getServer().getPluginManager().registerEvents(this, _main);
	}
	
	public int get_anti_block_time() {
		return _anti_block_time;
	}


	public void set_anti_block_time(int _anti_block_time) {
		this._anti_block_time = _anti_block_time;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				try 
				{
					PlayerDataCard pData = new PlayerDataCard(_main, event.getPlayer(),_playerDataFolderName);
					if(pData.isFile())
					{
						System.out.println("imusMiniGames: Restoring player data");
						pData.loadDataFileAndSetData();
						pData.setDataToPLAYER(event.getPlayer());
						pData.removeDataFile();
						_player_datas.remove(event.getPlayer().getUniqueId());
					}
				} 
				catch (Exception e) 
				{
					System.out.println("ERRoR: Counldnt find player data");
				}
				
			}
		}.runTaskAsynchronously(_main);
		
	}
	

	@Override
	public void afterMatchEnd(GameCard gameCard, Player winner) 
	{
		
	}

	@Override
	public MiniGame afterMatchStart(GameCard gameCard) {
		SpleefGameCard spleefGameCard = (SpleefGameCard) gameCard;
		MiniGameSpleef miniGame = new MiniGameSpleef(_main, this, spleefGameCard,"SPLEEF");
		miniGame.set_roundTime(_roundTime);
		
		miniGame.set_anti_stand(_anti_block_time);
		for(UUID uuid : gameCard.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			miniGame.addPlayer(p);
		}
		
		//match starts after this
		return miniGame;
	}

	@Override
	public void afterDefaultRequest(Player p, GameCard card) 
	{
		// TODO Auto-generated method stub
		
	}
	
}

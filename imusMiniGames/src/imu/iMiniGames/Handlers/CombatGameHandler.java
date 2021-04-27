package imu.iMiniGames.Handlers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Leaderbords.CombatLeaderBoard;
import imu.iMiniGames.Leaderbords.CombatPlayerBoard;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.CombatDataCard;
import imu.iMiniGames.Other.CombatGameCard;
import imu.iMiniGames.Other.ConfigMaker;
import imu.iMiniGames.Other.MiniGame;
import imu.iMiniGames.Other.MiniGameCombat;
import imu.iMiniGames.Other.PlayerDataCard;

public class CombatGameHandler extends GameHandeler implements Listener
{
	
	CombatLeaderBoard _leaderBoard;
	
	Material[] blackList_mat_gear = 
		{
				Material.PAPER,
				Material.SHULKER_BOX,
				Material.TOTEM_OF_UNDYING,


		};

	public CombatGameHandler(Main main)
	{
		super(main,"Combat");		
		_leaderBoard = _main.get_combatManager().getLeaderBoard();
		_main.getServer().getPluginManager().registerEvents(this, _main);
	}

	@Override
	public void afterDefaultRequest(Player p, GameCard card)
	{
		CombatGameCard cCard = (CombatGameCard) card;
		p.sendMessage(ChatColor.GOLD + "Attributs: "+ cCard.getDataCard().getAttributStringWithColor());
		p.sendMessage(ChatColor.GOLD + "Combat Kit: "+ ((CombatDataCard) cCard.getDataCard()).get_kit().get_kitNameWithColor());
		if(((CombatDataCard) cCard.getDataCard()).isOwnGearKit())
		{
			p.sendMessage(ChatColor.GRAY + "Tip: Consumed consumables in arena will not recover after the battle! Mostly food and potions");
		}

	}

	@EventHandler
	public void onInvOpen(InventoryClickEvent event)
	{
		if(_hasAccepted.isEmpty())
			return;

		if(event.getWhoClicked() instanceof Player)
		{
			Player p = (Player) event.getWhoClicked();
			if(isAccepted(p))
			{
				p.sendMessage(ChatColor.GRAY + "You are waiting for arena, you can't do inventory actions!");
				p.closeInventory();
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	void onCMDwrite(PlayerCommandPreprocessEvent event)
	{
		if(_hasAccepted.isEmpty())
			return;

		if(isAccepted(event.getPlayer()))
		{
			event.getPlayer().sendMessage(ChatColor.RED + "You are waiting for arena, you can't do that!");
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onInvOpen(InventoryOpenEvent event)
	{
		if(_hasAccepted.isEmpty())
			return;

		if(event.getPlayer() instanceof Player)
		{
			Player p = (Player) event.getPlayer();
			if(isAccepted(p))
			{
				p.sendMessage(ChatColor.GRAY + "You are waiting for arena, you can't do inventory actions!");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		new BukkitRunnable() {

			@Override
			public void run() 
			{
				PlayerDataCard pData = new PlayerDataCard(_main, event.getPlayer(),_playerDataFolderName);
				if(pData.isFile())
				{
					//TODO here too
					System.out.println("imusMiniGames: Restoring player data");
					pData.loadDataFileAndSetData();
					pData.setDataToPLAYER(event.getPlayer());
					pData.removeDataFile();
					_player_datas.remove(event.getPlayer().getUniqueId());
				}

				if(_hasAccepted.containsKey(event.getPlayer().getUniqueId()))
				{
					_hasAccepted.remove(event.getPlayer().getUniqueId());
				}				
			}
		}.runTaskAsynchronously(_main);

	}

	
	public void loadSettingConfig(boolean refresh)
	{
		new BukkitRunnable() 
		{

			@Override
			public void run() 
			{
				ConfigMaker cm = new ConfigMaker(_main, "Combat_settings.yml");
				FileConfiguration config = cm.getConfig();
				if(refresh)
				{
					if(cm.isExists())
					{
						setCd_invite_time(config.getInt("Cd_for_invite_acceptTime(Integer)"));
						setRoundTime(config.getInt("RoundTime(Integer)"));
						setBet_fee_percent(config.getDouble("Bet_fee(Double)"));
						_enable_broadcast = config.getBoolean("BroadCast(Boolean)");

					}
					cm.clearConfig();
				}
				try 
				{
					setCd_invite_time(cm.addDefault("Cd_for_invite_acceptTime", getCd_invite_time(),"Cd_for_invite_acceptTime: how long invite stays before expires"));
					setRoundTime(cm.addDefault("RoundTime", getRoundTime(),"RoundTime: Round time"));
					setBet_fee_percent(cm.addDefault("Bet_fee", getBet_fee_percent(),"Bet_fee: How much fee is. Between 0.00 - 1.00 (0.05 = 5%)"));
					_enable_broadcast = cm.addDefault("BroadCast", _enable_broadcast,"BroadCast: If true everybody see in server who startet game and result");

					cm.addComments();

				} catch (Exception e) 
				{
					_main.getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Something got wrong imusMiniGame fileNamed: "+cm.getFileName());
					_main.getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Maybe you casted some value as Integer When it should be Double?");
				}		
			}
		}.runTaskAsynchronously(_main);
	}


	@Override
	public MiniGame afterMatchStart(GameCard gameCard) 
	{
		CombatGameCard combatGameCard = (CombatGameCard) gameCard;
		combatGameCard.setupKits(blackList_mat_gear);
		
		MiniGame miniGame = new MiniGameCombat(_main, this, combatGameCard, "COMBAT");
		miniGame.set_roundTime(_roundTime);

		
		for(UUID uuid : gameCard.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			miniGame.addPlayer(p);
			if(_leaderBoard.getPlayerBoard(uuid) == null)
			{
				_leaderBoard.setPlayerBoard(uuid, new CombatPlayerBoard(p.getName(), uuid));
			}
			((CombatPlayerBoard)_leaderBoard.getPlayerBoard(uuid)).checkWeekly();
			
		}
		
		//match starts after this
		return miniGame;
	}

	@Override
	public void afterMatchEnd(GameCard gameCard, Player winner)
	{
		
	}



	



}

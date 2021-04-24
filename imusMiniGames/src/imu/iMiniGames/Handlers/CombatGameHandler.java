package imu.iMiniGames.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Arenas.Arena;
import imu.iMiniGames.Leaderbords.CombatLeaderBoard;
import imu.iMiniGames.Leaderbords.CombatPlayerBoard;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.CombatGameCard;
import imu.iMiniGames.Other.ConfigMaker;
import imu.iMiniGames.Other.MiniGame;
import imu.iMiniGames.Other.MiniGameCombat;
import imu.iMiniGames.Other.PlayerDataCard;

public class CombatGameHandler extends GameHandeler implements Listener
{
	HashMap<UUID, CombatGameCard> _player_gameCards = new HashMap<>();

	ArrayList<CombatGameCard> _queue_arena = new ArrayList<>();

	HashMap<String, CombatGameCard> _games;
	HashMap<String, MiniGameCombat> _live_games = new HashMap<>();

	CombatLeaderBoard _leaderBoard;
	Material[] blackList_mat_gear = 
		{
				Material.PAPER,
				Material.SHULKER_BOX,
				Material.TOTEM_OF_UNDYING,


		};

	public CombatGameHandler(Main main)
	{
		super(main);
		_games = new HashMap<String,CombatGameCard>();
		_leaderBoard = _main.get_combatManager().getLeaderBoard();
		_main.getServer().getPluginManager().registerEvents(this, _main);
	}


	public void savePlayerGameCard(Player p , CombatGameCard card)
	{
		_player_gameCards.put(p.getUniqueId(), card);
	}

	public boolean repearStartGame(Player player, CombatGameCard card)
	{

		for(UUID uuid: card.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			if(isPlayerInArena(p))
			{
				card.get_maker().sendMessage(ChatColor.RED + "Somebody is in already in game! Invitations canceled");
				card.get_maker().sendMessage(ChatColor.DARK_AQUA + "Your plan has been saved!");
				return false;
			}
		}

		if(_queue_arena.isEmpty() &&!_games.containsKey(card.get_arena().get_name()))
		{
			_games.put(card.get_arena().get_name(), card);
			player.sendMessage(ChatColor.AQUA + "Game starting.. sending invites");
			sendInvitesToPlayers(card);
			return true;
		}

		card.get_maker().sendMessage(ChatColor.DARK_AQUA + "Arena was busy! Your plan has been saved!");
		card.sendMessageToALL(ChatColor.AQUA + "You are placed in spleef queue! You will get invitation when arena is free to use!");
		_queue_arena.add(card);


		return false;
	}

	public boolean isPlayerPlanInQueue(Player p)
	{
		for(CombatGameCard card : _queue_arena)
		{
			if(card.isPlayerInThisCard(p.getUniqueId()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isPlayerInArena(Player p)
	{
		for(Map.Entry<String, CombatGameCard> entry : _games.entrySet())
		{
			CombatGameCard card = entry.getValue();
			for(UUID uuid: card.get_players_accept().keySet())
			{
				Player pp = Bukkit.getPlayer(uuid);
				if(p == pp)
				{
					return true;
				}
			}
		}
		return false;
	}

	public CombatGameCard getPlayerSGameCard(UUID uuid)
	{
		return _player_gameCards.get(uuid);
	}



	void requestTooltip(Player p, CombatGameCard card)
	{		


		p.sendMessage(ChatColor.LIGHT_PURPLE + "======== Combat REQUEST ========");
		p.sendMessage(ChatColor.GOLD + "Arena name: "+ card.get_arena().get_arenaNameWithColor());
		p.sendMessage(ChatColor.GOLD + "Players: "+ ChatColor.AQUA+ card.getPlayersString());
		p.sendMessage(ChatColor.GOLD + "Combat Kit: "+ card.get_combatDataCard().get_kit().get_kitNameWithColor());
		p.sendMessage(ChatColor.GOLD + "Best of  "+ChatColor.DARK_PURPLE +ChatColor.BOLD+ card.get_combatDataCard().get_bestOfAmount());

		if(!card.get_combatDataCard().get_invPotionEffects().isEmpty())
		{
			p.sendMessage(ChatColor.DARK_PURPLE + "PotionEffects: "+ card.get_combatDataCard().get_potions_names_str());			
		}
		p.sendMessage(ChatColor.GOLD + "Attributs: "+ card.get_combatDataCard().getAttributStringWithColor());

		p.sendMessage(ChatColor.GOLD + "You pay: "+ ChatColor.RED+ card.get_bet());		
		p.sendMessage(ChatColor.GOLD + "Able to WIN: "+ ChatColor.GREEN+ card.get_total_bet());		
		//p.sendMessage(ChatColor.GOLD + "Maker: "+ ChatColor.AQUA+ card.get_maker().getName());		
		//		p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
		//		if(card.get_bet() > 0)
		//		{
		//			p.sendMessage(ChatColor.GOLD + "Disclaimer: "+ChatColor.AQUA + "If game ends draw, the money will be send to server!");			
		//		}

		if(card.get_combatDataCard().isOwnGearKit())
		{
			p.sendMessage(ChatColor.GRAY + "Tip: Consumed consumables in arena will not recover after the battle! Mostly food and potions");
		}

		p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
		p.sendMessage(ChatColor.AQUA+"Would you like to join Combat?");		
		_main.get_itemM().sendYesNoConfirm(p, "/mg combat accept confirm:yes", "/mg combat accept confirm:no");
		//p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
	}

	void sendInvitesToPlayers(CombatGameCard card)
	{

		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<UUID> failed_players = new ArrayList<UUID>();

		for(UUID uuid: card.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			if(p != null)
			{

				requestTooltip(p, card);				
				players.add(p);
			}else
			{
				failed_players.add(uuid);
			}
			_request_arenas.put(uuid, card.get_arena().get_name());
			_cd.setCooldownInSeconds(_cd_invite+uuid, _cd_invite_time);

		}
		inviteTracker(players, card);

		if(failed_players.isEmpty())
			return;

		new BukkitRunnable() 
		{			
			@Override
			public void run() 
			{
				for(UUID uuid : failed_players)
				{
					requestAnwser(uuid, false);
				}			
			}
		}.runTaskLater(_main, 20*2);

	}

	public boolean requestAnwser(UUID uuid, boolean yes)
	{
		if(_request_arenas.containsKey(uuid))
		{
			String arenaName = _request_arenas.get(uuid);
			CombatGameCard gameCard = _games.get(arenaName);
			if(yes)
			{
				_hasAccepted.put(uuid, true);
				if(gameCard.putPlayerAccept(Bukkit.getPlayer(uuid)))
				{
					if(gameCard.isEveryPlayerAvailable())
					{
						startTHEmatch(gameCard);
					}
					else
					{
						cancelArena(null, gameCard);
					}					
				}

			}
			else
			{					
				cancelArena(Bukkit.getPlayer(uuid), gameCard);
			}
			_request_arenas.remove(uuid);
			return true;
		}
		return false;
	}

	public void cancelArena(Player whoCanceled, CombatGameCard gameCard)
	{
		for(Map.Entry<UUID,Boolean> entry : gameCard.get_players_accept().entrySet())
		{
			_hasAccepted.remove(entry.getKey());

			Player p = Bukkit.getPlayer(entry.getKey());
			if(p == null)
				continue;

			if(whoCanceled != null)
			{
				p.sendMessage(ChatColor.AQUA +whoCanceled.getName() + ChatColor.RED + " has denied");
			}else
			{
				p.sendMessage(ChatColor.RED + "Match canceled");
			}

			_request_arenas.remove(p.getUniqueId());

		}
		if(_games.containsKey(gameCard.get_arena().get_name()))
		{
			gameCard.get_maker().sendMessage(ChatColor.DARK_RED + "Match has been canceled. You can redo same plan with same command!");
			_games.remove(gameCard.get_arena().get_name());
			CheckQueue();

		}


	}

	void inviteTracker(ArrayList<Player> players, CombatGameCard card)
	{
		int back_up = 60;

		new BukkitRunnable() 
		{
			int count = 0;
			@Override
			public void run() 
			{
				boolean c = false;
				for(Player p : players)
				{
					if(_request_arenas.containsKey(p.getUniqueId()) && _cd.isCooldownReady(_cd_invite+p.getUniqueId()))
					{
						cancelArena(p, card);
						c = true;

					}
				}

				if(++count > back_up || _request_arenas.isEmpty() || c)
				{
					this.cancel();
				}


			}
		}.runTaskTimer(_main, 0, 20);
	}

	public void gameEndForPlayer(CombatGameCard card, Player p)
	{
		if(p == null || !p.isOnline())
			return;


		if(_player_datas.containsKey(p.getUniqueId()))
		{
			PlayerDataCard pData = _player_datas.get(p.getUniqueId());
			pData.setDataToPLAYER(card,p);
			pData.removeDataFile();	

			//			if(card != null)
			//			{
			//				card.checkAndApplyCombatConsumambles(p);
			//			}
		}


		_request_arenas.remove(p.getUniqueId());
		_player_datas.remove(p.getUniqueId());
	}

	void CheckQueue()
	{
		if(!_queue_arena.isEmpty())
		{
			CombatGameCard combatGameCard = _queue_arena.get(0);
			_queue_arena.remove(0);
			repearStartGame(combatGameCard.get_maker(), combatGameCard);
		}
	}

	public void addSpectator(String arenaName, Player p)
	{
		MiniGame minigame = _live_games.get(arenaName);
		if(minigame == null)
			return;

		minigame.addSpectator(_main, p);
		minigame.teleportSpectatorToSpectate(p);
	}

	public void gameHasEnded(CombatGameCard card, Player winner)
	{
		new BukkitRunnable() 
		{

			@Override
			public void run() 
			{
				Arena arena = card.get_arena();
				try
				{

					for(PlayerDataCard pdc : _live_games.get(arena.get_name()).getSpectators())
					{
						Player p = _main.getServer().getPlayer(pdc.get_uuid());
						if(p != null)
						{
							p.teleport(pdc.get_location());
						}					
					}
				} 
				catch (Exception e) 
				{
					System.out.println("Get spect: "+_live_games.get(arena.get_name()).getSpectators());
					System.out.println("Arena: "+arena.get_name());
					System.out.println("If this error happens spectators will be stuck in arena: teleport somewhere?");
					System.out.println("SOMETHING WENT WRONG: ERROR HERE: combatGameHanddler:424");
					System.out.println(e);
				}

				_games.remove(arena.get_name());
				_live_games.remove(arena.get_name());
				_leaderBoard.saveToFile();
				if(winner != null)
				{
					String str_sides = ChatColor.DARK_PURPLE + "=================================";

					card.sendMessageToALL(str_sides);			
					card.sendMessageToALL(ChatColor.RED+""+ChatColor.BOLD+ "You have LOST the COMBAT!", winner);
					winner.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+ "You have WON the COMBAT!");
					card.sendMessageToALL(str_sides);

					if(card.get_total_bet() > 0 && _econ != null)
					{
						winner.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+ "Received: "+ card.get_total_bet());
						winner.sendMessage(ChatColor.DARK_PURPLE + "=================================");
						_econ.depositPlayer(winner, card.get_total_bet());

					}
				}		
				CheckQueue();
			}
		}.runTask(_main);


	}

	public void removePotionEffects(Player p)
	{
		for(PotionEffect effect : p.getActivePotionEffects())
		{
			p.removePotionEffect(effect.getType());
		}
	}

	void startTHEmatch(CombatGameCard gameCard)
	{
		boolean no_money = false;
		if(_econ != null && gameCard.get_bet() > 0)
		{			
			for(UUID uuid : gameCard.get_players_accept().keySet())
			{
				Player p = Bukkit.getPlayer(uuid);

				if(p != null)
				{
					double balance = _econ.getBalance(p);
					if(balance < gameCard.get_bet())
					{
						no_money = true;
					}
				}
				else
				{
					no_money = true;
				}

			}

			if(no_money)
			{
				cancelArena(null, gameCard);
				gameCard.sendMessageToALL(ChatColor.RED + "Someones balance werent enough!");
				return;
			}
		}

		gameCard.setupKits(blackList_mat_gear);
		MiniGameCombat combat = new MiniGameCombat(_main, this, gameCard,"COMBAT");
		combat.set_roundTime(_roundTime);


		for(UUID uuid : gameCard.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			_hasAccepted.remove(uuid);

			if(_leaderBoard.getPlayerBoard(uuid) == null)
			{
				_leaderBoard.setPlayerBoard(uuid, new CombatPlayerBoard(p.getName(), uuid));
			}
			((CombatPlayerBoard)_leaderBoard.getPlayerBoard(uuid)).checkWeekly();

			PlayerDataCard pData=new PlayerDataCard(_main, p,_playerDataFolderName);
			pData.saveDataToFile(false);			
			_player_datas.put(p.getUniqueId(), pData);

			PlayerDataCard pDataBackup=new PlayerDataCard(_main, p,_playerDataFolderName+"Backups/"+p.getName()+"_"+p.getUniqueId());
			pDataBackup.saveDataToFile(true);

			combat.addPlayer(p);

			String title_str = ChatColor.BLUE + "COMBAT";
			String bet_str = ChatColor.GOLD +"Winner takes: "+ChatColor.DARK_GREEN +gameCard.get_total_bet();
			if(_econ != null && gameCard.get_bet() > 0)
			{
				_econ.withdrawPlayer(p, gameCard.get_bet());
				p.sendMessage(ChatColor.DARK_PURPLE+ "You have placed your bet!");

				p.sendTitle(title_str, bet_str, 40, 20, 5);
			}
			else
			{
				p.sendTitle(title_str, null, 40, 20, 5);
			}

		}

		combat.Start();
		_live_games.put(gameCard.get_arena().get_name(),combat);
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

	public void onnDisable()
	{
		for(Map.Entry<String, MiniGameCombat> entry : _live_games.entrySet())
		{
			entry.getValue().endGame();
		}
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


}

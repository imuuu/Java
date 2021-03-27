package imu.iMiniGames.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.CombatManager;
import imu.iMiniGames.Other.CombatGameCard;
import imu.iMiniGames.Other.ConfigMaker;
import imu.iMiniGames.Other.Cooldowns;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.Other.MiniGameCombat;
import imu.iMiniGames.Other.PlayerDataCard;
import net.milkbowl.vault.economy.Economy;

public class CombatGameHandler implements Listener
{
	Main _main;
	ItemMetods _itemM;
	CombatManager _combatManager;
	Economy _econ;
	
	HashMap<String, CombatGameCard> _games = new HashMap<>();
	HashMap<String, MiniGameCombat> _live_games = new HashMap<>();
	
	
	HashMap<UUID, CombatGameCard> _player_gameCards = new HashMap<>();
	
	HashMap<UUID,String> _request_arenas = new HashMap<>();
	
	HashMap<UUID,PlayerDataCard> _player_datas = new HashMap<>();
	
	ArrayList<CombatGameCard> _queue_arena = new ArrayList<>();
	
	Cooldowns _cd;
	
	String _cd_invite = "invite_";
	int _cd_invite_time = 10; //seconds
	int _combat_roundTime = 600;
	
	double _bet_fee_percent = 0.05;
	
	String _playerDataFolderName="Combat";
	
	boolean _enable_broadcast = true;
	
	public boolean is_enable_broadcast() {
		return _enable_broadcast;
	}


	public CombatGameHandler(Main main)
	{
		_main = main;
		_itemM = main.get_itemM();
		_combatManager = main.get_combatManager();
		_cd = new Cooldowns();
		_econ = main.get_econ();
		_main.getServer().getPluginManager().registerEvents(this, _main);
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
		return _combat_roundTime;
	}


	public void setRoundTime(int roundTime) {
		this._combat_roundTime = roundTime;
	}

	public void savePlayerGameCard(Player p , CombatGameCard card)
	{
		_player_gameCards.put(p.getUniqueId(), card);
	}
	
	public boolean repearStartGame(Player player, CombatGameCard card)
	{
		
		for(Map.Entry<Player,Boolean> entry : card.get_players_accept().entrySet())
		{
			if(isPlayerInArena(entry.getKey()))
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
			if(card.isPlayerInThisCard(p))
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
			for(Map.Entry<Player,Boolean> card_entry : card.get_players_accept().entrySet())
			{
				if(card_entry.getKey() == p)
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
		
		p.sendMessage(ChatColor.GOLD + "You pay: "+ ChatColor.RED+ card.get_bet());		
		p.sendMessage(ChatColor.GOLD + "Able to WIN: "+ ChatColor.GREEN+ card.get_total_bet());		
		//p.sendMessage(ChatColor.GOLD + "Maker: "+ ChatColor.AQUA+ card.get_maker().getName());		
//		p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
//		if(card.get_bet() > 0)
//		{
//			p.sendMessage(ChatColor.GOLD + "Disclaimer: "+ChatColor.AQUA + "If game ends draw, the money will be send to server!");			
//		}

		p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
		p.sendMessage(ChatColor.AQUA+"Would you like to join Combat?");		
		_main.get_itemM().sendYesNoConfirm(p, "/mg combat accept confirm:yes", "/mg combat accept confirm:no");
		//p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
	}
	
	void sendInvitesToPlayers(CombatGameCard card)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		for(Map.Entry<Player,Boolean> entry : card.get_players_accept().entrySet())
		{
			Player p = entry.getKey();
			
			_request_arenas.put(p.getUniqueId(), card.get_arena().get_name());
			requestTooltip(p, card);
			_cd.setCooldownInSeconds(_cd_invite+p.getName(), _cd_invite_time);
			players.add(p);
		}
		inviteTracker(players, card);
	}
	
	public boolean requestAnwser(UUID uuid, boolean yesORno)
	{
		if(_request_arenas.containsKey(uuid))
		{
			String arenaName = _request_arenas.get(uuid);
			CombatGameCard gameCard = _games.get(arenaName);
			if(yesORno)
			{
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
		for(Map.Entry<Player,Boolean> entry : gameCard.get_players_accept().entrySet())
		{
			Player p = entry.getKey();
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
					if(_request_arenas.containsKey(p.getUniqueId()) && _cd.isCooldownReady(_cd_invite+p.getName()))
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
	
	public void gameEndForPlayer(Player p)
	{
		if(p == null || !p.isOnline())
			return;
		

		if(_player_datas.containsKey(p.getUniqueId()))
		{
			PlayerDataCard pData = _player_datas.get(p.getUniqueId());
			pData.setDataToPLAYER(p);
			pData.removeDataFile();			
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
	
	public void gameHasEnded(CombatGameCard card, Player winner)
	{

		_games.remove(card.get_arena().get_name());
		_live_games.remove(card.get_arena().get_name());
		
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
			for(Map.Entry<Player,Boolean> entry : gameCard.get_players_accept().entrySet())
			{
				Player p = entry.getKey();
				double balance = _econ.getBalance(p);
				if(balance > gameCard.get_bet())
				{
					no_money = true;
				}
				
			}
			if(!no_money)
			{
				cancelArena(null, gameCard);
				gameCard.sendMessageToALL(ChatColor.RED + "Someones balance werent enough!");
				return;
			}
		}
		
		
		MiniGameCombat combat = new MiniGameCombat(_main, this, gameCard,"COMBAT");
		combat.set_roundTime(_combat_roundTime);
		

		for(Map.Entry<Player,Boolean> entry : gameCard.get_players_accept().entrySet())
		{
			Player p = entry.getKey();
			
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
	public void onJoin(PlayerJoinEvent event)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
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

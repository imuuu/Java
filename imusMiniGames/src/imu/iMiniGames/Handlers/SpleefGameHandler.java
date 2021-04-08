package imu.iMiniGames.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Arenas.Arena;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.Cooldowns;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.Other.MiniGame;
import imu.iMiniGames.Other.MiniGameSpleef;
import imu.iMiniGames.Other.PlayerDataCard;
import imu.iMiniGames.Other.SpleefGameCard;
import net.milkbowl.vault.economy.Economy;

public class SpleefGameHandler implements Listener
{
	Main _main;
	ItemMetods _itemM;
	SpleefManager _spleefManager;
	Economy _econ;
	
	HashMap<String, SpleefGameCard> _games = new HashMap<>();
	HashMap<String, MiniGameSpleef> _live_games = new HashMap<>();
	
	
	HashMap<UUID, SpleefGameCard> _player_gameCards = new HashMap<>();
	
	HashMap<UUID,String> _request_arenas = new HashMap<>();
	
	HashMap<UUID,PlayerDataCard> _player_datas = new HashMap<>();
	
	ArrayList<SpleefGameCard> _queue_arena = new ArrayList<>();
	
	Cooldowns _cd;
	
	String _cd_invite = "invite_";
	int _cd_invite_time = 10; //seconds
	int _spleef_roundTime = 90;
	
	double _bet_fee_percent = 0.05;
	int _anti_block_time = 7;
	
	

	String _playerDataFolderName="Spleef";
	
	public SpleefGameHandler(Main main)
	{
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


	public int getSpleef_roundTime() {
		return _spleef_roundTime;
	}


	public void setSpleef_roundTime(int spleef_roundTime) {
		this._spleef_roundTime = spleef_roundTime;
	}

	public void savePlayerGameCard(Player p , SpleefGameCard card)
	{
		_player_gameCards.put(p.getUniqueId(), card);
	}
	
	public boolean repearStartGame(Player player, SpleefGameCard card)
	{
		
		for(UUID uuid : card.get_players_accept().keySet())
		{
			if(isPlayerInArena(uuid))
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
		for(GameCard card : _queue_arena)
		{
			if(card.isPlayerInThisCard(p.getUniqueId()))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isPlayerInArena(UUID uuid)
	{
		for(Map.Entry<String, SpleefGameCard> entry : _games.entrySet())
		{
			SpleefGameCard card = entry.getValue();
			for(UUID uuid2 : card.get_players_accept().keySet())
			{
				if(uuid2 == uuid)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public SpleefGameCard getPlayerSGameCard(UUID uuid)
	{
		return _player_gameCards.get(uuid);
	}
	
	void requestTooltip(Player p, SpleefGameCard card)
	{		
		

		p.sendMessage(ChatColor.LIGHT_PURPLE + "======== SPLEEF REQUEST ========");
		p.sendMessage(ChatColor.GOLD + "Arena name: "+ ChatColor.AQUA+ card.get_arena().get_arenaNameWithColor());
		p.sendMessage(ChatColor.GOLD + "Players: "+ ChatColor.AQUA+ card.getPlayersString());
		p.sendMessage(ChatColor.GOLD + "Best of  "+ChatColor.DARK_PURPLE +ChatColor.BOLD+ card.get_spleefDataCard().get_bestOfAmount());
		
		if(!card.get_spleefDataCard().get_invPotionEffects().isEmpty())
		{
			p.sendMessage(ChatColor.DARK_PURPLE + "PotionEffects: "+ card.get_spleefDataCard().get_potions_names_str());			
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
		p.sendMessage(ChatColor.AQUA+"Would you like to join spleef?");		
		_main.get_itemM().sendYesNoConfirm(p, "/mg spleef accept confirm:yes", "/mg spleef accept confirm:no");
		//p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
	}
	
	void sendInvitesToPlayers(SpleefGameCard card)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		for(UUID uuid: card.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			
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
			SpleefGameCard gameCard = _games.get(arenaName);
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
	
	public void cancelArena(Player whoCanceled, SpleefGameCard gameCard)
	{
		for(UUID uuid : gameCard.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
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
	
	void inviteTracker(ArrayList<Player> players, SpleefGameCard card)
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
			SpleefGameCard spleefGameCard = _queue_arena.get(0);
			_queue_arena.remove(0);
			repearStartGame(spleefGameCard.get_maker(), spleefGameCard);
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
	
	public void gameHasEnded(SpleefGameCard card, Player winner)
	{

		Arena arena = card.get_arena();

		for(PlayerDataCard pdc : _live_games.get(arena.get_name()).getSpectators())
		{
			Player p = _main.getServer().getPlayer(pdc.get_uuid());
			if(p != null)
			{
				p.teleport(pdc.get_location());
			}
			
		}

		_games.remove(arena.get_name());
		_live_games.remove(arena.get_name());

		if(winner != null)
		{
			String str_sides = ChatColor.DARK_PURPLE + "=================================";
			
			card.sendMessageToALL(str_sides);			
			card.sendMessageToALL(ChatColor.RED+""+ChatColor.BOLD+ "You have LOST the SPLEEF!", winner);
			winner.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+ "You have WON the SPLEEF!");
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
	
	void startTHEmatch(SpleefGameCard gameCard)
	{
		boolean no_money = false;
		if(_econ != null && gameCard.get_bet() > 0)
		{			
			for(Map.Entry<UUID,Boolean> entry : gameCard.get_players_accept().entrySet())
			{
				Player p = Bukkit.getPlayer(entry.getKey());
				
				if(p != null)
				{
					double balance = _econ.getBalance(p);
					if(balance > gameCard.get_bet())
					{
						no_money = true;
					}
				}else
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
		
		
		MiniGameSpleef spleef = new MiniGameSpleef(_main, this, gameCard,"SPLEEF");
		spleef.set_roundTime(_spleef_roundTime);
		
		spleef.set_anti_stand(_anti_block_time);

		for(UUID uuid : gameCard.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);

			
			PlayerDataCard pData=new PlayerDataCard(_main, p,_playerDataFolderName);
			pData.saveDataToFile(false);			
			_player_datas.put(p.getUniqueId(), pData);
			
			PlayerDataCard pDataBackup=new PlayerDataCard(_main, p,_playerDataFolderName+"Backups/"+p.getName()+"_"+p.getUniqueId());
			pDataBackup.saveDataToFile(true);
			
			spleef.addPlayer(p);
			
			String title_str = ChatColor.BLUE + "SPLEEF";
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
		
		spleef.Start();
		_live_games.put(gameCard.get_arena().get_name(),spleef);
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
	
	public void onnDisable()
	{
		for(Map.Entry<String, MiniGameSpleef> entry : _live_games.entrySet())
		{
			entry.getValue().endGame();
		}
	}
	
}

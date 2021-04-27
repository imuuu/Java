package imu.iMiniGames.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Arenas.Arena;
import imu.iMiniGames.Interfaces.IGameHandeler;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.CombatManager;
import imu.iMiniGames.Other.Cooldowns;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.Other.MiniGame;
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

	HashMap<UUID, GameCard> _player_gameCards = new HashMap<>();
	
	ArrayList<GameCard> _queue_arena = new ArrayList<>(); //combatGameCarad

	HashMap<String, GameCard> _games; //combatGameCarad
	HashMap<String, MiniGame> _live_games = new HashMap<>();
	
	boolean _enable_broadcast = true;
	double _bet_fee_percent = 0.05;
	
	String _cd_invite = "invite_";
	int _cd_invite_time = 10; //seconds
	int _roundTime = 600;
	String _playerDataFolderName="Combat";
	public GameHandeler(Main main,String dataFolderName) 
	{
		_main = main;
		_itemM = main.get_itemM();
		_combatManager = main.get_combatManager();
		_cd = new Cooldowns();
		_econ = main.get_econ();
		_playerDataFolderName = dataFolderName;
		_games = new HashMap<String,GameCard>();

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
	
	public void savePlayerGameCard(Player p , GameCard card)
	{
		_player_gameCards.put(p.getUniqueId(), card);
	}
	
	public GameCard getPlayerSGameCard(UUID uuid)
	{
		return _player_gameCards.get(uuid);
	}
	
	void requestTooltip(Player p, GameCard card)
	{		

		p.sendMessage(ChatColor.LIGHT_PURPLE + "======== "+card.get_tagName()+" REQUEST ========");
		p.sendMessage(ChatColor.GOLD + "Arena name: "+ card.get_arena().get_arenaNameWithColor());
		p.sendMessage(ChatColor.GOLD + "Players: "+ ChatColor.AQUA+ card.getPlayersString());		
		p.sendMessage(ChatColor.GOLD + "Best of  "+ChatColor.DARK_PURPLE +ChatColor.BOLD+ card.getDataCard().get_bestOfAmount());

		if(!card.getDataCard().get_invPotionEffects().isEmpty())
		{
			p.sendMessage(ChatColor.DARK_PURPLE + "PotionEffects: "+ card.getDataCard().get_potions_names_str());			
		}
		p.sendMessage(ChatColor.GOLD + "You pay: "+ ChatColor.RED+ card.get_bet());		
		p.sendMessage(ChatColor.GOLD + "Able to WIN: "+ ChatColor.GREEN+ card.get_total_bet());		
		
		afterDefaultRequest(p,card);
		
		
		p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
		p.sendMessage(ChatColor.AQUA+"Would you like to join Combat?");		
		_main.get_itemM().sendYesNoConfirm(p, "/"+card.get_cmdString()+" accept confirm:yes", "/"+card.get_cmdString()+" accept confirm:no"); //TODO COMMAND
		//p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
		///mg combat accept
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
	
	public boolean isPlayerInArena(Player p)
	{
		for(Map.Entry<String, GameCard> entry : _games.entrySet())
		{
			GameCard card = entry.getValue();
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
	
	void sendInvitesToPlayers(GameCard card)
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
			GameCard gameCard = _games.get(arenaName);
			if(yes)
			{
				_hasAccepted.put(uuid, true);
				if(gameCard.putPlayerAccept(Bukkit.getPlayer(uuid)))
				{
					if(gameCard.isEveryPlayerAvailable())
					{
						//startTHEmatch(gameCard);
						matchSTART(gameCard);
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

	public void cancelArena(Player whoCanceled, GameCard gameCard)
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

	void inviteTracker(ArrayList<Player> players, GameCard card)
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
	
	void CheckQueue()
	{
		if(!_queue_arena.isEmpty())
		{
			GameCard gameCard = _queue_arena.get(0);
			_queue_arena.remove(0);
			repearStartGame(gameCard.get_maker(), gameCard);
		}
	}
	
	public boolean repearStartGame(Player player, GameCard card)
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
	
	public void onnDisable()
	{
		for(Map.Entry<String, MiniGame> entry : _live_games.entrySet())
		{
			entry.getValue().endGame();
		}
	}
	
	public void gameEndForPlayer(GameCard card, Player p)
	{
		if(p == null || !p.isOnline())
			return;


		if(_player_datas.containsKey(p.getUniqueId()))
		{
			PlayerDataCard pData = _player_datas.get(p.getUniqueId());
			pData.setDataToPLAYER(card,p);
			pData.removeDataFile();	
		}

		_request_arenas.remove(p.getUniqueId());
		_player_datas.remove(p.getUniqueId());
	}

	public void addSpectator(String arenaName, Player p)
	{
		MiniGame minigame = _live_games.get(arenaName);
		if(minigame == null)
			return;

		minigame.addSpectator(_main, p);
		minigame.teleportSpectatorToSpectate(p);
	}
	
	public void removePotionEffects(Player p)
	{
		for(PotionEffect effect : p.getActivePotionEffects())
		{
			p.removePotionEffect(effect.getType());
		}
	}
	
	@Override
	public void matchEND(GameCard gameCard, Player winner) 
	{
		new BukkitRunnable() 
		{

			@Override
			public void run() 
			{
				Arena arena = gameCard.get_arena();
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
				
				//_leaderBoard.saveToFile();
				
				if(winner != null)
				{
					String str_sides = ChatColor.DARK_PURPLE + "=================================";

					gameCard.sendMessageToALL(str_sides);			
					gameCard.sendMessageToALL(ChatColor.RED+""+ChatColor.BOLD+ "You have LOST the "+gameCard._tagName+"+!", winner); 
					winner.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+ "You have WON the "+gameCard._tagName+"!");
					gameCard.sendMessageToALL(str_sides);

					if(gameCard.get_total_bet() > 0 && _econ != null)
					{
						winner.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+ "Received: "+ gameCard.get_total_bet());
						winner.sendMessage(ChatColor.DARK_PURPLE + "=================================");
						_econ.depositPlayer(winner, gameCard.get_total_bet());

					}
				}		
				CheckQueue();
				afterMatchEnd(gameCard, winner);
			}
		}.runTask(_main);	
	}
	
	@Override
	public void matchSTART(GameCard gameCard) 
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

		for(UUID uuid : gameCard.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			_hasAccepted.remove(uuid);

			PlayerDataCard pData=new PlayerDataCard(_main, p,_playerDataFolderName);
			pData.saveDataToFile(false);			
			_player_datas.put(p.getUniqueId(), pData);

			PlayerDataCard pDataBackup= new PlayerDataCard(_main, p,_playerDataFolderName+"Backups/"+p.getName()+"_"+p.getUniqueId());
			pDataBackup.saveDataToFile(true);

			

			String title_str = ChatColor.BLUE + "COMBAT"; //TODO
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
		MiniGame miniGame = afterMatchStart(gameCard);
		miniGame.startGame();
		_live_games.put(gameCard.get_arena().get_name(),miniGame);
	}
}

package imu.iMiniGames.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.Cooldowns;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.Other.MiniGameSpleef;
import imu.iMiniGames.Other.SpleefGameCard;
import net.milkbowl.vault.economy.Economy;

public class SpleefGameHandler 
{
	Main _main;
	ItemMetods _itemM;
	SpleefManager _spleefManager;
	Economy _econ;
	
	HashMap<String, SpleefGameCard> _games = new HashMap<>();
	HashMap<String, MiniGameSpleef> _live_games = new HashMap<>();
	
	
	HashMap<UUID, SpleefGameCard> _player_gameCards = new HashMap<>();
	
	HashMap<UUID,String> _request_arenas = new HashMap<>();
	
	HashMap<UUID,ItemStack[]> _player_invs = new HashMap<>();
	
	HashMap<UUID,Location> _player_last_loc = new HashMap<>();
	HashMap<UUID,Integer> _player_last_xp = new HashMap<>();
	HashMap<UUID,GameMode> _player_last_gamemode = new HashMap<>();
	
	Cooldowns _cd;
	
	String cd_invite = "invite_";
	int cd_invite_time = 10; //seconds
	int spleef_roundTime = 60;
	
	public SpleefGameHandler(Main main)
	{
		_main = main;
		_itemM = main.get_itemM();
		_spleefManager = main.get_spleefManager();
		_cd = new Cooldowns();
		_econ = main.get_econ();
	}
	
	public void putPlayerInvContent(Player p)
	{
		_player_invs.put(p.getUniqueId(), p.getInventory().getContents());
	}
	
	public ItemStack[] getPlayerInv(Player p)
	{
		if(_player_invs.containsKey(p.getUniqueId()))
		{
			return _player_invs.get(p.getUniqueId());
		}else
		{
			System.out.println("Didnt find player inv: "+p.getName());
		}
		return null;
	}
	
	public void putPlayerLastLoc(Player p)
	{
		_player_last_loc.put(p.getUniqueId(), p.getLocation());
	}
	public void putPlayerLastGM(Player p )
	{
		_player_last_gamemode.put(p.getUniqueId(), p.getGameMode());
	}
	public GameMode getPlayerLastGM(Player p)
	{
		return _player_last_gamemode.get(p.getUniqueId());
	}
	public void putPlayerLastXP(Player p)
	{
		_player_last_xp.put(p.getUniqueId(), p.getTotalExperience());
	}
	public Integer getPlayerLastXP(Player p)
	{
		return _player_last_xp.get(p.getUniqueId());
	}
	public Location getPlayerLastLoc(Player p)
	{
		return _player_last_loc.get(p.getUniqueId());
	}
	
	
	public boolean repearStartGame(Player player, SpleefGameCard card)
	{
		_player_gameCards.put(player.getUniqueId(), card);
		if(!_games.containsKey(card.get_arena().get_name()))
		{
			_games.put(card.get_arena().get_name(), card);
			player.sendMessage(ChatColor.AQUA + "Game starting.. sending invites");
			sendInvitesToPlayers(card);
			return true;
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
		p.sendMessage(ChatColor.GOLD + "Arena name: "+ ChatColor.AQUA+ card.get_arena().get_displayName());
		p.sendMessage(ChatColor.GOLD + "Players: "+ ChatColor.AQUA+ card.getPlayersString());
		p.sendMessage(ChatColor.GOLD + "You pay: "+ ChatColor.AQUA+ card.get_bet());		
		p.sendMessage(ChatColor.GOLD + "Able to WIN: "+ ChatColor.AQUA+ card.get_total_bet());		
		p.sendMessage(ChatColor.GOLD + "Maker: "+ ChatColor.AQUA+ card.get_maker().getName());		
		p.sendMessage(ChatColor.LIGHT_PURPLE + "================================");
		p.sendMessage(ChatColor.AQUA+"Would you like to join spleef?");		
		_main.get_itemM().sendYesNoConfirm(p, "/mg spleef accept confirm:yes", "/mg spleef accept confirm:no");
	}
	
	void sendInvitesToPlayers(SpleefGameCard card)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		for(Map.Entry<Player,Boolean> entry : card.get_players_accept().entrySet())
		{
			Player p = entry.getKey();
			_request_arenas.put(p.getUniqueId(), card.get_arena().get_name());
			requestTooltip(p, card);
			_cd.setCooldownInSeconds(cd_invite+p.getName(), cd_invite_time);
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
					System.out.println("EVERYBODY HAS ACCEPTED.. START THE MATCH");
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
					if(_request_arenas.containsKey(p.getUniqueId()) && _cd.isCooldownReady(cd_invite+p.getName()))
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
		
		ItemStack[] content =getPlayerInv(p);
		
		if(content != null)
		{
			p.getInventory().setContents(content);			
			p.teleport(getPlayerLastLoc(p));
			p.setTotalExperience(getPlayerLastXP(p));
			p.setGameMode(getPlayerLastGM(p));
		}
			
		
		_request_arenas.remove(p.getUniqueId());
		_player_last_loc.remove(p.getUniqueId());
		_player_invs.remove(p.getUniqueId());
		_player_last_xp.remove(p.getUniqueId());
		_player_last_gamemode.remove(p.getUniqueId());
	}
	
	public void gameHasEnded(SpleefGameCard card, Player winner)
	{

		_games.remove(card.get_arena().get_name());
		_live_games.remove(card.get_arena().get_name());
		
		if(winner != null)
		{
			winner.sendMessage(ChatColor.DARK_PURPLE + "=================================");
			winner.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+ "You have won the SPLEEF!");
			winner.sendMessage(ChatColor.DARK_PURPLE + "=================================");
			if(card.get_total_bet() > 0 && _econ != null)
			{
				winner.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+ "Received: "+ card.get_total_bet());
				winner.sendMessage(ChatColor.DARK_PURPLE + "=================================");
				_econ.depositPlayer(winner, card.get_total_bet());
				
			}
		}
		
		
	}
	
	
	void startTHEmatch(SpleefGameCard gameCard)
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
		
		
		MiniGameSpleef spleef = new MiniGameSpleef(_main, this, gameCard,"SPLEEF");
		spleef.set_roundTime(spleef_roundTime);

		for(Map.Entry<Player,Boolean> entry : gameCard.get_players_accept().entrySet())
		{
			Player p = entry.getKey();
			
			putPlayerInvContent(p);
			putPlayerLastLoc(p);
			putPlayerLastXP(p);
			putPlayerLastGM(p);
			
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
	
	public void onnDisable()
	{
		for(Map.Entry<String, MiniGameSpleef> entry : _live_games.entrySet())
		{
			entry.getValue().endGame();
		}
	}
	
}

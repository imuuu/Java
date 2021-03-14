package imu.iMiniGames.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.Cooldowns;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.Other.SpleefGameCard;

public class SpleefGameHandler 
{
	Main _main;
	ItemMetods _itemM;
	SpleefManager _spleefManager;
	
	HashMap<String, SpleefGameCard> _games = new HashMap<>();
	HashMap<UUID, SpleefGameCard> _player_gameCards = new HashMap<>();
	
	HashMap<UUID,String> _request_arenas = new HashMap<>();
	HashMap<UUID,ItemStack[]> _player_invs = new HashMap<>();
	
	Cooldowns _cd;
	
	String cd_invite = "invite_";
	int cd_invite_time = 10; //seconds
	public SpleefGameHandler(Main main)
	{
		_main = main;
		_itemM = main.get_itemM();
		_spleefManager = main.get_spleefManager();
		_cd = new Cooldowns();
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
				p.sendMessage(ChatColor.RED + "All players aren't available! Match canceled");
			}
			
			_request_arenas.remove(p.getUniqueId());
			
		}
		if(_games.containsKey(gameCard.get_arena().get_name()))
		{
			gameCard.get_maker().sendMessage(ChatColor.DARK_RED + "Match has been canceled. You can redo same plan with command spleefGamePlaner:83");
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
	
	void setupPlayerForStart(Player p)
	{
		p.setGameMode(GameMode.SURVIVAL);
		PlayerInventory inv = p.getInventory();
		_player_invs.put(p.getUniqueId(), inv.getContents());
		
		inv.clear();
		
		ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
		_itemM.setDisplayName(shovel, ChatColor.GOLD + "SPLEEF SHOVEL");
		shovel.addEnchantment(Enchantment.VANISHING_CURSE, 1);
		shovel.addEnchantment(Enchantment.DIG_SPEED, 5);
		shovel.addEnchantment(Enchantment.DURABILITY, 3);
		inv.addItem(shovel);
		
	}
	
	void startTHEmatch(SpleefGameCard gameCard)
	{
		int idx = 0;
		for(Map.Entry<Player,Boolean> entry : gameCard.get_players_accept().entrySet())
		{
			Player p = entry.getKey();
			setupPlayerForStart(p);
			p.teleport(gameCard.get_arena().getSpawnpointLoc(idx++));
			
		}
	}
	
	
	
}

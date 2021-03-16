package imu.iMiniGames.Other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.datafixers.util.Pair;

import imu.iMiniGames.Handlers.SpleefGameHandler;
import imu.iMiniGames.Main.Main;

public class MiniGameSpleef extends MiniGame implements Listener
{
	SpleefGameHandler _spleefHandler;
	SpleefGameCard _gameCard;
	Cooldowns _cd;
	
	double _layer_y = 0;
	
	boolean has_started = false;
	boolean has_ended = false;
	
	int _start_delay = 3;
	int _total_players = 0;
	int _anti_stand = 0;  // if 0, its disabled
	
	HashMap<Player, Pair<Location, Integer>> _player_anti_stands = new HashMap<>();
	ArrayList<Block> _remove_blocks = new ArrayList<>();
	
	public MiniGameSpleef(Main main, SpleefGameHandler spleefHandler,SpleefGameCard gameCard,String minigameName) 
	{
		super(main,minigameName);
		
		_spleefHandler = spleefHandler;
		_gameCard = gameCard;
		_cd = new Cooldowns();
		_layer_y = gameCard.get_arena().getPlatformCorner(0).getY();
		_main.getServer().getPluginManager().registerEvents(this, main);
		_total_players = _gameCard._players_accept.size();
		System.out.println("match starts with players: "+ _total_players);
		
		
	}
	
	void setupPlayerForStart(Player p)
	{
		p.setGameMode(GameMode.SURVIVAL);
		PlayerInventory inv = p.getInventory();
		
		p.setHealth(20);
		
		inv.clear();
		
		ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
		_itemM.setDisplayName(shovel, ChatColor.GOLD + "SPLEEF SHOVEL");
		shovel.addEnchantment(Enchantment.VANISHING_CURSE, 1);
		shovel.addEnchantment(Enchantment.DIG_SPEED, 5);
		shovel.addEnchantment(Enchantment.DURABILITY, 3);
		inv.addItem(shovel);
		inv.addItem(shovel);
		inv.addItem(shovel);
		
	}
	
	public void Start()
	{
		int count = 0;
		for(Map.Entry<Player,Integer> entry : _players_score.entrySet())
		{
			Player p = entry.getKey();
			setupPlayerForStart(p);
			p.teleport(_gameCard.get_arena().getSpawnpointLoc(count));
			_player_anti_stands.put(p, new Pair<Location, Integer>(new Location(p.getWorld(), 0, 0, 0),0));
			count++;
		}
		
		_cd.setCooldownInSeconds("round", _roundTime);
		_cd.setCooldownInSeconds("start", _start_delay);
		_gameCard.get_arena().fillWithSnowpPlatform();
		runnableAsyc();
	}
	
	public void endGame()
	{
		has_ended = true;
		String msg = ChatColor.DARK_PURPLE +""+ChatColor.BOLD+ "Spleef Game Has Ended";
		Player winner;
		if(_total_players > 1)
		{
			winner = null;
		}else
		{
			 winner = (Player)_players_score.keySet().toArray()[0];
		}
		
				
		for(Map.Entry<Player,Boolean> entry : _gameCard.get_players_accept().entrySet())
		{
			Player p = entry.getKey();
			
			if(!p.isOnline())
			{
				continue;
			}
			p.sendMessage(ChatColor.DARK_PURPLE + "=================================");
			p.sendMessage(msg);
			
			if(winner == null )
			{
				if(_gameCard.get_bet() > 0)
				{
					p.sendMessage(ChatColor.RED + ""+ChatColor.BOLD + "It was DRAW! Money has send to the server! Thanks for playing!");
				}else
				{
					p.sendMessage(ChatColor.RED + ""+ChatColor.BOLD + "It was DRAW!");
				}
				
			}

			_spleefHandler.gameEndForPlayer(p);
			
		}
		
		_players_lobby.clear();
		_player_anti_stands.clear();
		_players_score.clear();
		_remove_blocks.clear();
		_spleefHandler.gameHasEnded(_gameCard, winner);
		HandlerList.unregisterAll(this);
	}
	
	void moveToLobbyPlayer(Player p)
	{
		p.getInventory().clear();
		p.teleport(_gameCard.get_arena().get_spectator_lobby());
		_players_lobby.put(p, _players_score.get(p));
		_players_score.remove(p);
		p.setHealth(20);
		p.sendMessage(ChatColor.DARK_GRAY + "You have been moved to spetator lobby! Better luck next time!");
		
		_total_players--;
		if(_total_players <= 1)
		{
			endGame();
		}
		
		//move to lobby
	}
	
	@EventHandler
	void onMove(PlayerMoveEvent event)
	{
		Player p = event.getPlayer();
		if(_players_score.containsKey(p))
		{
			if(!has_started)
			{
				event.setCancelled(true);
			}
			if(p.getLocation().getBlockY() < _layer_y-3)
			{
				moveToLobbyPlayer(p);				
			}
		}
	}
	
	@EventHandler
	void onFoodLevel(FoodLevelChangeEvent event)
	{
		System.out.println("food level 1");
		if(event.getEntity() instanceof Player)
		{
			Player p =(Player) event.getEntity();
			if(_players_score.containsKey(p))
			{
				System.out.println("food level 1");
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	void onQuit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		if(_players_score.containsKey(p) || _players_lobby.containsKey(p))
		{
			moveToLobbyPlayer(p);
			_spleefHandler.gameEndForPlayer(p);
			
		}
	}
	
	void anti_standBlocks(Player p, Material mat)
	{
		p.sendMessage(ChatColor.RED + "[ANTI_STAND]: You have stand too long same spot!");
		Location loc = p.getLocation();
		int z;
		for(int  x = loc.getBlockX()-1; x < loc.getBlockX()+2; ++x)
		{
			for( z = loc.getBlockZ()-1; z < loc.getBlockZ()+2; ++z)
			{
				Block b = p.getWorld().getBlockAt(x, loc.getBlockY()-1, z);
				b.setType(mat);
				_remove_blocks.add(b);
			}
			z = loc.getBlockZ()-1;
		}
	}
	
	void runnableAsyc()
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				if(_cd.isCooldownReady("round") && !has_ended)
				{
					endGame();
					this.cancel();
				}
				
				if(has_ended)
					return;
				
				if(!_cd.isCooldownReady("start") && !has_started)
				{					
					String str = ChatColor.GREEN + ""+ChatColor.BOLD + "Game starts in: "+ChatColor.WHITE+(_cd.GetCdInSeconds("start")+1) + " s";
					_gameCard.sendMessageToALL(str);
				}
				else
				{
					if(!has_started)
					{
						String str = ChatColor.GREEN + ""+ChatColor.BOLD + "Game starts in: "+ChatColor.WHITE+"GO!";
						_gameCard.sendMessageToALL(str);
					}
					has_started = true;
					
					if(!_remove_blocks.isEmpty())
					{
						for(Block b : _remove_blocks)
						{
							b.setType(Material.AIR);
						}
						_remove_blocks.clear();
					}
					
					if(_anti_stand > 0)
					{
						for(Map.Entry<Player,Integer> entry : _players_score.entrySet())
						{
							Player p = entry.getKey();
							
							Pair<Location, Integer> data = _player_anti_stands.get(p);
							Location p_loc = p.getLocation();
							Location last_loc = data.getFirst();

							if(p_loc.distance(last_loc) < 2)
							{
								_player_anti_stands.put(p, new Pair<>(p_loc, data.getSecond()+1));
								if(data.getSecond() > _anti_stand-1)
								{
									anti_standBlocks(p, Material.RED_STAINED_GLASS);
									_player_anti_stands.put(p, new Pair<>(p_loc, 0));
								}
							}
							else
							{
								_player_anti_stands.put(p, new Pair<>(p_loc, 0));
							}
						}
					}
					
				}
				
				
				
				
				
			}
		}.runTaskTimer(_main, 20, 20);
	}

}

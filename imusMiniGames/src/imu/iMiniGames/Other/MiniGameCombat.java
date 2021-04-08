package imu.iMiniGames.Other;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.iMiniGames.Handlers.CombatGameHandler;
import imu.iMiniGames.Main.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

@SuppressWarnings("deprecation")
public class MiniGameCombat extends MiniGame implements Listener
{
	CombatGameHandler _combatHandler;
	CombatGameCard _gameCard;
	Cooldowns _cd;

	boolean has_started = false;
	boolean has_ended = false;
	
	int _start_delay = 3;
	int _total_players = 0;

	int _roundEnd_warning = 10;
	
	int _best_of = 1;
	
	Location mid_loc = null;
	double _max_distance = 0;
	BukkitTask run;
	BukkitTask runAsync;
	
	ItemStack[] _kit;
	
	int draw_count = 0;
	int draw_max = 3;
	
	
	ArrayList<Entity> _entities = new ArrayList<>();
	
	public MiniGameCombat(Main main, CombatGameHandler combatHandler,CombatGameCard gameCard,String minigameName) 
	{
		super(main,minigameName);
		
		_combatHandler = combatHandler;
		_gameCard = gameCard;
		
		_main.getServer().getPluginManager().registerEvents(this, main);
		_best_of = _gameCard.get_combatDataCard().get_bestOfAmount();
		broadCastStart();

		mid_loc = _gameCard.get_arena().getArenas_middleloc();
		_max_distance = _gameCard.get_arena().getArena_radius();
		_kit = _gameCard.get_combatDataCard().get_kit().get_kitInv();
		_spectator_loc = gameCard.get_arena().get_spectator_lobby();
		
	}
	
	void putPotionEffects(Player p)
	{
		if(!_gameCard.get_combatDataCard().get_invPotionEffects().isEmpty())
		{
			for(Entry<PotionEffectType, PotionEffect> potion :_gameCard.get_combatDataCard().get_invPotionEffects().entrySet())
			{
				PotionEffect ef =new PotionEffect(potion.getKey(), _roundTime* 20, potion.getValue().getAmplifier());
				p.addPotionEffect(ef);
				
			}
		}
	}

	void setupPlayerForStart(Player p)
	{
		p.setGameMode(GameMode.SURVIVAL);
		PlayerInventory inv = p.getInventory();
		inv.clear();
		
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setFireTicks(-20);
		_combatHandler.removePotionEffects(p);
		
		inv.setContents(_kit);
		putPotionEffects(p);
		
		//inv.addItem(new ItemStack(Material.DIAMOND_SWORD));		
	}
	
	
	void keepFighterAlive()
	{
		for(Map.Entry<Player,Integer> entry : _players_score.entrySet())
		{
			Player p = entry.getKey();
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setFireTicks(-20);
		}
	}
	
	void broadCastStart()
	{
		if(!_combatHandler.is_enable_broadcast())
			return;
		
		for(Player p : _main.getServer().getOnlinePlayers())
		{
			if(_main.get_combatGameHandler().isPlayerInArena(p))
			continue;
			
			p.sendMessage(ChatColor.AQUA + "=== COMBAT GAME STARTED! ===");
			p.sendMessage(ChatColor.YELLOW + "Arena: "+_gameCard.get_arena().get_arenaNameWithColor());
			p.sendMessage(ChatColor.YELLOW + "Players: "+ChatColor.AQUA+_gameCard.getPlayersString());
			if(_gameCard.get_bet() > 0)
			{
				p.sendMessage(ChatColor.YELLOW + "Winner gets: "+ChatColor.GREEN+_gameCard.get_total_bet());
			}

			TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&b=== &dSTART SPECTATING &l&a(Click) &b==="));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/mg spectate combat "+_gameCard.get_arena().get_name()));
			msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click teleport to Spectate!")));
			p.spigot().sendMessage(msg);
		}
		
	}
	
	void broadCastEnd(Player winner)
	{
		if(!_combatHandler.is_enable_broadcast())
			return;
		
		_main.getServer().broadcastMessage(ChatColor.DARK_AQUA + "=== COMBAT GAME ENDED! ===");
		_main.getServer().broadcastMessage(ChatColor.YELLOW + "Arena: "+_gameCard.get_arena().get_arenaNameWithColor());
		_main.getServer().broadcastMessage(ChatColor.YELLOW + "Players: "+ChatColor.DARK_AQUA+_gameCard.getPlayersString());
		if(_gameCard.get_bet() > 0 && winner != null)
		{
			_main.getServer().broadcastMessage(ChatColor.AQUA + winner.getName()+ChatColor.YELLOW+" was Winner and got: "+ChatColor.GREEN+_gameCard.get_total_bet()+ChatColor.YELLOW+ChatColor.BOLD + " Congrats!");
		}else if(winner != null)
		{
			_main.getServer().broadcastMessage(ChatColor.AQUA + winner.getName()+ChatColor.YELLOW+" was Winner!"+ChatColor.YELLOW+ChatColor.BOLD + " Congrats!");
		}else
		{
			_main.getServer().broadcastMessage(ChatColor.RED + "The game was DRAW!");
		}
		_main.getServer().broadcastMessage(ChatColor.DARK_AQUA + "========================");
	}
	
	void stopRunnables()
	{
		if(run != null)
		{
			run.cancel();
		}
		if(runAsync != null)
		{
			runAsync.cancel();
		}
	}
	
	public void Start()
	{
		has_started = false;
		has_ended = false;
		_cd = new Cooldowns();

		_total_players = _gameCard.get_players_accept().size();
		int count = 0;
		_round++;
		
		
		for(Map.Entry<Player,Integer> entry : _players_score.entrySet())
		{
			Player p = entry.getKey();
			setupPlayerForStart(p);
			p.teleport(_gameCard.get_arena().getSpawnpointLoc(count));

			count++;
		}
		
		_cd.setCooldownInSeconds("round", _roundTime);
		_cd.setCooldownInSeconds("roundCloseToEnd", _roundTime-_roundEnd_warning);
		
		_cd.setCooldownInSeconds("start", _start_delay);
		
		stopRunnables();
		
		runnable();
		runnableAsync();
		
		String str1 = ChatColor.YELLOW + "========================";
		
		
		_gameCard.sendMessageToALL(" ");
		_gameCard.sendMessageToALL(" ");
		_gameCard.sendMessageToALL(" ");
		_gameCard.sendMessageToALL(" ");
		_gameCard.sendMessageToALL(" ");
		_gameCard.sendMessageToALL(str1);
		_gameCard.sendMessageToALL(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + "ROUND: "+ChatColor.WHITE+_round 
		+ ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + " ROUND LASTS "+ChatColor.WHITE+_roundTime+" s");
		_gameCard.sendMessageToALL(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + "Best of "+ChatColor.WHITE+_best_of);
		_gameCard.sendMessageToALL(str1);
		if(_gameCard.get_bet() > 0 && _round == 1)
		{
			_gameCard.sendMessageToALL(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + "Winner gets: "+ChatColor.DARK_GREEN+_gameCard.get_total_bet());
			_gameCard.sendMessageToALL(str1);
		}
		
		if(_round > 1)
		{
			for(Entry<Player,Integer> p : _players_score.entrySet())
			{
				_gameCard.sendMessageToALL(ChatColor.AQUA + p.getKey().getName() + ChatColor.GOLD+" score:  "+ChatColor.WHITE+p.getValue()); 
			}
		}
		_gameCard.sendMessageToALL(" ");
	}
	
	Player checkBestOf()
	{
		addLobbyPlayersToScore();
		
		Player winner = null;
		for(Entry<Player,Integer> p : _players_score.entrySet())
		{
			if(p.getValue() >= _best_of)
			{
				winner = p.getKey();
				break;
			}
		}		
		return winner;
	}
	
	public void endGame()
	{
		has_ended = true;
		
		for(Entity e : _entities)
		{
			e.remove();
		}
		_entities.clear();
				
		Player round_winner;
		if(_total_players > 1 || _total_players == 0)
		{
			round_winner = null;
		}else
		{
			 round_winner = (Player)_players_score.keySet().toArray()[0];
			 addPointsPlayer(round_winner, 1);
		}
		
		
		
		if(_best_of != 1)
		{
			Player real_winner = checkBestOf();
			if(real_winner == null && _round < _gameCard.get_players_accept().size()*_best_of-1+5)
			{
				Start();
				return;
			}
		}
		
		broadCastEnd(round_winner);
		
		String msg = ChatColor.DARK_PURPLE +""+ChatColor.BOLD+ "Combat Game Has Ended";	
		for(UUID uuid: _gameCard.get_players_accept().keySet())
		{
			Player p = Bukkit.getPlayer(uuid);
			
			if(!p.isOnline())
			{
				continue;
			}
			p.sendMessage(ChatColor.DARK_PURPLE + "=================================");
			p.sendMessage(msg);
			
			_combatHandler.removePotionEffects(p);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 5));
			
			if(round_winner == null )
			{
				p.sendMessage(ChatColor.DARK_PURPLE + "=================================");
				if(_gameCard.get_bet() > 0)
				{
					p.sendMessage(ChatColor.RED + ""+ChatColor.BOLD + "It was DRAW! Money has send to the server! Thanks for playing!");
				}else
				{
					p.sendMessage(ChatColor.RED + ""+ChatColor.BOLD + "It was DRAW!");
				}
				p.sendMessage(ChatColor.DARK_PURPLE + "=================================");
			}

			_combatHandler.gameEndForPlayer(p);
			
		}
		
		_players_lobby.clear();
		_players_score.clear();
		_combatHandler.gameHasEnded(_gameCard, round_winner);
		stopRunnables();
		HandlerList.unregisterAll(this);
	}
	
	void moveToLobbyPlayer(Player p)
	{
		p.getInventory().clear();
		p.teleport(_gameCard.get_arena().get_spectator_lobby());
		
		movePlayerToLobbyHash(p);
		
		p.setHealth(20);
		p.setFireTicks(-20);
		
		_combatHandler.removePotionEffects(p);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 5));
		
		p.sendMessage(ChatColor.DARK_GRAY + "You have been moved to spetator lobby! Better luck next time!");
		
		_total_players--;
		checkIFendGame();
		
		//move to lobby
	}
	void checkIFendGame()
	{
		if(_total_players <= 1 || _gameCard.get_players_accept().size() < 2)
		{
			endGame();
		}
	}
	void playerLeft(Player p)
	{
		//_combatHandler.removePotionEffects(p);
		_total_players--;
		_players_score.remove(p);
		_gameCard.get_players_accept().remove(p.getUniqueId());

		checkIFendGame();
	}
	
	@EventHandler
	void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(event.getEntity().getShooter() instanceof Player)
		{
			Player p = (Player) event.getEntity().getShooter();
			if(_players_score.containsKey(p) || _players_lobby.containsKey(p))
			{
				if(event.getEntity().getType() == EntityType.TRIDENT)
				{
					_entities.add(event.getEntity());
				}
			}
			
		}
		
	}
	
	@EventHandler
	void onMove(PlayerMoveEvent event)
	{
		if(has_ended)
			return;
		
		Player p = event.getPlayer();
		if(_players_score.containsKey(p))
		{
			if(!has_started)
			{
				event.setCancelled(true);
				return;
			}
			
			if(p.getLocation().distance(mid_loc) > _max_distance)
			{
				p.sendMessage(ChatColor.RED + "You went too far from arena!");
				moveToLobbyPlayer(p);
				return;
			}
		}
	}
	
	@EventHandler
	void onQuit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		if(_players_score.containsKey(p) )
		{
			playerLeft(p);
			
		}
		if(_players_lobby.containsKey(p))
		{
			playerLeft(p);
		}
		if(_players_spectators.containsKey(p))
		{
			teleportSpectatorToBack(p);
		}
	}
	
	@EventHandler
	void onDrop(PlayerDropItemEvent event)
	{
		Player p = event.getPlayer();
				
		if(_players_score.containsKey(p) || _players_spectators.containsKey(p) || _players_lobby.containsKey(p))
		{
			event.setCancelled(true);
					
		}
	}
	
	@EventHandler
	void onPickUp( PlayerPickupItemEvent event)
	{
		Player p = event.getPlayer();
		if(_players_score.containsKey(p) || _players_spectators.containsKey(p) || _players_lobby.containsKey(p))
		{
			Entity entity = null;
			for(Entity ent : _entities)
			{
				if(ent.getUniqueId() == event.getItem().getUniqueId())
				{
					entity = ent;
					break;
				}
				
			}
			
			if(entity != null)
			{
				_entities.remove(entity);
				return;
			}
			event.setCancelled(true);	
		}
	}
	
	@EventHandler
	void onCMDwrite(PlayerCommandPreprocessEvent event)
	{
		if(_players_score.containsKey(event.getPlayer()) || _players_lobby.containsKey(event.getPlayer()))
		{
			event.getPlayer().sendMessage(ChatColor.RED + "You are in Combat game! Can't use commands!");
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	void onEntityDamage(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player p = (Player)event.getEntity();
			if(_players_score.containsKey(p))
			{
				if(!has_started)
					event.setCancelled(true);
				
				if(p.getHealth() - event.getFinalDamage() <= 0.5)
				{
					event.setCancelled(true);
					moveToLobbyPlayer(p);
					return;
				}
			}
		}
	}
		
	void runnableAsync()
	{
		runAsync = new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				if(has_started)
				{
					if(!_gameCard.get_combatDataCard().get_invPotionEffects().containsKey(PotionEffectType.GLOWING))
					{
						for(Map.Entry<Player,Integer> entry : _players_score.entrySet())
						{
							Player p = entry.getKey();
							if(p.isGlowing())
							{
								p.setGlowing(false);
							}
						}
					}
					
					
				}
			}
		}.runTaskTimerAsynchronously(_main, 0, 20);
	}
	void runnable()
	{
		run = new BukkitRunnable() 
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
					keepFighterAlive();
				}
				else
				{
					if(!has_started)
					{
						String str = ChatColor.GREEN + ""+ChatColor.BOLD + "Game starts in: "+ChatColor.WHITE+"GO!";
						_gameCard.sendMessageToALL(str);
					}
					

					has_started = true;
					
					if(_cd.isCooldownReady("roundCloseToEnd"))
					{
						String str = ChatColor.RED + ""+ChatColor.BOLD + "Ends in "+ChatColor.WHITE+ (_cd.GetCdInSeconds("round")+1) + " s";
						_gameCard.sendMessageToALL(str);
					}					
				}
				
				
				
				
				
			}
		}.runTaskTimer(_main, 20, 20);
	}

}

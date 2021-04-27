package imu.iMiniGames.Other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.iMiniGames.Arenas.CombatArena;
import imu.iMiniGames.Enums.COMBAT_ATTRIBUTE;
import imu.iMiniGames.Handlers.CombatGameHandler;
import imu.iMiniGames.Leaderbords.CombatLeaderBoard;
import imu.iMiniGames.Leaderbords.CombatPlayerBoard;
import imu.iMiniGames.Leaderbords.PlayerVsPlayerBoard;
import imu.iMiniGames.Main.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

@SuppressWarnings("deprecation")
public class MiniGameCombat extends MiniGame implements Listener
{
	CombatGameHandler _combatHandler;
	CombatLeaderBoard _leaderBoard;
	CombatDataCard _dataCard;
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
	
	HashMap<UUID, ItemStack[]> _ownGear = new HashMap<>();
	int draw_count = 0;
	int draw_max = 3;
	
	HashMap<UUID, ArrayList<ItemStack>> _player_gears_check = new HashMap<>();
	
	ArrayList<Entity> _entities = new ArrayList<>();
	
	boolean _show_dps = false;
	boolean _no_arrow_spread = false;
	
	
	public MiniGameCombat(Main main, CombatGameHandler combatHandler, CombatGameCard gameCard,String minigameName) 
	{
		super(main,minigameName);
		
		_combatHandler = combatHandler;
		_gameCard = gameCard;

		_dataCard = (CombatDataCard) gameCard.getDataCard();
		
		_main.getServer().getPluginManager().registerEvents(this, main);
		_best_of = _dataCard.get_bestOfAmount();
		broadCastStart();
		
		CombatArena arena = (CombatArena) _gameCard.get_arena();
		mid_loc = arena.getArenas_middleloc();
		_max_distance = arena.getArena_radius();
		//_kit = _gameCard.get_combatDataCard().get_kit().get_kitInv();
		_ownGear = _dataCard.get_ownGear();
		_spectator_loc = gameCard.get_arena().get_spectator_lobby();
		
		_show_dps = _dataCard.getAttribute(COMBAT_ATTRIBUTE.SHOW_DMG) == 0 ? false : true;
		_no_arrow_spread = _dataCard.getAttribute(COMBAT_ATTRIBUTE.NO_ARROW_SPREAD) == 0 ? false : true;
		_leaderBoard = main.get_combatManager().getLeaderBoard();
		
		setupGearData();
		
	}
	
	@Override
	public void startGame() 
	{
		has_started = false;
		has_ended = false;
		_cd = new Cooldowns();

		_total_players = _gameCard.get_players_accept().size();
		int count = 0;
		_round++;
		
		
		for(Player p  : _players_ingame.keySet())
		{
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
		
		
		
		_gameCard.sendMessageToALL(" ");
	}
	
	@Override
	public void endGame() 
	{
		if(has_ended)
			return;
		
		has_ended = true;
		stopRunnables();
		
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
			 round_winner = (Player)_players_ingame.keySet().toArray()[0];
			 addPointsPlayer(round_winner, 1);
		}
		
		addLobbyPlayersToScore();
		
		for(Entry<Player,MiniGamePlayerStats> pStats : _players_ingame.entrySet()) // LOOP ALL PLEARYS
		{
			Player p = pStats.getKey();
			MiniGamePlayerStats stats = pStats.getValue();
			//_gameCard.sendMessageToALL(ChatColor.AQUA + p.getName() + ChatColor.GOLD+" score:  "+ChatColor.WHITE+stats.get_score());
			_gameCard.sendMessageToALL(ChatColor.translateAlternateColorCodes('&', "&9=== &b"+p.getName()+" &2STATS &9 ===="));
			_gameCard.sendMessageToALL(ChatColor.translateAlternateColorCodes('&', "&1===> &6Score: &f"+stats.get_score()));
			if(_show_dps)
			{
				if(stats.get_hp() > 0)
				{
					//_gameCard.sendMessageToALL(ChatColor.AQUA + p.getName() + ChatColor.RED+" Hp left:  "+ChatColor.WHITE+stats.get_hp());
					_gameCard.sendMessageToALL(ChatColor.translateAlternateColorCodes('&', "&1===> &cHp left: &f"+twoDesimalsAndHearts(stats.get_hp())+" &7Hearts"));
				}
				//_gameCard.sendMessageToALL(ChatColor.AQUA + p.getName() + ChatColor.GREEN+" Total dmg done:  "+ChatColor.WHITE+stats.get_damage_done());
				_gameCard.sendMessageToALL(ChatColor.translateAlternateColorCodes('&', "&1===> &2Total dmg done: &f"+twoDesimalsAndHearts(stats.get_damage_done())+" &7Hearts" ));
				
				//_gameCard.sendMessageToALL(ChatColor.AQUA + p.getName() + ChatColor.DARK_RED+" Total dmg taken:  "+ChatColor.WHITE+stats.get_damage_taken());
				_gameCard.sendMessageToALL(ChatColor.translateAlternateColorCodes('&', "&1===> &4Total dmg taken: &f"+twoDesimalsAndHearts(stats.get_damage_taken())+" &7Hearts"));
			}
			
			_gameCard.sendMessageToALL(" ");
		}
		
		
		if(_best_of != 1)
		{
			Player real_winner = checkBestOf();
			if(real_winner == null && _round < _gameCard.get_players_accept().size()*_best_of-1+5)
			{
				startGame();
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
			
			leaderboardThings(p, round_winner);
			
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
			}else 
			{
				
			}

			
			_combatHandler.gameEndForPlayer(_gameCard, p);
			
		}
		
		_players_off_game.clear();
		_players_ingame.clear();
		
		_combatHandler.matchEND(_gameCard, round_winner);
		
		HandlerList.unregisterAll(this);
	}

	
	void setupGearData()
	{

		for(UUID uuid : _gameCard.get_players_accept().keySet())
		{
			ArrayList<ItemStack> arr = new ArrayList<>();
			for(ItemStack s : _ownGear.get(uuid))
			{
				if(s == null)
					continue;
				
				ItemStack clone  = s.clone();
				if(_itemM.isArmor(clone) || _itemM.isTool(clone))
				{
					_itemM.setDamage(clone, 0);
				}
				
				arr.add(clone);
			}
			_player_gears_check.put(uuid, arr);
		}
	}
	
	void putPotionEffects(Player p)
	{
		if(!_dataCard.get_invPotionEffects().isEmpty())
		{
			for(Entry<PotionEffectType, PotionEffect> potion :_gameCard.getDataCard().get_invPotionEffects().entrySet())
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
		
		inv.setContents(_ownGear.get(p.getUniqueId()));
		putPotionEffects(p);
		
		//inv.addItem(new ItemStack(Material.DIAMOND_SWORD));		
	}
	
	
	void keepFighterAlive()
	{
		for(Player p : _players_ingame.keySet())
		{
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

	Player checkBestOf()
	{		
		Player winner = null;
		for(Entry<Player,MiniGamePlayerStats> p : _players_ingame.entrySet())
		{
			if(p.getValue().get_score() >= _best_of)
			{
				winner = p.getKey();
				break;
			}
		}		
		return winner;
	}
	
	double twoDesimalsAndHearts(double d)
	{
		return (Math.round((d/2) * 100.0)/100.0);
	}
	void leaderboardThings(Player p, Player winner)
	{
		UUID uuid = p.getUniqueId();
		
		CombatPlayerBoard board =(CombatPlayerBoard) _leaderBoard.getPlayerBoard(uuid);
		MiniGamePlayerStats stats = _players_ingame.containsKey(p) == true ? _players_ingame.get(p) :_players_off_game.get(p);
		if(board == null)
		{
			board = new CombatPlayerBoard(p.getName(), uuid);
		}
		
		if(winner == null ||  winner.getUniqueId() !=  uuid)
		{
			board.set_Loses(board.get_Loses()+1);
			board.set_total_bet_lost_amount(board.get_total_bet_lost_amount() + _gameCard.get_bet());
			
			board.get_weekly().set_Loses(board.get_weekly().get_Loses() + 1);
			board.get_weekly().set_total_bet_lost_amount(board.get_weekly().get_total_bet_lost_amount() + _gameCard.get_bet());
			System.out.println("TOTAL PLAYERS: "+_gameCard.getTotalPlayers());
			if(_gameCard.getTotalPlayers() == 2 &&  winner != null)
			{
				PlayerVsPlayerBoard pvpBoard = board.get_pvp_playerBoard(winner.getUniqueId());
				pvpBoard.set_lost(pvpBoard.get_lost() + 1);
				pvpBoard.set_total_bet_lost_amount(pvpBoard.get_total_bet_lost_amount() +_gameCard.get_bet());
				//board.putPvpBoard(winner.getUniqueId(), pvpBoard);
				
				CombatPlayerBoard winner_board =(CombatPlayerBoard) _leaderBoard.getPlayerBoard(winner.getUniqueId());
				pvpBoard = winner_board.get_pvp_playerBoard(uuid);
				pvpBoard.set_wins(pvpBoard.get_wins()+1);
				pvpBoard.set_total_bet_wons_amount(pvpBoard.get_total_bet_wons_amount() +(_gameCard.get_total_bet()-_gameCard.get_bet()));
				//board.putPvpBoard(uuid, pvpBoard);
				
				
			}
		}
		else
		{
			board.set_Wins(board.get_Wins() + 1);
			board.set_total_bet_wins_amount(board.get_total_bet_wins_amount() + (_gameCard.get_total_bet() > 0 ? (_gameCard.get_total_bet()-_gameCard.get_bet()) : 0));
			
			board.get_weekly().set_Wins(board.get_weekly().get_Wins() + 1);
			board.get_weekly().set_total_bet_wins_amount(board.get_weekly().get_total_bet_wins_amount() + (_gameCard.get_total_bet() > 0 ? (_gameCard.get_total_bet()-_gameCard.get_bet()) : 0));
			
			
			
		}	
		board.set_total_kills(board.get_total_kills()+stats.get_kills());
		board.set_total_deaths(board.get_total_deaths() + stats.get_deaths());
		board.set_total_dmg_done(board.get_total_dmg_done()+stats.get_damage_done());
		board.set_total_dmg_taken(board.get_total_dmg_taken() + stats.get_damage_taken());
		
		board.get_weekly().set_total_kills(board.get_weekly().get_total_kills() + stats.get_kills());
		board.get_weekly().set_total_deaths(board.get_weekly().get_total_deaths() + stats.get_deaths());
		board.get_weekly().set_total_dmg_done(board.get_weekly().get_total_dmg_done() + stats.get_damage_done());
		board.get_weekly().set_total_dmg_taken(board.get_weekly().get_total_dmg_taken() + stats.get_damage_taken());
		
		_leaderBoard.setPlayerBoard(uuid, board);
		
	}
	public void ndGame()
	{
		
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
		_players_ingame.remove(p);
		_gameCard.get_players_accept().remove(p.getUniqueId());

		checkIFendGame();
	}
	
	@EventHandler
	void onUse(PlayerInteractEvent event)
	{
		if(!has_started)
		{
			if(_players_ingame.containsKey(event.getPlayer()))
			{
				event.setCancelled(true);
			}
		}
		if(event.getItem() == null || event.getItem().getType() == Material.AIR)
			return;
		
		if(has_started && _players_ingame.containsKey(event.getPlayer()))
		{
			Material used_mat = event.getItem().getType();
			if(used_mat == Material.SPLASH_POTION || used_mat == Material.LINGERING_POTION)
			{
				_gameCard.checkAndReduceCombatConsumable(event.getPlayer().getUniqueId(), event.getItem(), -1);
			}
		}
	}
	
	@EventHandler
	void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(has_ended)
			return;
		
		if(event.getEntity().getShooter() instanceof Player)
		{
			Player p = (Player) event.getEntity().getShooter();
			if(_players_ingame.containsKey(p) || _players_off_game.containsKey(p))
			{
				if(event.getEntity().getType() == EntityType.TRIDENT || event.getEntity().getType() == EntityType.ARROW)
				{
					_entities.add(event.getEntity());
				}
				
				if(_no_arrow_spread && event.getEntity().getType() == EntityType.ARROW)
				{
					Arrow arrow = (Arrow)event.getEntity();
					arrow.setVelocity(p.getLocation().getDirection().multiply(arrow.getVelocity().length()));
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
		if(_players_ingame.containsKey(p))
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
	void onConsumeItem(PlayerItemConsumeEvent event)
	{
		if(has_ended)
			return;
		
		if(_players_ingame.containsKey(event.getPlayer()))
		{
			_gameCard.checkAndReduceCombatConsumable(event.getPlayer().getUniqueId(), event.getItem(), -1);
		}
	}
	
	@EventHandler
	void onQuit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		if(_players_ingame.containsKey(p) )
		{
			playerLeft(p);
			
		}
		if(_players_off_game.containsKey(p))
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
				
		if(_players_ingame.containsKey(p) || _players_spectators.containsKey(p) || _players_off_game.containsKey(p))
		{
			event.setCancelled(true);
					
		}
	}
	
	@EventHandler
	void onPickUp( PlayerPickupItemEvent event)
	{
		Player p = event.getPlayer();
		if(_players_ingame.containsKey(p) || _players_spectators.containsKey(p) || _players_off_game.containsKey(p))
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
		if(_players_ingame.containsKey(event.getPlayer()) || _players_off_game.containsKey(event.getPlayer()))
		{
			event.getPlayer().sendMessage(ChatColor.RED + "You are in Combat game! Can't use commands!");
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if(has_ended || !_show_dps)
			return;
		
		
	}
	
	@EventHandler
	void onEntityDamage(EntityDamageEvent event)
	{
		if(has_ended)
			return;
		
		if(event.getEntity() instanceof Player)
		{
			
			if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
				if(event2.getDamager() instanceof Projectile)
				{
					Projectile projectile = (Projectile)event2.getDamager();
					projectile.getShooter();
				}
				
				if(event2.getEntity() instanceof Player )
				{
					Player victim = (Player) event2.getEntity();
					double dmg = event2.getFinalDamage();
					if(_players_ingame.containsKey(victim))
					{						
						Player damager = null;
						
						if(event2.getDamager() instanceof Projectile)
						{
							Projectile projectile = (Projectile)event2.getDamager();
							if(projectile.getShooter() instanceof Player)
							{
								damager = (Player) projectile.getShooter();
							}
							
						}
						if(event2.getDamager() instanceof Player)
						{
							damager = (Player) event2.getDamager();
						}
						
						victim.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDamage Taken: &5"+twoDesimalsAndHearts(dmg)+ " &7Hearts"));
						
						if(damager != null && _players_ingame.containsKey(damager))
						{
							_players_ingame.get(damager).addDamageDone(dmg);
							damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Damage Done: &5"+twoDesimalsAndHearts(dmg)+" &7Hearts"));
						}
						_players_ingame.get(victim).set_hp(victim.getHealth() - event.getFinalDamage());
						_players_ingame.get(victim).addDamageTaken(event.getFinalDamage());
						
						if(victim.getHealth() - dmg <= 0.5)
						{
							event2.setCancelled(true);
							_players_ingame.get(victim).addDeaths(1);
							_players_ingame.get(damager).addKills(1);
							moveToLobbyPlayer(victim);
							return;
						}
						
					}
				}
				
			}
			else if (event instanceof EntityDamageEvent)
			{
				Player p = (Player)event.getEntity();
				if(_players_ingame.containsKey(p))
				{
					if(!has_started)
						event.setCancelled(true);

					_players_ingame.get(p).set_hp(p.getHealth() - event.getFinalDamage());
					_players_ingame.get(p).addDamageTaken(event.getFinalDamage());
									
					if(p.getHealth() - event.getFinalDamage() <= 0.5)
					{
						event.setCancelled(true);
						moveToLobbyPlayer(p);
						return;
					}
				}
			}
			
		}
	}
		
	boolean isItemValid(UUID uuid, ItemStack s)
	{
		if(s.getType() == Material.CROSSBOW)
			return true;
		
		ArrayList<ItemStack> gear = _player_gears_check.get(uuid);
		ItemStack copy = s.clone();
		
		if(_itemM.isArmor(copy) || _itemM.isTool(copy))
		{
			_itemM.setDamage(copy, 0);
//			if(copy.getType() == Material.CROSSBOW)
//			{
//				CrossbowMeta meta = (CrossbowMeta)copy.getItemMeta();
//				meta.addChargedProjectile(new ItemStack(Material.ARROW));
//				copy.setItemMeta(meta);
//			}
		}
		
		for(ItemStack ge : gear)
		{
			if(ge == null)
				continue;
			
			
			if(ge.isSimilar(copy))
			{
				return true;
			}
			
		}
		
		return false;
	}
	void runnableAsync()
	{
		runAsync = new BukkitRunnable() 
		{
			int count = 0;
			@Override
			public void run() 
			{
				if(has_ended)
					return;
				
				if(has_started)
				{
					if(!_dataCard.get_invPotionEffects().containsKey(PotionEffectType.GLOWING))
					{
						for(Player p : _players_ingame.keySet())
						{
							if(p.isGlowing())
							{
								p.setGlowing(false);
							}
						}
					}
				}
				
				if(count % 3 == 0 && !_dataCard.isOwnGearKit)
				{
					for(Player p : _players_ingame.keySet())
					{
						
						if(_player_gears_check.containsKey(p.getUniqueId()))
						{
							for(ItemStack s : p.getInventory().getContents())
							{
								if(s == null)
									continue;
								
								if(!isItemValid(p.getUniqueId(), s))
								{
									System.out.println("imusMiniGames:Combat: item isnt valid!: "+s);
									s.setAmount(0);
								}
							}
						}
					}
				}
				count++;
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

package imu.DontLoseItems.CustomEnd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.DontLoseItems.main.DontLoseItems;
import imu.iAPI.Other.ConfigMaker;


public class EndEvents implements Listener
{
	public static EndEvents Instance;

	public UnstableEnd UnstableEnd;	
	
	@SuppressWarnings("unused")
	private BukkitTask _asyncTask;
	
	private HashMap<INC_ID, UnstableIncrease> _increases;
	private HashMap<UUID, UnstableEnd_Player> _players;
	
	private HashSet<Material> _validBlocks;
	public EndEvents()
	{
		Instance = this;
		UnstableEnd = new UnstableEnd();
		_players = new HashMap<>();
		InitIncrease();
		//GetSettings();
		RunnableAsync();
		
	}
	public void InitValidBlocks()
	{
		_validBlocks = new HashSet<>();
		_validBlocks.add(Material.END_STONE);
		_validBlocks.add(Material.END_STONE_BRICK_SLAB);
		_validBlocks.add(Material.END_STONE_BRICK_WALL);
		_validBlocks.add(Material.END_STONE_BRICKS);
		_validBlocks.add(Material.END_ROD);
	}
	public enum INC_ID
	{
		PLACING_OTHER_BLOCKS,
		BREAKING_OTHER_BLOCKS,
	}
	public void InitIncrease()
	{
		_increases = new HashMap<>();
		
		_increases.put(INC_ID.BREAKING_OTHER_BLOCKS, new  UnstableIncrease(5));
		_increases.put(INC_ID.PLACING_OTHER_BLOCKS, new  UnstableIncrease(8));
	}
	public UnstableIncrease GetIncrease(INC_ID id)
	{
		if(!_increases.containsKey(id)) return null;
		
		return _increases.get(id);
	}
	public void OnDisabled()
	{
		
	}
		
	void RunnableAsync()
	{
		
		_asyncTask = new BukkitRunnable() 
		{			
			int counter = 0;
			
			@Override
			public void run() 
			{
				
				LinkedList<Player> includedPlayers = new LinkedList<>();
				for(Player player : Bukkit.getOnlinePlayers())
				{
					if(IsPlayerUnstableArea(player)) 
					{
						includedPlayers.add(player);
					}
				}
				counter++;
				
				if(includedPlayers.size() == 0) return;
				
				new BukkitRunnable() 
				{
					
					@Override
					public void run()
					{
						for(Player player : includedPlayers)
						{
							AddUnstablePlayer(player);
						}
						
						includedPlayers.clear();
					}
				}.runTask(DontLoseItems.Instance);
				
			}
		}.runTaskTimerAsynchronously(DontLoseItems.Instance, 0, 20);	
	}
	
	public void AddUnstablePlayer(Player player)
	{
		_players.put(player.getUniqueId(), new UnstableEnd_Player(player));
	}
	
	public UnstableEnd_Player GetPlayer(Player player)
	{
		if(!_players.containsKey(player.getUniqueId())) AddUnstablePlayer(player);
		
		return _players.get(player.getUniqueId());
	}
	
	public void RemovePlayer(Player player)
	{
		if(_players.containsKey(player.getUniqueId())) _players.remove(player.getUniqueId());
	}
	public boolean IsPlayerUnstableArea(Player player)
	{
		if(!DontLoseItems.IsEnd(player)) return false;
		
		
		return true;
	}
	private void UpdateUnstapleVoid(INC_ID id)
	{
		UpdateUnstapleVoid(GetIncrease(id));
	}
	private void UpdateUnstapleVoid(UnstableIncrease increase)
	{		
		if(increase == null) return;
		
		BossBar bossBar = UnstableEnd.BOSS_BAR;
        
        //int counter = 0;
        
        bossBar.removeAll();
        Iterator<UnstableEnd_Player> it = _players.values().iterator();
		while (it.hasNext())
		{
			
			UnstableEnd_Player uPlayer = it.next();
			Player player = uPlayer.Player;
			if(player == null || !IsPlayerUnstableArea(player))
			{
				it.remove();
				continue;
			}
			
			bossBar.addPlayer(player);
			
		}
	
		
		if(_players.size() > 1)
		{
			UnstableEnd.AddState(increase.Amount / _players.size());
		}else { UnstableEnd.AddState(increase.Amount); }
		
		UnstableEnd.RefreshPorgress();
		
		if(!UnstableEnd.IsMax()) return;
		
		UnstableEnd.OnTrigger();
//		new BukkitRunnable() 
//		{
//            @Override
//            public void run() 
//            {
//            	UnstableEnd.AddState(amount);
//            }
//        }.runTaskLater(DontLoseItems.Instance, 20L); 
        
//		new BukkitRunnable() 
//		{
//            @Override
//            public void run() 
//            {
//                bossBar.removeAll();
//            }
//        }.runTaskLater(DontLoseItems.Instance, 200L); 
        
        
	}
	

	@EventHandler
	public void OnBlockPlace(BlockPlaceEvent e)
	{
		if(e.isCancelled()) return;
		
		if(!IsPlayerUnstableArea(e.getPlayer())) return;
			
		//if(e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
				
		if(_validBlocks.contains(e.getBlockPlaced().getType())) return;
		
		UpdateUnstapleVoid(INC_ID.PLACING_OTHER_BLOCKS);

	}
	

	@EventHandler
	public void OnBlockPlace(BlockBreakEvent e)
	{
		if(e.isCancelled()) return;
		
		if(!IsPlayerUnstableArea(e.getPlayer())) return;

		if(_validBlocks.contains(e.getBlock().getType())) return;
		
		UpdateUnstapleVoid(INC_ID.BREAKING_OTHER_BLOCKS);

	}

	public void OnEntitySpawn(CreatureSpawnEvent e) 
	{
		
		
	}
	
	@EventHandler
	public void OnEntityDamageByEntity(EntityDamageByEntityEvent e) 
	{
		
	    
	  }

	
	
	 
	
	@EventHandler
	public void OnEntityDamage(EntityDamageEvent e)
	{
		

	}
	
	@EventHandler
	public void OnProtectileHit(ProjectileHitEvent e)
	{
		
		
	}
	
	
	
	@EventHandler
	public void OnEntityDeath(EntityDeathEvent e)
	{
		
		
	}
	

	void GetSettings()
	{
		final String netherSettings = "EndSettings";
		ConfigMaker cm = new ConfigMaker(DontLoseItems.Instance, netherSettings + ".yml");
		FileConfiguration config = cm.getConfig();

		// String dot_dmg = netherSettings+".durabilityDamageFromDot";
		
		
		if (!config.contains(netherSettings + "."))
		{
			// default values
			DontLoseItems.Instance.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "DontLoseItems : Default config made!");

			cm.saveConfig();
			//return;
		}
		
		

		
		

	}

}

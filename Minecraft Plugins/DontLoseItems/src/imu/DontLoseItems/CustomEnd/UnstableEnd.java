package imu.DontLoseItems.CustomEnd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import imu.DontLoseItems.CustomEnd.EndCustomEvents.EndEvent;
import imu.DontLoseItems.CustomEnd.EndCustomEvents.EndEvent_RandomEntityTypeEnderman;
import imu.DontLoseItems.CustomEnd.EndCustomEvents.EndEvent_RandomPotionEffect;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Utilities.ImusUtilities;



public class UnstableEnd implements Listener
{
	public static UnstableEnd Instance;
	private double _state = 0;
	private final double MAX_STATE = 1000;
	public BossBar BOSS_BAR;
	private LinkedList<EndEvent> _allEvents;
	private LinkedList<EndEvent> _activeEvents;
	
	public int _totalRolls = 3;
	
	private Cooldowns _cds;
	
	private final String EVENT_ID = "EventID";
	
	private boolean isEventActive = false;
	
	private HashMap<UUID, Integer> _chestLootBases = new HashMap<>();
	private int _totalTicks = 0;
	public UnstableEnd()
	{
		Instance = this;
		_cds = new Cooldowns();
		BOSS_BAR =  Bukkit.createBossBar("Unstable Void", BarColor.PURPLE, BarStyle.SEGMENTED_20, BarFlag.DARKEN_SKY);
		BOSS_BAR.setTitle(ChatColor.BLACK + "| "+ChatColor.DARK_PURPLE+"Unstable Void"+ChatColor.BLACK+" |");
		InitEvents();
	}
	
	public void InitEvents()
	{
		_allEvents = new LinkedList<>();
		_activeEvents = new LinkedList<>();
		_allEvents.add(new EndEvent_RandomEntityTypeEnderman(10));
		_allEvents.add(new EndEvent_RandomPotionEffect(10));
	}
	
	public void OnDisabled()
	{
		_chestLootBases.clear();
		_allEvents.clear();
		_activeEvents.clear();
	}
	public void RefreshPorgress()
	{
		BOSS_BAR.setProgress(_state * 0.001);
	}
	public void SetState(double amount)
	{
		_state = amount;
		if(_state < 0 ) _state = 0;
		if(_state > MAX_STATE) _state = MAX_STATE;
	}
	
	public int GetPlayerBaseRollAmount(Player player)
	{
		if(_chestLootBases.containsKey(player.getUniqueId())) return _chestLootBases.get(player.getUniqueId());
		return 0;
	}
	
	public void AddPlayerChestlootBase(Player player, int amount)
	{
		int value = 0;
		if(_chestLootBases.containsKey(player.getUniqueId()))
		{
			value = _chestLootBases.get(player.getUniqueId());
		}
		
		if((value + amount) < 0) 
		{
			_chestLootBases.remove(player.getUniqueId());
			return;
		}
		_chestLootBases.put(player.getUniqueId(), value+amount);
	}
	public boolean IsMax()
	{
		return _state >= MAX_STATE;
	}
	public double GetStateAmount()
	{
		return _state;
	}
	
	public void AddState(double amount)
	{
		SetState(GetStateAmount() + amount);
	}
	
	public void AddState(UnstableIncrease increase)
	{
		SetState(GetStateAmount() + increase.Amount);
	}
	
	public void OnLoop()
	{
		
		if(_activeEvents.size() <= 0) return;
		
		
		GetCurrentEvent().OnOneTickLoop();
		
		if(_totalRolls % 20 == 0) CheckPlayers();
		
		OnEventChange();
		
		_totalTicks++;
	}
	
	private void CheckPlayers()
	{
		EndEvent event = GetCurrentEvent();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(EndEvents.Instance.IsPlayerUnstableArea(player)) 
			{
				if(GetCurrentEvent().HasPlayer(player)) continue;
				
				//joined some where ot event
				event.AddPlayer(player);
				event.OnPlayerJoinMiddleOfEvent(player);
				continue;
			}
			
			if(!event.HasPlayer(player)) return;
			
			event.RemovePlayer(player);
			GetCurrentEvent().OnPlayerLeftMiddleOfEvent(player);
		}
	}
	@EventHandler
	public void OnPlayerQuit(PlayerQuitEvent e)
	{
		if(!GetCurrentEvent().HasPlayer(e.getPlayer())) return;
		
		GetCurrentEvent().OnPlayerLeftMiddleOfEvent(e.getPlayer());
	}
	
	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent e)
	{
		if(GetCurrentEvent().HasPlayer(e.getPlayer())) return;
		
		if(EndEvents.Instance.IsPlayerUnstableArea(e.getPlayer())) 
			
		GetCurrentEvent().AddPlayer(e.getPlayer());
		GetCurrentEvent().OnPlayerJoinMiddleOfEvent(e.getPlayer());
	}
	
	private void AllTimeEventsHasBeenEnded()
	{
		System.out.println("All done");
		isEventActive = false;
		SetState(0);
		RefreshPorgress();
		_totalTicks = 0;
	}
	public void OnEventChange()
	{
		if(!_cds.isCooldownReady(EVENT_ID)) return;
		
		OnEventDeActive(_activeEvents.removeFirst());
		
		if(_activeEvents.size() <= 0) 
		{
			AllTimeEventsHasBeenEnded();
			return;
		}
		OnTriggerEvent(_activeEvents.getFirst());
		
		System.out.println("Event is changed");
	}
	
	private void SetCooldown(EndEvent event)
	{
		_cds.setCooldownInSeconds(EVENT_ID, event.GetDuration());
	}
	private void OnEventDeActive(EndEvent event)
	{
		event.ClearPlayers();
		event.UnRegisterBukkitEvents();
		event.OnEventEnd();
	}
	public EndEvent GetCurrentEvent()
	{
		return _activeEvents.getFirst();
	}
	private void OnTriggerEvent(EndEvent event)
	{
		SetCooldown(event);
		
		isEventActive = true;
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(!EndEvents.Instance.IsPlayerUnstableArea(player)) continue;
			
			event.AddPlayer(player);
			event.PrintToPlayer(player);
		}
		
		event.RegisterBukkitEvents();
		event.OnEventStart();
	}
	public void OnTrigger()
	{
		if(isEventActive) return;
		
		isEventActive = true;
		EndEvent[] events = _allEvents.toArray(new EndEvent[_allEvents.size()]);
		
		ImusUtilities.ShuffleArray(events);
		
		for(int i = 0; i < events.length; i++)
		{
			System.out.println("roll");
			if(i >= _totalRolls) break;
			
			EndEvent event = events[i];
			_activeEvents.add(event);
		}
		System.out.println("OnTrigger: "+events);
		
		if(_activeEvents.size() == 0)
		{
			AllTimeEventsHasBeenEnded();
			return;
		}
		OnTriggerEvent(_activeEvents.getFirst());
		
		
		
		
	}
}
package imu.DontLoseItems.CustomEnd;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import imu.DontLoseItems.CustomEnd.EndCustomEvents.EndEvent;
import imu.DontLoseItems.CustomEnd.EndCustomEvents.EndEvent_RandomEntityTypeEnderman;
import imu.DontLoseItems.CustomEnd.EndCustomEvents.EndEvent_RandomPotionEffect;
import imu.iAPI.Utilities.ImusUtilities;



public class UnstableEnd
{
	public static UnstableEnd Instance;
	private double _state = 0;
	private final double MAX_STATE = 1000;
	public BossBar BOSS_BAR;
	private LinkedList<EndEvent> _allEvents;
	public int _totalRolls = 3;
		
	public UnstableEnd()
	{
		Instance = this;
		BOSS_BAR =  Bukkit.createBossBar("Unstable Void", BarColor.PURPLE, BarStyle.SEGMENTED_20, BarFlag.DARKEN_SKY);
		BOSS_BAR.setTitle(ChatColor.BLACK + "| "+ChatColor.DARK_PURPLE+"Unstable Void"+ChatColor.BLACK+" |");
		InitEvents();
	}
	
	public void InitEvents()
	{
		_allEvents = new LinkedList<>();
		_allEvents.add(new EndEvent_RandomEntityTypeEnderman(1));
		_allEvents.add(new EndEvent_RandomPotionEffect(1));
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
		return 0;
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
//rasse plus  plus 4
	public void OnTrigger()
	{
		EndEvent[] events = _allEvents.toArray(new EndEvent[_allEvents.size()]);
		
		for(var e : ImusUtilities.ShuffleArray(events))
		{
			System.out.println("e :"+e.GetName());
		}
		
		
		System.out.println("OnTrigger: "+events);
		SetState(0);
		RefreshPorgress();
	}
}

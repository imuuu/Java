package imu.iWaystone.Upgrades;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Other.Metods;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Enums.UpgradeType;

public abstract class BaseUpgrade 
{
	public UpgradeType _id;
	Tier[] _tiers;
	private int _currentTier = 0;
	protected String[] _description;
	public double _tierReduceValue = 0.0;
	public ItemStack _displayItem;
	
	public BaseUpgrade()
	{
		_id = SetType();
		_tiers = SetTiers();
		_description = SetDescription();
		_tierReduceValue = SetTierReduceValue();
		_displayItem = new ItemStack(SetMaterial());
		Metods.setDisplayName(_displayItem, SetDisplayName());
		//Tooltip();
	}
	
	abstract Tier[] SetTiers();
	
	public abstract String[] SetDescription();
	public abstract double SetTierReduceValue();
	public abstract String SetDisplayName();
	public abstract Material SetMaterial();
	public abstract UpgradeType SetType();
	public abstract double GetCombinedValue(double value);
	public abstract void ButtonPressUpgradeTier(Player player, Waystone ws,int tierBeforeUpgrade);
	
	public void Tooltip()
	{
		ArrayList<String> lores = new ArrayList<>();
		//lores.add("&e"+_description);
		for(String descs : _description)
		{
			lores.add(descs);
		}
		lores.add(" ");
		lores.add("&b===== &6Tiers &b=====");
		for(int i = 0; i < _tiers.length+1; ++i)
		{
			if(i != _currentTier)
			{
				lores.add("&7=Tier &8"+(i+1));
				continue;
			}
			
			lores.add("&e&l=>Tier &6&l"+(i+1));
			lores.add(" ");
			if(i >= GetMaxTier())
			{
				lores.add("&e== &5MAXED OUT! &e==");
				break;
			}
		
			lores.add("&e== &2NEXT TIER COST &e==");
			for(ItemStack stack : _tiers[i]._cost)
			{
				lores.add("&9==> &3"+stack.getType()+ ": &b"+stack.getAmount());
			}
			lores.add(" ");
			lores.add("&4===> &e&k#&7(&bCLICK&7)&e&k# &eto &5UPGRADE!");
			//System.out.println("setting up upgrade");
			break;
			
		}
		
		//System.out.println("lores: "+lores);
		Metods._ins.SetLores(_displayItem, lores.toArray(String[] :: new), false);
	}
	
	public int GetCurrentTier()
	{
		return _currentTier;
	}
	
	public void SetCurrentier(int tierlvl)
	{
		if(tierlvl < 0) tierlvl = 0;
		if(tierlvl > GetMaxTier()) tierlvl = GetMaxTier();
		_currentTier = tierlvl;
	}
	
	public void IncreaseCurrentTier(int amount)
	{
		if(amount < 0) amount = 0;
		if(_currentTier > GetMaxTier()) 
		{
			amount = 0;		
		}
		
		
		_currentTier += amount;

	}
	public boolean IsMaxTier()
	{
		return _tiers.length == _currentTier;
	}
	public int GetMaxTier()
	{
		return _tiers.length;
	}
	
	public ItemStack[] GetCost()
	{
		return _tiers[_currentTier]._cost;
	}
	
}

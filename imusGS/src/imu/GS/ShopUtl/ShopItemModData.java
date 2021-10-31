package imu.GS.ShopUtl;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import imu.GS.ENUMs.ITEM_MOD_DATA;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Tuple;



public class ShopItemModData implements Cloneable
{
	public double _ownPrice = -1;
	public int _maxAmount  = -1;
	public int _fillAmount = -1;
	public int _fillDelayMinutes = -1;
	
	public int _sellTimeStart = -1;
	public int _sellTimeEnd = -1;
	
	public ArrayList<Tuple<Integer,Location>> _locations;
	
	public ArrayList<String> _permissions;
	public ArrayList<String> _worldNames;
	
	public int _roll = 0;
	public String GetValueStr(ITEM_MOD_DATA value, String trueFrontText, String trueBackText,String falseStr)
	{
		String str ="";
		if(trueFrontText == null) trueFrontText = "";
		if(trueBackText == null) trueBackText = "";
		switch (value) 
		{
		case OWN_PRICE:
			str += _ownPrice != -1 ? trueFrontText+_ownPrice +trueBackText: falseStr;
			break;
		case MAX_AMOUNT:
			str += _maxAmount != -1 ? trueFrontText+_maxAmount+trueBackText : falseStr;
			break;
		case FILL_AMOUNT:
			str += _fillAmount != -1 ? trueFrontText+_fillAmount+trueBackText : falseStr;
			break;
		case FILL_DELAY:
			str += _fillDelayMinutes != -1 ? trueFrontText+_fillDelayMinutes +trueBackText: falseStr;
			break;
		case SELL_TIME_START:
			str += _sellTimeStart != -1 ? trueFrontText+_sellTimeStart +trueBackText: falseStr;
			break;
		case SELL_TIME_END:
			str += _sellTimeEnd != -1 ? trueFrontText+_sellTimeEnd+trueBackText : falseStr;
			break;
		case PERMISSIONS:
			str += _permissions != null ? trueFrontText+ImusAPI._metods.CombineArrayToOneString(_permissions.toArray(), "; ")+trueBackText : falseStr;
			break;
		case WORLD_NAMES:
			str += _worldNames != null ? trueFrontText+ImusAPI._metods.CombineArrayToOneString(_worldNames.toArray(), "; ")+trueBackText : falseStr;
			break;
		case DISTANCE_LOC:
			//str += _locations != null ? trueFrontText + _locWorld +" "+_locX+ " "+_locY+" "+_locZ+trueBackText : falseStr;
			String[] strs = null;
			if(_locations != null)
			{
				strs = new String[_locations.size()];
				for(int i = 0; i < strs.length; ++i)
				{
					Location loc =  _locations.get(i).GetValue();
					String locStr = loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
					strs[i] = _locations.get(i).GetKey() + " "+locStr;
				}
			}		
			str += _locations != null ? trueFrontText + ImusAPI._metods.CombineArrayToOneString(strs, "; ")+trueBackText : falseStr;
			break;
		default:
			break;
		}

		return str;
	}
	
	public boolean SetAndCheck(ITEM_MOD_DATA value, String str)
	{

		switch (value) 
		{
		case OWN_PRICE:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_ownPrice = Double.parseDouble(str);
			break;
		case MAX_AMOUNT:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_maxAmount = Integer.parseInt(str);
			break;
		case FILL_AMOUNT:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_fillAmount = Integer.parseInt(str);
			break;
		case FILL_DELAY:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_fillDelayMinutes = Integer.parseInt(str);
			break;
		case DISTANCE_LOC:
			if(!IsLocationValid(str)) return false;
			AddLocation(str);
			break;
		case SELL_TIME_START:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_sellTimeStart = Integer.parseInt(str);
			break;
		case SELL_TIME_END:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_sellTimeEnd = Integer.parseInt(str);
			break;
		case PERMISSIONS:
			AddPermission(str);
			break;
		case WORLD_NAMES:
			AddWorldName(str);				
			break;
		default:
			break;
		}
		return true;
	}
	
	public void AddLocation(int distance, Location loc)
	{
		if(_locations == null) _locations = new ArrayList<>();
		_locations.add(new Tuple<Integer, Location>(distance, loc));
	}
	
	public void AddLocation(String loc_str)
	{
		String[] parts = loc_str.split(" ");
		Location loc = new Location(Bukkit.getWorld(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
		AddLocation(Integer.parseInt(parts[0]), loc);
	}
	boolean IsLocationValid(String loc_str)
	{
		String[] parts = loc_str.split(" ");
		if(parts.length != 5) return false;
		if(!(Bukkit.getWorlds().stream().anyMatch(world -> world.getName().equals(parts[1])))) return false;
		if(!(ImusAPI._metods.isDigit(parts[2])) ||!(ImusAPI._metods.isDigit(parts[3])) || !(ImusAPI._metods.isDigit(parts[3]))) return false;
		
		return true;
	}
	public void ClearLocations()
	{
		_locations = null;
	}
	
	public void AddPermission(String permission)
	{
		if(_permissions == null) _permissions = new ArrayList<>();
		_permissions.add(permission);
	}
	
	public void ClearPermissions()
	{
		_permissions = null;
	}
	
	public void AddWorldName(String worldName)
	{
		if(_worldNames == null) _worldNames = new ArrayList<>();
		_worldNames.add(worldName);
		
	}
	
	public void ClearWorldNames()
	{
		_worldNames = null;
	}
	
	public Object clone()
	{
		try {
			return super.clone();	
		} 
		catch 
		(Exception e) 
		{
		}
		return null;
		
	}
}

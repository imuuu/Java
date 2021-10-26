package imu.GS.ShopUtl;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;

import imu.GS.ENUMs.ITEM_MOD_DATA;
import imu.iAPI.Main.ImusAPI;



public class ShopItemModData implements Cloneable
{
	public double _ownPrice = -1;
	public int _maxAmount  = -1;
	public int _fillAmount = -1;
	public int _fillDelayMinutes = -1;
	public int _distance = -1;
	public int _sellTimeStart = -1;
	public int _sellTimeEnd = -1;
	public ArrayList<String> _permissions;
	public ArrayList<String> _worldNames;
	
	public int _roll = 0;
	public String GetValueStr(ITEM_MOD_DATA value, String falureStr)
	{
		String str ="";
		
		switch (value) 
		{
		case OWN_PRICE:
			str += _ownPrice != -1 ? _ownPrice : falureStr;
			break;
		case MAX_AMOUNT:
			str += _maxAmount != -1 ? _maxAmount : falureStr;
			break;
		case FILL_AMOUNT:
			str += _fillAmount != -1 ? _fillAmount : falureStr;
			break;
		case FILL_DELAY:
			str += _fillDelayMinutes != -1 ? _fillDelayMinutes : falureStr;
			break;
		case DISTANCE:
			str += _distance != -1 ? _distance : falureStr;
			break;
		case SELL_TIME_START:
			str += _sellTimeStart != -1 ? _sellTimeStart : falureStr;
			//break;
		case SELL_TIME_END:
			str += _sellTimeEnd != -1 ? " "+_sellTimeEnd : "";
			break;
		case PERMISSIONS:
			str += _permissions != null ? ImusAPI._metods.CombineArrayToOneString(_permissions.toArray(), "; ") : falureStr;
			break;
		case WORLD_NAMES:
			str += _worldNames != null ? ImusAPI._metods.CombineArrayToOneString(_worldNames.toArray(), "; ") : falureStr;
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
		case DISTANCE:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_distance = Integer.parseInt(str);
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
		}
		return true;
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

package imu.GS.ShopUtl;

import imu.GS.ENUMs.ModDataShop;
import imu.GS.Interfaces.IModData;
import imu.GS.Interfaces.IModDataValues;
import imu.iAPI.Main.ImusAPI;

public class ShopModData implements IModData
{
	public String _name = "";
	public String _displayName = "";
	
	public double _sellMultiplier = -1.0;
	public double _buyMultiplier = -1.0;
	
	public double _expire_percent = -1.0;
	public int _expire_cooldown_minutes = -1;
	
	public boolean _absoluteItemPosition = true;
	
	public boolean _lock = false;
	
	public ShopModData ReadShop(ShopBase shop)
	{
		_name = shop.GetName();
		_displayName = shop.GetDisplayName();
		_sellMultiplier = shop.get_sellM();
		_buyMultiplier = shop.get_buyM();
		_expire_percent = shop.get_expire_percent();
		_expire_cooldown_minutes = shop.get_expire_cooldown_m();
		
		_absoluteItemPosition = shop.IsAbsoluteItemPositions();
		_lock = shop.HasLocked();
		return this;
	}
	
	@Override
	public String GetValueStr(IModDataValues v, String trueFrontText, String trueBackText, String falseStr)
	{ 
		ModDataShop value = (ModDataShop)v;
		String str ="";
		
		switch (value) {
		case BUY_MUL:
			if(_buyMultiplier == -1.0) return falseStr;
			str += _buyMultiplier;
			break;
		case SELL_MUL:
			if(_sellMultiplier == -1.0) return falseStr;
			str += _sellMultiplier;
			break;
		case NAME:
			str+= _name;
			break;
//		case DISPLAYNAME:
//			str += _displayName;
//			break;
		case EXPIRE_PERCENT:
			if(_expire_percent == -1.0) return falseStr;
			str += _expire_percent;
			break;
		case EXPIRE_COOLDOWN:
			if(_expire_cooldown_minutes == -1.0) return falseStr;
			str += _expire_cooldown_minutes;
			break;
		case LOCKED:
			str += _lock;
			break;
		case ABSOLUTE_POS:
			str += _absoluteItemPosition;
			break;
		}
		
		return trueFrontText+str+trueBackText;
	}

	@Override
	public boolean SetAndCheck(IModDataValues v, String str) 
	{
		ModDataShop value = (ModDataShop)v;
		
		switch(value)
		{
		case NAME:
			_name = str; return true;	
		case BUY_MUL:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_buyMultiplier = Double.parseDouble(str);
			break;
		case SELL_MUL:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_sellMultiplier = Double.parseDouble(str);
			break;
		case EXPIRE_COOLDOWN:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_expire_cooldown_minutes = Integer.parseInt(str);
			break;
		case EXPIRE_PERCENT:
			if(!ImusAPI._metods.isDigit(str)) return false;
			_expire_percent = Double.parseDouble(str);
			break;
		case LOCKED:
			if(_lock) 
			{
				_lock = false; 
			}else
			{
				_lock = true;
			}
			return true;
		case ABSOLUTE_POS:
			if(_absoluteItemPosition) 
			{
				_absoluteItemPosition = false; 
			}else
			{
				_absoluteItemPosition = true;
			}
			return true;

		}
		return true;
	}

	
}

package imu.GS.ShopUtl.ShopItems;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import imu.GS.ENUMs.ShopItemType;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemModData;
import imu.iAPI.Other.Cooldowns;

public class ShopItemStockable extends ShopItemSeller
{
	ShopItemModData _modData;
	
	protected Cooldowns _cd;
	public ShopItemStockable(Main main, ShopBase shopBase, ItemStack real, int amount) 
	{
		super(main, shopBase, real, amount);
		_type = ShopItemType.STOCKABLE;
		_modData = new ShopItemModData();
		_modData._maxAmount = amount;
		
	}
		
	public ShopItemModData GetModData()
	{
		return _modData;
	}
	
	@Override
	public void AddAmount(int amount)
	{
		if(_modData._fillAmount > 0)
		{
			super.AddAmount(amount);
			if(Get_amount() > _modData._maxAmount)
				Set_amount(_modData._maxAmount);
			return;
		}
			
		System.out.println("Stocable stack, amount doesnt decrease");
	}
	
	@Override
	public void Set_amount(int _amount) {
		super.Set_amount(_amount);
		_modData._maxAmount = _amount;
	}
	
	@Override
	public JsonObject GetJsonData() 
	{
		JsonObject obj = super.GetJsonData();
		obj.addProperty("maxAmount", _modData._maxAmount);
		obj.addProperty("fillAmount", _modData._fillAmount);
		obj.addProperty("fillDelayMinutes", _modData._fillDelayMinutes);
		return obj;
	}

	@Override
	public void ParseJsonData(JsonObject data) 
	{
		super.ParseJsonData(data);
		SetMaxAmount(data.get("maxAmount").getAsInt());
		SetFillAmount(data.get("fillAmount").getAsInt());
		SetFillDelayMinutes(data.get("fillDelayMinutes").getAsInt());
		
	}
	
	public void Fill()
	{
		AddAmount(_modData._fillAmount);
	}
	
	public void RefreshFillCD()
	{
		if(_cd == null)
			_cd = new Cooldowns();
		
		_cd.setCooldownInSeconds("fill", _modData._fillDelayMinutes * 60);
	}
	
	protected void SetMaxAmount(int amount)
	{
		_modData._maxAmount = amount;
		Set_amount(_modData._maxAmount);
	}
	
	protected void SetFillAmount(int amount)
	{
		_modData._fillAmount = amount;
	}
		
	protected void SetFillDelayMinutes(int minutes)
	{
		_modData._fillDelayMinutes = minutes;
		if(minutes > 0)
		{
			RefreshFillCD();
		}
		else
		{
			_cd = null;
		}
	}
	
	public boolean AbleToFill()
	{
		if(_cd != null && _cd.isCooldownReady("fill"))
			return true;
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

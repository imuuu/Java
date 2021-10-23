package imu.GS.ShopUtl.ShopItems;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import imu.GS.ENUMs.ShopItemType;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.iAPI.Other.Cooldowns;

public class ShopItemStockable extends ShopItemSeller
{
	protected int _maxAmount  = 0;
	protected int _fillAmount = 0;
	protected int _fillDelayMinutes = 0;
	protected Cooldowns _cd;
	public ShopItemStockable(Main main, ShopBase shopBase, ItemStack real, int amount) 
	{
		super(main, shopBase, real, amount);
		_type = ShopItemType.STOCKABLE;
		_maxAmount = amount;
		
	}
		
	@Override
	public void AddAmount(int amount)
	{
		if(_fillAmount > 0)
		{
			super.AddAmount(amount);
			if(Get_amount() > _maxAmount)
				Set_amount(_maxAmount);
			return;
		}
			
		System.out.println("Stocable stack, amount doesnt decrease");
	}
	
	@Override
	public void Set_amount(int _amount) {
		super.Set_amount(_amount);
		_maxAmount = _amount;
	}
	
	@Override
	public JsonObject GetJsonData() 
	{
		JsonObject obj = super.GetJsonData();
		obj.addProperty("maxAmount", _maxAmount);
		obj.addProperty("fillAmount", _fillAmount);
		obj.addProperty("fillDelayMinutes", _fillDelayMinutes);
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
		AddAmount(_fillAmount);
	}
	
	public void RefreshFillCD()
	{
		if(_cd == null)
			_cd = new Cooldowns();
		
		_cd.setCooldownInSeconds("fill", _fillDelayMinutes * 60);
	}
	
	protected void SetMaxAmount(int amount)
	{
		_maxAmount = amount;
		Set_amount(_maxAmount);
	}
	
	protected void SetFillAmount(int amount)
	{
		_fillAmount = amount;
	}
		
	protected void SetFillDelayMinutes(int minutes)
	{
		_fillDelayMinutes = minutes;
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

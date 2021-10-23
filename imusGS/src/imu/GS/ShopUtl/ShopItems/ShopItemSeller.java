package imu.GS.ShopUtl.ShopItems;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ItemPrice.ItemPrice;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;

public class ShopItemSeller extends ShopItemBase
{
	public ShopItemSeller(Main main,ShopBase shopBase,ItemStack real, int amount) {
		super(main,shopBase, real, amount);
	}

	@Override
	protected void SetShowPrice(ItemPrice price) 
	{
		if(price instanceof PriceMoney)
		{
			double p = ((PriceMoney)price).GetPrice();
			((PriceMoney)price).SetShowPrice(p * _shopBase.get_sellM());			
		}
		
	}


	@Override
	public JsonObject GetJsonData() 
	{
		JsonObject obj = new JsonObject();
		return obj;
	}

	@Override
	public void ParseJsonData(JsonObject data) 
	{
		// TODO Auto-generated method stub
		
	}
	
}

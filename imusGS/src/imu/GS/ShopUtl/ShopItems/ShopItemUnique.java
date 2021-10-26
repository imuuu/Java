package imu.GS.ShopUtl.ShopItems;


import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import imu.GS.ENUMs.ShopItemType;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ItemPrice.PriceUnique;

public class ShopItemUnique extends ShopItemStockable
{
	
	protected double _uniquePrice = 100;

	public ShopItemUnique(Main main, ShopBase shopBase, ItemStack real, int amount) 
	{
		super(main, shopBase, real, amount);
		_type = ShopItemType.UNIQUE;
		SetItemPrice(new PriceUnique().SetPrice(_uniquePrice));
	}
	
	
	@Override
	public void SetUUID(UUID uuid) 
	{
		_uuid = uuid;
		_main.get_shopManager().GetUniqueManager().PutPDuuid(GetRealItem(), uuid);
	}
	
	
	@Override
	public void AddAmount(int amount)
	{
		super.AddAmount(amount);
		System.out.println("Unique stack, amount doesnt decrease");
	}
	
	
	@Override
	public JsonObject GetJsonData() 
	{
		JsonObject obj = super.GetJsonData();
		obj.addProperty("uniquePrice", _uniquePrice);
		
		
		return obj;
	}

	@Override
	public void ParseJsonData(JsonObject data) 
	{
		super.ParseJsonData(data);
		((PriceUnique)GetItemPrice()).SetPrice(data.get("uniquePrice").getAsDouble());
		
	}
}

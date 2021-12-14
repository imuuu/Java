package imu.GS.ShopUtl.ShopItems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonObject;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ItemPrice.ItemPrice;
import imu.GS.ShopUtl.ItemPrice.PriceMaterial;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;
import imu.iAPI.Main.ImusAPI;


public class ShopItemSeller extends ShopItemBase
{
	int _shopPage = -1;
	int _slot = -1;
	public ShopItemSeller(Main main, ShopBase shopBase,ItemStack real, int amount) {
		super(main,shopBase, real, amount);

	}
	
	public ShopItemSeller SetPageAndSlot(int page, int slot)
	{
		_shopPage = page;
		_slot = slot;
		ImusAPI._metods.setPersistenData(_display_stack, _main.get_shopManager().pd_page, PersistentDataType.INTEGER, page);
		ImusAPI._metods.setPersistenData(_display_stack, _main.get_shopManager().pd_slot, PersistentDataType.INTEGER, slot);
		return this;
	}
	
	@Override
	public ItemStack GetDisplayItem() 
	{
		super.GetDisplayItem();
		ImusAPI._metods.setPersistenData(_display_stack, _main.get_shopManager().pd_page, PersistentDataType.INTEGER, _shopPage);
		ImusAPI._metods.setPersistenData(_display_stack, _main.get_shopManager().pd_slot, PersistentDataType.INTEGER, _slot);
		return _display_stack;
	}
	
	public int GetPage()
	{
		return _shopPage;
	}
	
	public int GetSlot()
	{
		return _slot;
	}
	
	public boolean CanShowToPlayer(Player player)
	{
		return true;
	}
	
	@Override
	protected void SetShowPrice(ItemPrice price) 
	{

		if(_shopBase == null)
			return;
		
		if(price instanceof PriceMaterial)
		{
			double p = price.GetPrice();
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

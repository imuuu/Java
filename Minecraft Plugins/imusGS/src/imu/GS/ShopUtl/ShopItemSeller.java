package imu.GS.ShopUtl;

import org.bukkit.inventory.ItemStack;

import imu.GS.Main.Main;
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
			((PriceMoney)price).SetShowPrice(p * _shopBase._sellM);			
		}
		
	}
	
}

package imu.GS.ShopUtl.ItemPrice;

import imu.GS.Other.CustomPriceData;


public class PriceCustom extends ItemPrice
{
	

	CustomPriceData[] _items;
	double _customMoney = -1;
	@Override
	public double GetPrice() 
	{
		return 0;
	}

	@Override
	public ItemPrice SetPrice(double price) 
	{
		System.out.println("do not use");
		return null;
	}

	@Override
	public String GetShowPriceOfAmountStr(int amount) 
	{
		return "Custom price";
	}
	
	public ItemPrice SetItemsAndPrice(CustomPriceData[] items, double price)
	{
		_items = items;
		_customMoney = price;
		return this;
	}
	
	public CustomPriceData[] GetItems()
	{
		return _items;
	}

}

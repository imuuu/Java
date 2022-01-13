package imu.GS.ShopUtl.ItemPrice;

import imu.GS.Other.CustomPriceData;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;


public class PriceCustom extends ItemPrice
{
	private CustomPriceData[] _items;
	private double _customMoney = 0;
	private int _minimumStackAmount = 1;
	@Override
	public double GetPrice() 
	{
		return _customMoney;
	}
	
	@Override
	public double GetCustomerPrice() 
	{
		return -1;
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
	
	public ItemPrice SetItemsAndPrice(CustomPriceData[] items, double price, int minimumStackAmount)
	{
		_items = items;
		_customMoney = Metods.Round(price);
		_minimumStackAmount = minimumStackAmount;
		return this;
	}
	
	public CustomPriceData[] GetItems()
	{
		return _items;
	}
	
	public int GetMinimumStackAmount()
	{
		return _minimumStackAmount;
	}
	
	public String GetViewStringOfItems(int buyAmount)
	{
		String str="Money:"+_customMoney;
		for(CustomPriceData data : _items)
		{
			str += "; "+ImusAPI._metods.GetItemDisplayName(data._stack)+ ":"+(data._amount*buyAmount);
		}
		return str;
	}

	
	
	

}
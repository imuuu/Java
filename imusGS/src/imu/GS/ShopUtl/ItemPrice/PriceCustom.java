package imu.GS.ShopUtl.ItemPrice;

import imu.GS.Other.CustomPriceData;


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
		_customMoney = price;
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
	
	

}

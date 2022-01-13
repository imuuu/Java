package imu.GS.ShopUtl.ItemPrice;

import imu.iAPI.Other.Metods;

public class PriceMoney extends ItemPrice
{
	double _price = 0;
	double _showPrice = 0;
	
	@Override
	public String GetShowPriceOfAmountStr(int amount) 
	{
		return String.valueOf((Metods.Round(_showPrice*amount)))+" &2$";
	}
	
	@Override
	public double GetCustomerPrice() 
	{
		return _showPrice;
	}
	
	public ItemPrice SetPrice(double price)
	{
		_price = Metods.Round(price);
		//System.out.println("info:"+info+ "price has been set: "+price);
		return this;
	}
	
	public void SetCustomerPrice(double showPrice)
	{
		_showPrice = Metods.Round(showPrice);
	}
	
	@Override
	public double GetPrice()
	{
		return _price;
	}
}

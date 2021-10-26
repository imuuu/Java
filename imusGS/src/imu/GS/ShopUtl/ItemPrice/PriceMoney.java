package imu.GS.ShopUtl.ItemPrice;

public class PriceMoney extends ItemPrice
{
	double _price = 0;
	double _showPrice = 0;
	@Override
	public String GetShowPriceOfAmountStr(int amount) 
	{
		return String.valueOf((_showPrice*amount));
	}
	
	public ItemPrice SetPrice(double price)
	{
		_price = price;
		//System.out.println("info:"+info+ "price has been set: "+price);
		return this;
	}
	
	public void SetShowPrice(double showPrice)
	{
		_showPrice = showPrice;
	}
	
	@Override
	public double GetPrice()
	{
		return _price;
	}
	
	public double GetShowPrice()
	{
		return _showPrice;
	}

}

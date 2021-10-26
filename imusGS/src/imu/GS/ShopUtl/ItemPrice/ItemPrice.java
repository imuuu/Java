package imu.GS.ShopUtl.ItemPrice;

public abstract class ItemPrice 
{
	public abstract double GetPrice();
	public abstract ItemPrice SetPrice(double price);
	public abstract String GetShowPriceOfAmountStr(int amount);
}

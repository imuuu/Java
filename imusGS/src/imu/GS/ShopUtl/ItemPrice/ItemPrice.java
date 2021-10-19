package imu.GS.ShopUtl.ItemPrice;

public abstract class ItemPrice 
{
	public abstract String GetPrice1Str();
	public abstract String GetPrice8Str();
	public abstract String GetPrice64Str();
	public abstract String GetPriceOfAmountStr(int amount);
}

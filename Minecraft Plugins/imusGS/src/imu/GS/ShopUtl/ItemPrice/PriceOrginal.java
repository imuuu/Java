package imu.GS.ShopUtl.ItemPrice;

public class PriceOrginal extends ItemPrice
{
	int _price = 0;
	
	public int GetPrice()
	{
		return _price;
	}
	
	public void SetPrice(int price)
	{
		_price = price;
	}

	@Override
	public String GetPrice1Str() 
	{
		return String.valueOf(_price);
	}

	@Override
	public String GetPrice8Str() {
		
		return String.valueOf(_price * 8);
	}

	@Override
	public String GetPrice64Str() {
		return String.valueOf(_price * 64);
	}

	@Override
	public String GetPriceOfAmountStr(int amount) 
	{
		return String.valueOf(_price * amount);
	}
}

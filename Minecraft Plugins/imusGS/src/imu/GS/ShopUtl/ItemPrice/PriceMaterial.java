package imu.GS.ShopUtl.ItemPrice;

import imu.GS.Other.MaterialOverflow;
import imu.GS.ShopUtl.ShopItemBase;

public class PriceMaterial extends PriceMoney
{
	
	MaterialOverflow _overflow;
	ShopItemBase _sib;

	double defautlPrice = 1;
	public double GetPrice(int askAmount, int softcap, double batch_size, double dropPercent, double minprice)
	{
		return defautlPrice;
	}
	
	@Override
	public double GetCustomerPrice(int amount) 
	{
		//System.out.println("getting customer price and amount: "+amount);
		if(_overflow == null || _sib == null ) 
		{
			return _showPrice * amount;
		}
		double price = 0;
		int over = 0;
		
		if(_overflow.get_softCap() < (_sib.Get_amount()+amount) ) 
		{
			over = _sib.Get_amount()-_overflow.get_softCap()+amount;
		}

		int amountOver = amount-over;
		
		int overFromCap = (over-amount);
		
		if(overFromCap < 0) overFromCap = 0;
		
		if(amountOver >= 0)
		{
			price += _showPrice * amountOver;
		}
		
		
		int rolls = over ;//(over-lastThings) / (_overflow.Get_batchSize());
		
		if(rolls > amount) rolls = amount;
		
		int left_over = rolls % _overflow.Get_batchSize();
		//System.out.println("left over: "+left_over);
		rolls -= left_over;
		rolls = rolls / _overflow.Get_batchSize();
		
		//overFromCap = Math.round(overFromCap / _overflow.Get_batchSize());
		
		double newPrice = _showPrice * Math.pow(_overflow.get_dropProsent(), overFromCap);

		if(newPrice < _overflow.Get_minPrice()) 
		{
			newPrice = _overflow.Get_minPrice();
		}
				
		int extraLefties = 0;
		for(int i = 0; i < rolls; ++i)
		{			
		
			price += _overflow.Get_batchSize() * newPrice;
				
			newPrice = _showPrice * Math.pow(_overflow.get_dropProsent(), i+overFromCap);

			if(newPrice < _overflow.Get_minPrice()) 
			{
				newPrice = _overflow.Get_minPrice();
				extraLefties += rolls - i -1;
				if(extraLefties < 0 )extraLefties = 0;
				break;
			}
		}

		price += (left_over + extraLefties)* newPrice;

		
		return price;
		
	}
	
	public void SetShopItem(ShopItemBase sib)
	{
		_sib = sib;
	}
	
	public void SetOverflow(MaterialOverflow overflow)
	{
		_overflow = overflow;
	}
	
	public boolean HasOverflow()
	{
		return _overflow != null ? true : false;
	}
	
	
	public boolean HasShopitem()
	{
		return _sib != null ? true : false;
	}
	public MaterialOverflow GetOverflow()
	{
		return _overflow;
	}
	
	@Override
	public ItemPrice clone() 
	{
		PriceMaterial money = new PriceMaterial();
		money.SetPrice(_price);
		money.SetCustomerPrice(_showPrice);
		money.SetOverflow(_overflow);
		money.SetShopItem(_sib);
		return money;
	}
}

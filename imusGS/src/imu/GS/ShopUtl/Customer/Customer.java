package imu.GS.ShopUtl.Customer;

import org.bukkit.entity.Player;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;

public class Customer 
{
	Main _main;
	public Player _player;
	public double _totalBuy = 0;
	public ShopBase _shopBase;
	public CustomerMenuInv _shopInv;
	
	public Customer(Main main, Player player,ShopBase shopBase)
	{
		_main = main;
		_player = player;
		_shopBase = shopBase;
		_shopInv = new CustomerMenuInv(main,player, _shopBase);
	}
	
	public Customer Open()
	{
		_shopInv.openThis();
		return this;
	}
	
	public void Close()
	{
		_player.closeInventory();
	}
	
}

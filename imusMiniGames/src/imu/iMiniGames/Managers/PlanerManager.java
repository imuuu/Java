package imu.iMiniGames.Managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.ItemMetods;

public class PlanerManager 
{
	Main _main;
	ItemMetods _itemM;
	
	String pd_value = "planer_value";
		
	public PlanerManager(Main main) 
	{
		_main = main;
		_itemM = main.get_itemM();
	}
	
	public void setPDvalue(ItemStack stack,String value)
	{
		_itemM.setPersistenData(stack, pd_value, PersistentDataType.STRING, value);
	}
	
	public String getPDvalue(ItemStack stack)
	{
		return _itemM.getPersistenData(stack, pd_value, PersistentDataType.STRING);
	}
	
	public void removePDvalue(ItemStack stack)
	{
		_itemM.removePersistenData(stack, pd_value);
	}
	
}

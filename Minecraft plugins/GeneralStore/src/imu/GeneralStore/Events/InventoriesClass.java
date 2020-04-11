package imu.GeneralStore.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import imu.GeneralStore.main.Main;

public class InventoriesClass implements Listener
{
	Main main = Main.getInstance();
	String invName = "";
	
	
	public InventoriesClass() 
	{
		invName = main.shop1.getName();
	}
	@EventHandler
	public void invOpen(InventoryOpenEvent e)
	{
	
	}
	
	@EventHandler
	public void invClose(InventoryCloseEvent e)
	{
		
	}
	
}

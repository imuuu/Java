package imu.iAPI.Other;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;


public class CustomInvLayout 
{
	protected Plugin _main = null;
	String _name="";
	protected int _size = 0;
	
	protected Inventory _inv = null;
	protected Player _player = null;

	public CustomInvLayout(Plugin main, Player player, String name, int size)
	{
		_main = main;
		_name = name;
		_size = size;
		_player = player;		
		_inv =  _main.getServer().createInventory(null, _size, _name);		
	}
		
	public boolean isThisInv(InventoryEvent e) 
	{
		if(e.getInventory().equals(_inv))
		{
			return true;
		}
		return false;
	}
		
	public void onDisable()
	{
		_player.closeInventory();
	}
	
	public void openThis() 
	{
		_player.openInventory(_inv);		
	}
	
	
	
}

package imu.GeneralStore.Other;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.main.Main;

public class Shop implements Listener
{
	String _name = "";
	
	Main main = Main.getInstance();
	
	String _invContentYAML="";
	
	public Shop(String shopName) 
	{
		_name = shopName;
		_invContentYAML = main.playerInvContentYAML;		
	}
	
	public String getName()
	{
		return _name;
	}
	
	@EventHandler
	public void invOpen(InventoryOpenEvent e)
	{
		System.out.println("Inv OPEN"+ e.getInventory());
		if(e.getPlayer() instanceof Player)
		{		
			Player player = (Player) e.getPlayer();
			Inventory inv = e.getInventory();
			InventoryView view = e.getView();
			if(view.getTitle().equalsIgnoreCase(_name))
			{
				System.out.println("Inv opened: " + _name);
				saveInvTOconfig(player);
			}
			
		}
		
	}
	
	void saveInvTOconfig(Player player)
	{
		ConfigMaker cm = new ConfigMaker(main, _invContentYAML);
		FileConfiguration config = cm.getConfig();
		
		config.set(player.getUniqueId().toString(), player.getInventory().getContents());
		cm.saveConfig();
	}
	
	ItemStack[] getSavedInvFromConfig(Player player)
	{
		ConfigMaker cm = new ConfigMaker(main, _invContentYAML);
		FileConfiguration config = cm.getConfig();
		if(config.contains(player.getUniqueId().toString()))
		{
			System.out.println("Player exist in invs");
			List<ItemStack> stacks = (List<ItemStack>) config.getList(player.getUniqueId().toString());
			ItemStack[] content = new ItemStack[stacks.size()];
			stacks.toArray(content);
			return content;
			//for(ItemStack s : stacks)
			//{
			//	System.out.println("item: "+s);
			//}
			
		}
		return null;
	}
	
	@EventHandler
	public void invClose(InventoryCloseEvent e)
	{
		//System.out.println("Inv CLOSED "+ e.getInventory());
		Inventory inv = e.getInventory();
		InventoryView view = e.getView();
		
		if(e.getPlayer() instanceof Player)
		{
			Player player = (Player) e.getPlayer();
			{
				if(view.getTitle().equalsIgnoreCase(_name))
				{
					System.out.println("it was test");
					//player.getInventory().setContents(main.playerInvContent.get(player));
					
					ItemStack[] content = getSavedInvFromConfig(player);
					player.getInventory().setContents(content);
					
				}
			}
			
		}
		
		


	}
	
}

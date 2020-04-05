package imu.AccountBoundItems.Events;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import imu.AccountBoundItems.Other.ItemABI;
import imu.AccountBoundItems.Other.ItemMetods;

public class DropAndPickup implements Listener
{
	
	ItemABI itemAbi = new ItemABI();
	ItemMetods itemM = new ItemMetods();
	
	int pickUpDelay = 1; // seconds
		
	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		ItemStack stack = e.getItemDrop().getItemStack();

		boolean bound=itemM.hasLore(stack, itemAbi.boundStr);
		boolean broken = itemM.hasLore(stack,itemAbi.brokenStr);
		
		if(bound || broken)
		{
			e.getItemDrop().setPickupDelay(20*pickUpDelay);
			e.getItemDrop().setGlowing(true);
			
		}
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			ItemStack stack = e.getItem().getItemStack();
						
			if(itemM.hasLore(stack, itemAbi.boundStr))
			{
				if(!player.getName().equalsIgnoreCase(itemAbi.getBindersName(stack)) && !player.isOp())
				{
					player.sendMessage(ChatColor.RED + "This item doesn't belong to you!");
					e.getItem().setPickupDelay(20 * (int)(pickUpDelay*0.5));
					e.setCancelled(true);
					return;
				}
								
			}
			
		}
	}
	
	
	
	
	
}

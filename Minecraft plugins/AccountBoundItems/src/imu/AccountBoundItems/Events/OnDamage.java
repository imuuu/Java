package imu.AccountBoundItems.Events;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import imu.AccountBoundItems.Other.ItemABI;
import imu.AccountBoundItems.Other.ItemMetods;

public class OnDamage implements Listener
{
	
	ItemABI itemAbi = new ItemABI();
	ItemMetods itemM = new ItemMetods();
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e)
	{
		
		if(e.getDamager() instanceof Player)
		{
			if(checkItemsForBroken((Player)e.getDamager()))
			{
				e.setDamage(0);
			}
		}
		

		if(e.getEntity() instanceof Player)
		{
			checkItemsForBroken((Player)e.getEntity());
		}
		
	}
		
	boolean checkItemsForBroken(Player player)
	{
		boolean weaponHasBroken = false;
		PlayerInventory inv = player.getInventory();
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(inv.getItemInMainHand());
		stacks.add(inv.getItemInOffHand());
		stacks.addAll(Arrays.asList(inv.getArmorContents()));
		
		for(int i= 0 ; i < stacks.size(); ++i)
		{
			ItemStack stack = stacks.get(i);
			if(itemM.hasLore(stack, itemAbi.brokenStr))
			{
				if(i == 0) // main
				{
					player.sendMessage(ChatColor.RED + "There is no use for broken item, go fix it!");
					weaponHasBroken = true;
					continue;
				}
				
				itemAbi.dropItem(stack, player);
			}
		}
		return weaponHasBroken;
	}
	
	
	
	
	
	
}

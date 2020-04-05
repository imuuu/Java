package imu.AccountBoundItems.Other;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import imu.AccountBoundItems.main.Main;

public class ItemABI 
{
	ItemMetods itemM = new ItemMetods();
	
	public String boundStr;
	public String brokenStr;
	
	Main main = Main.getInstance();
	
	public ItemABI() 
	{
		boundStr = main.loreNames.get("bound");
		brokenStr = main.loreNames.get("broken");
	}
	
	public void setBind(ItemStack stack, Player player, boolean forceIt)
    {

    	if(!itemM.hasLore(stack, boundStr))
    	{ 		
    		player.sendMessage("You have bind the item");
    		itemM.addLore(stack, boundStr+ChatColor.AQUA+player.getName(), false);
    	}
    	else
    	{
    		if(forceIt)
    		{
    			itemM.removeLore(stack, boundStr);
    			setBind(stack, player, false);
    		}
    		else 
    		{
    			player.sendMessage("You have bind on item already");
    		}
    	  		
    	}
    }
		
	public void setBroken(ItemStack stack, boolean forceIt)
    {
    	if(!itemM.hasLore(stack, brokenStr))
    	{
    		//player.sendMessage(ChatColor.RED + "Your item: "+stack.getType().toString()+ " has broken!");
    		itemM.addLore(stack, brokenStr, false);
    	}else
    	{
    		if(forceIt)
    		{
    			itemM.removeLore(stack, brokenStr);
    			setBroken(stack, false);
    		}else
    		{
    			System.out.println("already contains lore");
    		}
    		
    	}
    		
    }
	
	public void repairAll(ItemStack[] stacks)
	{
		for(ItemStack stack : stacks)
		{
			repair(stack);
		}
	}
	
	public void repair(ItemStack stack)
	{
		if(itemM.hasLore(stack, brokenStr))
		{
			itemM.removeLore(stack, brokenStr);
		}
	}
	
	public void unBind(ItemStack stack)
	{
		if(itemM.hasLore(stack, boundStr))
		{
			itemM.removeLore(stack, boundStr);
		}
	}
	
	/**
	 * 
	 * returns name who bound the item as string
	 * @return
	 */
	public String getBindersName(ItemStack stack)
	{
		String name="";
		int idx = itemM.findLoreIndex(stack, boundStr);
		if(idx > -1)
		{
			String lore = stack.getItemMeta().getLore().get(idx);
			String str = lore.split(" ")[1];
			name = str.substring(2, str.length());
		}
		return name;
	}
	
	public int getFirstEmpty(ItemStack[] itemStacks)
	{
		for(int i = itemStacks.length-6; i > 8; --i) // armors lots and shield = -6, hotbar = 8
		{
			if(itemStacks[i] == null)
			{
				return i;
			}			
		}
		return -1;
	}
	
	public void dropItem(ItemStack stack, Player player)
	{
		ItemStack copy = new ItemStack(stack);
		stack.setAmount(0);
		
		player.sendMessage(ChatColor.RED + "You have dropped your: "+ ChatColor.AQUA +copy.getType().toString());
		Item dropped = player.getWorld().dropItemNaturally(player.getLocation(), copy);
		PlayerDropItemEvent event = new PlayerDropItemEvent(player, dropped);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	public void moveItemFirstFreeSpaceInv(ItemStack stack, Player player, boolean includeHotbar)
	{
		if(stack == null || stack.getType() == Material.AIR)
		{
			return;
		}
		ItemStack copy = new ItemStack(stack);
		stack.setAmount(0);
		
		PlayerInventory inv = player.getInventory();
		
		int invSlot;
		if(includeHotbar)
		{
			invSlot = inv.firstEmpty();
		}else
		{
			invSlot = getFirstEmpty(inv.getContents());
		}

		if( invSlot != -1)
		{
			player.sendMessage(ChatColor.RED + "You can't use broken: "+ ChatColor.AQUA + copy.getType().toString());
			inv.setItem(invSlot, copy);
		}else
		{
			player.sendMessage(ChatColor.RED + "You don't have space!");
			dropItem(copy,player);
			
			
		}
		
	}
}

package imu.AccountBoundItems.Other;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemABI extends ItemPersistentDataMethods
{
	
	public String boundStr;
	public String brokenStr;
		
	public ItemABI() 
	{
		boundStr = main.loreNames.get("bound");
		brokenStr = main.loreNames.get("broken");
	}
	
	/**
	 * 
	 * adds item bound lore, tag:bound,uuid
	 * @return
	 */
	
	public boolean setBind(ItemStack stack, Player player, boolean forceIt)
    {
		if(hasDurability(stack))
		{
			if(!hasLore(stack, boundStr))
	    	{ 		
	    		player.sendMessage("You have bind the item");
	    		addLore(stack, boundStr+ChatColor.AQUA+player.getName(), false);
	    		
	    		setNameData(stack, player.getName());
	    		setUUIDData(stack,  player.getUniqueId().toString());
	    		setWaitData(stack, 0);
	    		setOnUseWaitData(stack, 0);
	    		setBoundData(stack, 1);
	    		return true;
	    	}
	    	else
	    	{
	    		if(forceIt)
	    		{
	    			removeLore(stack, boundStr);
	    			setBind(stack, player, false);
	    		}
	    		else 
	    		{
	    			player.sendMessage("You have bind on item already");
	    		}
	    	  		
	    	}
		}else
		{
			player.sendMessage(ChatColor.RED + "You can't bind that item");			
		}
		return false;
    	
    }
	
	/**
	 * 
	 * adds item bound lore, tag:bound,price,uuid
	 * @return
	 */
	
	public boolean setBind(ItemStack stack, Player player, double price, boolean forceIt,boolean overridePrice)
    {
		if(hasDurability(stack))
		{
			if(!hasLore(stack, boundStr))
	    	{ 		
	    		player.sendMessage("You have bind the item");
	    		addLore(stack, boundStr+ChatColor.AQUA+player.getName(), false);
	    		
	    		setNameData(stack, player.getName());
	    		setUUIDData(stack,  player.getUniqueId().toString());
	    		setPriceData(stack, price,overridePrice);
	    		setWaitData(stack, 0);
	    		setOnUseWaitData(stack, 0);
	    		setBoundData(stack, 1);
	    		return true;
	    		
	    	}
	    	else
	    	{
	    		if(forceIt)
	    		{
	    			removeLore(stack, boundStr);
	    			setBind(stack, player, false);
	    		}
	    		else 
	    		{
	    			player.sendMessage("You have bind on item already");
	    		}
	    	  		
	    	}
		}
    	
    	return false;
    }
	
	public boolean setWait(ItemStack stack)
	{
		if(hasDurability(stack))
		{
			if(!hasLore(stack, boundStr))
	    	{ 		
	    		addLore(stack, boundStr+ChatColor.MAGIC+"WAITING BIND", false);
	    		
	    		setWaitData(stack, 1);
	    		return true;
	    		
	    	}
	    	else
	    	{	    		
				removeLore(stack, boundStr);
				setWait(stack);

	    	}
	    	
		}
		return false;
	}
	
	public boolean setOnUseWait(ItemStack stack)
	{
		if(hasDurability(stack))
		{
			if(!hasLore(stack, boundStr))
	    	{ 		
	    		addLore(stack, boundStr+ChatColor.DARK_PURPLE+"BIND ON USE", false);
	    		
	    		setOnUseWaitData(stack, 1);
	    		return true;
	    		
	    	}
	    	else
	    	{	    		
				removeLore(stack, boundStr);
				setOnUseWait(stack);

	    	}
	    	
		}
		return false;
	}
	
	/**
	 * 
	 * adds item broken lore and tag
	 * @return
	 */
	public void setBroken(ItemStack stack, boolean forceIt)
    {
    	if(!hasLore(stack, brokenStr))
    	{
    		//player.sendMessage(ChatColor.RED + "Your item: "+stack.getType().toString()+ " has broken!");
    		//addLore(stack, brokenStr, false);
    		removeName(stack, brokenStr);
    		addName(stack, brokenStr, true);
    		setBrokenData(stack, 1);
    		
    	}else
    	{
    		if(forceIt)
    		{
    			//removeLore(stack, brokenStr);
    			removeName(stack, brokenStr);
    			setBroken(stack, false);
    		}else
    		{
    			System.out.println("already contains lore");
    		}
    		
    	}
    		
    }
		
	public boolean isBound(ItemStack stack)
	{
		Integer value = getBoundData(stack);
		if(value != null)
		{
			if(value > 0)
			{
				return true;
			}
		}
		
		return false;
	}
	public boolean isBroken(ItemStack stack)
	{
		Integer value = getBrokenData(stack);
		if(value != null)
		{
			if(value > 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isWaiting(ItemStack stack)
	{
		Integer value = getWaitData(stack);
		if(value != null)
		{
			if(value > 0)
			{
				System.out.println("its waiting");
				return true;
			}
		}
		
		return false;
	}
	
	
	public boolean isOnUse(ItemStack stack)
	{
		Integer value = getOnUseWaitData(stack);
		if(value != null)
		{
			if(value > 0)
			{
				System.out.println("its use");
				return true;
			}
		}
		
		return false;
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
		if(isBroken(stack))
		{
			//removeLore(stack, brokenStr);
			removeName(stack, brokenStr);
			setBrokenData(stack, 0);
		}
	}
	
	public void unBind(ItemStack stack)
	{
		if(hasLore(stack, boundStr))
		{
			removeLore(stack, boundStr);
			setNameData(stack, "");
    		setUUIDData(stack, "");
    		setBoundData(stack, 0);
    		setWaitData(stack, 0);
    		setOnUseWaitData(stack, 0);
		}
	}
	
		

	/**
	 * 
	 * returns name who bound the item as string
	 * @return
	 */	
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
	
	/**
	 * 
	 * Drops item to ground where player is gives text and adds it to dropevent
	 * @return
	 */
	public void dropItem(ItemStack stack, Player player)
	{
		ItemStack copy = new ItemStack(stack);
		stack.setAmount(0);
		
		player.sendMessage(ChatColor.RED + "You have dropped your: "+ ChatColor.AQUA +copy.getType().toString());
		Item dropped = player.getWorld().dropItemNaturally(player.getLocation(), copy);
		PlayerDropItemEvent event = new PlayerDropItemEvent(player, dropped);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	/**
	 * 
	 * Put item to player inventory if there is free space.. if not drop it on ground.. can include hotbar
	 * @return
	 */
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

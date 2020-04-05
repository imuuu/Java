package imu.AccountBoundItems.Other;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemMetods 
{
	
	public ItemStack addLore(ItemStack stack, String lore,boolean addLast)
	{

    	if(stack != null && stack.getType() != Material.AIR)
    	{
			ItemMeta meta = stack.getItemMeta();
    		ArrayList<String> lores = new ArrayList<String>();
    		if(meta.hasLore())
    		{
    			lores.addAll(meta.getLore());
    		}
    		
    		if(addLast)
    		{
    			lores.add(lore);
    		}else
    		{
    			lores.add(0, lore);
    		}
    		
    		meta.setLore(lores);
    		stack.setItemMeta(meta);

			
    	}
    	return stack;
	}
	
	
	public ItemStack removeLore(ItemStack stack, String lore)
	{
		int idx = findLoreIndex(stack, lore);
		if(idx > -1)
		{
			ItemMeta meta = stack.getItemMeta();
			ArrayList<String> lores = new ArrayList<String>();
			lores.addAll(meta.getLore());
			lores.remove(idx);
			meta.setLore(lores);
			stack.setItemMeta(meta);
			
			
		}
		return stack;
	}
	public int findLoreIndex(ItemStack stack, String lore)
	{
		if(stack != null)
    	{
			if(stack.hasItemMeta())
			{
				ItemMeta meta = stack.getItemMeta();  		
	    		if(meta.hasLore())
	    		{
	    			ArrayList<String> lores = new ArrayList<String>();
	    			lores.addAll(meta.getLore());
	    			
	    			for(int i = 0 ; i < lores.size() ; ++i)
	    			{  				
	    				if(lores.get(i).contains(lore))
	    				{
	    					return i;
	    				}
	    			}
	    		}
			}
			
    	}
		return -1;
	}
	public boolean hasLore(ItemStack stack, String lore)
	{
		if(stack != null)
		{
			if(stack.hasItemMeta())
			{
				if(findLoreIndex(stack, lore) > -1)
				{
					return true;
				}
			}
		}
				
		return false;
	}
	
	void printItemStacks(ItemStack[] stacks)
	{
		System.out.println("===========================");
		for (ItemStack itemStack : stacks)
		{
			if(itemStack != null)
			{
				System.out.println("Item: " + itemStack + "Material:" +itemStack.getType());
			}else
			{
				System.out.println("Item: " + itemStack);
			}
				
		}
	}
	
	public boolean isInArmor(ItemStack stack,Player player)
	{
		System.out.println("===ARMOR===: " + stack);
		if(stack != null && stack.getType() != Material.AIR)
		{
			for(ItemStack s : player.getInventory().getArmorContents())
			{
				if(s == null)
					continue;
				
				if(s.isSimilar(stack))
				{
					
					System.out.println("found match");
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isInShield(ItemStack stack, Player player)
	{
		System.out.println("===SHield!===: " + stack);
		if(stack != null && stack.getType() != Material.AIR)
		{
			ItemStack offHand = player.getInventory().getItemInOffHand();
			if(offHand != null)
			{
				if(offHand.isSimilar(stack))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}

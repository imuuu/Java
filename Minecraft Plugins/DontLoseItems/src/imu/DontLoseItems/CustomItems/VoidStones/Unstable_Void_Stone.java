package imu.DontLoseItems.CustomItems.VoidStones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.DontLoseItems.CustomItems.CustomItem;
import imu.DontLoseItems.Enums.VOID_STONE_TIER;
import imu.DontLoseItems.Managers.Manager_VoidStones;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ImusUtilities;

public class Unstable_Void_Stone extends CustomItem
{
	private static final String PD_VOID_STONE = "UNSTABLE_VOID_STONE";
	public Unstable_Void_Stone()
	{
		super(new ItemStack(Material.STONE), "&5UNSTABLE &0VOID &7STONE");		
	}
	
	
	@Override
	public ItemStack GetItemStack()
	{
		ItemStack stack = Stack.clone();
		Metods.setDisplayName(stack, "&5UNSTABLE &0VOID &7STONE");

		Metods._ins.setPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER, 0);
		return stack;
	}
	
	@SuppressWarnings("incomplete-switch")
	public ItemStack GetVoidStoneWithTier(VOID_STONE_TIER tier)
	{
		switch (tier)
		{
		case NORMAL: return GetNormal();
		case RARE: return GetRare();
		}
		return GetItemStack();
	}
	
	public static int GetTierInceaseToEnchant(VOID_STONE_TIER tier)
	{
		switch (tier)
		{
		case NONE: 		return 0;
		case NORMAL: 	return 1;
		case RARE: 		return 2;
		}
		return 0;
	}
	
	private ItemStack SetBaseLore(ItemStack stack, int power)
	{
		List<String> lores = new ArrayList<>();
		
		lores.add("");
		lores.add("&9This unstable stone is able to enchant two random enchants");
		lores.add("&9with either a &2+"+power+" &9 or &4-"+power+"&9 level.");
		lores.add("");
		lores.add("&9Combine it in a Smithing Table");
		lores.add("&9with an enchanted item to apply the effect");
		lores.add("");
		lores.add("&9Item needs to have at least 3 enchants");
		lores.add("");
		lores.add("&9After use the item will be");
		lores.add("&9unenchantable");
		Metods._ins.addLore(stack, lores);
		return stack;

	}
	private ItemStack GetNormal()
	{
		ItemStack stack = GetItemStack();
		stack = SetBaseLore(stack, 1);
		Metods._ins.AddGlow(stack);
		Metods._ins.setPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER, 0);
		return stack;
	}
	
	private ItemStack GetRare()
	{
		ItemStack stack =  GetItemStack();

		stack = SetBaseLore(stack, 2);
		Metods._ins.AddGlow(stack);
		Metods._ins.setPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER, 1);
		return stack;
	}
	
	public static boolean IsVoidStone(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER) != null;
	}
	
	public static VOID_STONE_TIER GetVoidStoneTier(ItemStack stack)
	{
		Integer i = Metods._ins.getPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER);
		if( i == null) return VOID_STONE_TIER.NONE;
		
		return VOID_STONE_TIER.GetFromIndex(i);
		
	}
	
	public static ItemStack UseItem(ItemStack stack, VOID_STONE_TIER tier)
	{
		if(stack.getEnchantments().size() < 3)
		{
			System.out.println("not enough enchants");
			return stack;
		}
		
		boolean positive = ThreadLocalRandom.current().nextInt(100) < 50;
		HashMap<Enchantment, Integer> data = Metods._ins.GetEnchantsWithLevels(stack);
		
		Enchantment[] ench = new Enchantment[data.size()];
		
		int index = 0;
		for(var da : data.keySet())
		{
			ench[index++] = da;
		}
		ench = ImusUtilities.ShuffleArray(ench);
		Enchantment ench1 = ench[0];
		Enchantment ench2 = ench[1];
		stack.removeEnchantment(ench1);
		stack.removeEnchantment(ench2);
		
		if(positive)
		{
			int value = data.get(ench1)+GetTierInceaseToEnchant(tier);
			if(Manager_VoidStones.MAX_ENCH_LEVEL.containsKey(ench1)) value = Manager_VoidStones.MAX_ENCH_LEVEL.get(ench1);
			
			stack.addUnsafeEnchantment(ench1, value);
			
			value = data.get(ench2)+GetTierInceaseToEnchant(tier);
			if(Manager_VoidStones.MAX_ENCH_LEVEL.containsKey(ench2)) value = Manager_VoidStones.MAX_ENCH_LEVEL.get(ench2);
			
			stack.addUnsafeEnchantment(ench2, value);
		}
		else
		{
			int value = data.get(ench1)-GetTierInceaseToEnchant(tier);
			if(value >= 1) stack.addEnchantment(ench1, value);
			
			value = data.get(ench2)-GetTierInceaseToEnchant(tier);
			if(value >= 1) stack.addEnchantment(ench2, value);
		}
		
		return stack;
	}
	
	

	

}

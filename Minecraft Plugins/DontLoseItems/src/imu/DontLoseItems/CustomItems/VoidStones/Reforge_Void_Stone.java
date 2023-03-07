package imu.DontLoseItems.CustomItems.VoidStones;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.DontLoseItems.Enums.VOID_STONE_TIER;
import imu.DontLoseItems.Enums.VOID_STONE_TYPE;
import imu.DontLoseItems.Managers.Manager_HellArmor;
import imu.DontLoseItems.Managers.Manager_HellTools;
import imu.iAPI.Other.Metods;

public class Reforge_Void_Stone extends Void_Stone
{
	
	public Reforge_Void_Stone()
	{
		super("&5REFORGE &0VOID &7STONE", VOID_STONE_TYPE.REFORCE, VOID_STONE_TIER.NORMAL);		
	}
	
	private ItemStack SetBaseLore(ItemStack stack, int power)
	{
		List<String> lores = new ArrayList<>();
		
		lores.add("");
		lores.add("&9This unstable stone is reforge rarity item");
		lores.add("&9It might completly destory the item or");
		lores.add("&9decrease or increase rarity by one");
		lores.add("");
		lores.add("&9Combine it in a Smithing Table");
		lores.add("&9with a rarity item to apply the effect");
		lores.add("");

		Metods._ins.addLore(stack, lores);
		return stack;

	}
	
	private enum Roll_Type
	{
		DESTROY,
		DECREASE,
		INCREASE,
	}
	
	private Roll_Type Roll()
	{
		int value = ThreadLocalRandom.current().nextInt(100);
		
		if(value < 33) return Roll_Type.DESTROY;
		if(value < 66) return Roll_Type.DECREASE;
		return Roll_Type.INCREASE;
	}
	@Override
	public ItemStack UseItem(ItemStack stack,VOID_STONE_TIER tier)
	{
		Roll_Type roll = Roll();
		
		if(roll == Roll_Type.DESTROY) return new ItemStack(Material.AIR);
		ItemStack newStack = null;
		if(Manager_HellArmor.Instance.IsHellArmor(stack))
		{
			newStack =Manager_HellArmor.Instance.IncreaseTier(stack, roll == Roll_Type.INCREASE ? 1 : -1);
		}
		if(Manager_HellTools.Instance.IsHellTool(stack))
		{
			newStack = Manager_HellTools.Instance.IncreaseTier(stack, roll == Roll_Type.INCREASE ? 1 : -1);
		}
		
		
		return newStack;
	}

	@Override
	public ItemStack GetVoidStoneWithTier(VOID_STONE_TIER tier)
	{
		ItemStack stack = GetItemStack();
		stack = SetBaseLore(stack, 1);
		
		return stack;
	}
	
	
	
	
	

	

}

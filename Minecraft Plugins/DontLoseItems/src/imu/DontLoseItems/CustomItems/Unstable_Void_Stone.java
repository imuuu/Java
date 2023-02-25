package imu.DontLoseItems.CustomItems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.iAPI.Other.Metods;

public class Unstable_Void_Stone extends CustomItem
{
	private static final String PD_VOID_STONE = "UNSTABLE_VOID_STONE";
	public Unstable_Void_Stone()
	{
		super(new ItemStack(Material.STONE), "&5UNSTABLE &0VOID &7STONE");		
	}
	
	public enum VOID_STONE_TIER
	{
		NORMAL,
		RARE,
	}
	@Override
	public ItemStack GetItemStack()
	{
		ItemStack stack = Stack.clone();
		Metods.setDisplayName(stack, "&5UNSTABLE &0VOID &7STONE");

		Metods._ins.setPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER, 0);
		return stack;
	}
	
	public ItemStack GetVoidStoneWithTier(VOID_STONE_TIER tier)
	{
		switch (tier)
		{
		case NORMAL: return GetNormal();
		case RARE: return GetRare();
		}
		return GetItemStack();
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
		lores.add("&9After use the item will be");
		lores.add("&9unenchantable");
		Metods._ins.addLore(stack, lores);
		return stack;

	}
	private ItemStack GetNormal()
	{
		ItemStack stack = GetItemStack();
		Metods._ins.setPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER, 0);
		
		stack = SetBaseLore(stack, 1);
		
		
		Metods._ins.AddGlow(stack);
		return stack;
	}
	
	private ItemStack GetRare()
	{
		ItemStack stack =  GetItemStack();
		stack = Metods._ins.setPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER, 1);
		stack = SetBaseLore(stack, 2);
		Metods._ins.AddGlow(stack);
		return stack;
	}
	
	public static boolean IsVoidStone(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, PD_VOID_STONE, PersistentDataType.INTEGER) != null;
	}
	
	

	

}

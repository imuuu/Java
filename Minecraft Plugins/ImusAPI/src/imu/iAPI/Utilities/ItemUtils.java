package imu.iAPI.Utilities;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;

public class ItemUtils
{
	private static ImusAPI _main = ImusAPI._instance;
	
	public static boolean IsValid(ItemStack stack) 
	{ 
		return stack != null && stack.getType() != Material.AIR;
	}
	
	public static boolean IsArmor(ItemStack stack) 
	{   
		if (!IsValid(stack))  return false;

	    switch (stack.getType()) 
	    {
	        case LEATHER_HELMET:
	        case LEATHER_CHESTPLATE:
	        case LEATHER_LEGGINGS:
	        case LEATHER_BOOTS:
	        case IRON_HELMET:
	        case IRON_CHESTPLATE:
	        case IRON_LEGGINGS:
	        case IRON_BOOTS:
	        case GOLDEN_HELMET:
	        case GOLDEN_CHESTPLATE:
	        case GOLDEN_LEGGINGS:
	        case GOLDEN_BOOTS:
	        case DIAMOND_HELMET:
	        case DIAMOND_CHESTPLATE:
	        case DIAMOND_LEGGINGS:
	        case DIAMOND_BOOTS:
	        case NETHERITE_HELMET:
	        case NETHERITE_CHESTPLATE:
	        case NETHERITE_LEGGINGS:
	        case NETHERITE_BOOTS:
	        case TURTLE_HELMET:
	        case ELYTRA:
	            return true;
	        default:
	            return false;
	    }
	}

	public boolean IsShulkerBox(ItemStack stack)
	{
		if (!IsValid(stack))  return false;

		if(stack.getItemMeta() instanceof BlockStateMeta )
		{
			BlockStateMeta im = (BlockStateMeta)stack.getItemMeta();
            if(im.getBlockState() instanceof ShulkerBox){
            	return true;
            }
			
		}
		return false;
	}

	public static boolean IsTool(ItemStack stack) 
	{
		if (!IsValid(stack))  return false;
		
		switch(stack.getType()) 
		{
			case WOODEN_PICKAXE: return true;
			case WOODEN_SHOVEL: return true;
			case WOODEN_AXE: return true;
			case WOODEN_HOE: return true;
			case WOODEN_SWORD: return true;
			
			case STONE_PICKAXE: return true;
			case STONE_SHOVEL: return true;
			case STONE_AXE: return true;
			case STONE_HOE: return true;
			case STONE_SWORD: return true;
			
			case IRON_PICKAXE: return true;
			case IRON_SHOVEL: return true;
			case IRON_AXE: return true;
			case IRON_HOE: return true;
			case IRON_SWORD: return true;
			
			case GOLDEN_PICKAXE: return true;
			case GOLDEN_SHOVEL: return true;
			case GOLDEN_AXE: return true;
			case GOLDEN_HOE: return true;
			case GOLDEN_SWORD: return true;
			
			case DIAMOND_PICKAXE: return true;
			case DIAMOND_SHOVEL: return true;
			case DIAMOND_AXE: return true;
			case DIAMOND_HOE: return true;
			case DIAMOND_SWORD: return true;
			
			case NETHERITE_PICKAXE: return true;
			case NETHERITE_SHOVEL: return true;
			case NETHERITE_AXE: return true;
			case NETHERITE_SWORD: return true;
			
			case SHIELD: return true;
			
			case BOW: return true;
			case CROSSBOW: return true;
			
			case TRIDENT: return true;
			
			case FISHING_ROD: return true;
			
			default: return false;
		}
		
	}
	
	public static  ItemStack AddLore(ItemStack stack, String lore, boolean addLast)
	{
		if (!IsValid(stack))  return stack;
		
		lore = Metods.msgC(lore);
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
		
    	return stack;
	}
	
	public static  ItemStack RemoveLores(ItemStack stack)
	{
		if (!IsValid(stack))  return stack;
		
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(new ArrayList<>());
		stack.setItemMeta(meta);
		
    	return stack;
	}
	
	public static  ItemStack AddLore(ItemStack stack, Iterable<String> lores)
	{
		if (!IsValid(stack))  return stack;
		
		ItemMeta meta = stack.getItemMeta();
		ArrayList<String> read_lores = new ArrayList<String>();
		if(meta.hasLore())
		{
			read_lores.addAll(meta.getLore());
		}
		
		int idx = 0;
		for(String l : lores)
		{
			read_lores.add(idx++, Metods.msgC(l));
		}
		
		meta.setLore(read_lores);
		stack.setItemMeta(meta);
    	
		return stack;
	}
	public static  ItemStack SetLores(ItemStack stack, String[] lores, boolean removeEmpty)
	{
		return SetLores(stack, Arrays.asList(lores), removeEmpty);
	}
	
	public static ItemStack SetLores(ItemStack stack, Iterable<String> lores, boolean removeEmpty)
	{
		if (!IsValid(stack))  return stack;
		
		ItemMeta meta = stack.getItemMeta();
		ArrayList<String> metaLores = new ArrayList<>();

		for(String lore : lores)
		{
			metaLores.add(Metods.msgC(lore));
		}
		if(removeEmpty)
		{
			//System.out.println("Removing empty");
			for(int i = metaLores.size()-1; i >= 0 ; i--)
			{
				//System.out.println("==> empty? "+metaLores.get(i)+ "is it? "+Strings.isNullOrEmpty(metaLores.get(i)));
				if(Metods.IsStringNullOrEmpty(metaLores.get(i)))
				{
					metaLores.remove(i);
				}
			}
		}
		
		meta.setLore(metaLores);
		
		stack.setItemMeta(meta);
    	
		return stack;
	}
	
	public static ItemStack ReSetLore(ItemStack stack, String lore, int index)
	{

		ItemMeta meta = stack.getItemMeta();
		if(!meta.hasLore())
		{
			meta.setLore(new ArrayList<>());			
		}
		
		ArrayList<String> metaLores = (ArrayList<String>)meta.getLore();
		if(metaLores.size() <= index)
		{
			while(metaLores.size() < index+1)
			{
				metaLores.add("");
			}
		}
		metaLores.set(index, lore);
		meta.setLore(metaLores);
		
		stack.setItemMeta(meta);

		return stack;
	}
	
	public static String GetDisplayName(ItemStack stack)
	{
		if (!IsValid(stack))  return "";
		
		ItemMeta meta = stack.getItemMeta();
		String dName="";
		if(meta.hasDisplayName())
		{
			dName = meta.getDisplayName();
		}
		else
		{
			String[] mNames = stack.getType().toString().split("_");
			for(String subName : mNames)
			{
				String sub = subName.substring(0,1).toUpperCase() + subName.substring(1).toLowerCase();
				if(dName == "")
				{
					dName = dName +sub;
				}else
				{
					dName = dName +" "+sub;
				}
				
			}
		}
		return dName;
	}
	public static ItemStack RemoveDisplayName(ItemStack stack, String name)
	{
		if (!IsValid(stack))  return stack;
		
		ItemMeta meta = stack.getItemMeta();
		String dName = GetDisplayName(stack);
		dName = dName.replace(name, "");
		meta.setDisplayName(dName);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack SetDisplayName(ItemStack stack, String name)
	{
		if (!IsValid(stack))  return stack;
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Metods.msgC(name));
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack SetDisplayNameEmpty(ItemStack stack)
	{
		return SetDisplayName(stack, " ");
	}
	
	public static ItemStack RemovePersistenData(ItemStack stack, String keyName)
	{
		if (!IsValid(stack))  return stack;

		NamespacedKey key = new NamespacedKey(_main, keyName);
		ItemMeta meta = stack.getItemMeta();
		meta.getPersistentDataContainer().remove(key);
		stack.setItemMeta(meta);

		return stack;
	}
	
	public static <T> ItemStack SetPersistenData(ItemStack stack, String keyName, PersistentDataType<T, T> type, T data)
	{
		if (!IsValid(stack))  return stack;
		
		NamespacedKey key = new NamespacedKey(_main, keyName);
		ItemMeta meta = stack.getItemMeta();
		meta.getPersistentDataContainer().set(key, type, data);
		stack.setItemMeta(meta);
		return stack;
		
	}
	
	public static <T> T GetPersistenData(ItemStack stack, String keyName, PersistentDataType<T, T> type)
	{
		T value = null;
		if (!IsValid(stack))  return value;
		
		NamespacedKey key = new NamespacedKey(_main, keyName);
		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer container = meta.getPersistentDataContainer();
		if(container.has(key, type))
		{
			value = container.get(key, type);
			return value;
		}
		
		return value;
	}
	
	
}

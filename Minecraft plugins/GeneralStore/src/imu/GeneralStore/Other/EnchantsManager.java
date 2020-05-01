package imu.GeneralStore.Other;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.GeneralStore.main.Main;

public class EnchantsManager 
{
	Main _main = null;
	ItemMetods _itemM = null;
	HashMap<Enchantment, Double[]> enchPrices = new HashMap<>();  // {minlvl,maxlvl,minPrice,maxPrice
	ArrayList<ItemStack> ench_items = new ArrayList<>();
	
	Material ench_item_mat = Material.ENCHANTED_BOOK;
	
	public String pd_ench = "gs.emEnc";
	public String pd_modify = "gs.emModify";
	public String pd_name = "gs.emEncName";
	
	public EnchantsManager(Main main) 
	{
		_main = main;
		_itemM = _main.getItemM();
	}
	
	public void openEnchantINV(Player player)
	{
		EnchantINV ei = new EnchantINV(_main, player, "Enchantments");
		ei.openThis();
	}
	
	public void openEnchantINVmodify(Player player, ItemStack stack)
	{
		EnchantInvModify eim = new EnchantInvModify(_main, player, "modify", stack);
		eim.openThis();
	}
	
	public HashMap<Enchantment, Double[]> getEnchPrices() 
	{
		return enchPrices;
	}
	
	public void addNewEnchant(Enchantment enc, Double[] price)
	{
		enchPrices.put(enc, price);
		ItemStack encItem = makeEnchItem(enc, price);
		int contain = isContaining(encItem);
		if(contain > 0)
		{
			ench_items.set(contain, encItem);
		}else
		{
			ench_items.add(encItem);
		}
	}
	
	void removeEnchant(Enchantment enc)
	{
		enchPrices.remove(enc);
	}
	
	public ItemStack makeEnchItem(Enchantment enc, Double[] data)
	{
		ItemStack newEnc = new ItemStack(ench_item_mat);
		String display_name = WordUtils.capitalize( enc.getKey().getKey().replace("_", " "));
		_itemM.setDisplayName(newEnc, display_name);
		setEnchNameData(newEnc, enc);		
		setEnchData(newEnc, data);
		
		return newEnc;
	}
	
	public Enchantment getEnchantByName(String name)
	{
		return Enchantment.getByKey(NamespacedKey.minecraft(name));
	}
	
	public Enchantment getEnchantFromStack(ItemStack stack)
	{
		return getEnchantByName(getEnchNameData(stack));
	}
	
	public int isContaining(ItemStack stack)
	{
		ItemStack test,copy;
		test = stack.clone();
		removeEnchPriceData(test);
		
		for(int i = 0; i < ench_items.size(); ++i)
		{
			copy = ench_items.get(i).clone();
			removeEnchPriceData(copy);
			if(test.isSimilar(copy))
			{
				return i;
			}		
		}		
		return -1;
	}
	
	public boolean isEnchantPriceValid(Double[] data)
	{
		if (  (data.length < 3) || (data.length > 3))
		{
			return false;
		}
		
		if((data[0] > data[1]) || (data[2] > data[3])) // can be chanced in future
		{
			return false;
		}
		
		if(data[0] < 1 || data[1] < 1 || data[2] < 0 || data[3] < 0)
		{
			return false;
		}
		
		
		return true;
	}
	
	public void setEnchNameData(ItemStack stack, Enchantment enc)
	{
		_itemM.setPersistenData(stack, pd_name, PersistentDataType.STRING, enc.getKey().getKey());
	}
	
	public String getEnchNameData(ItemStack stack)
	{
		return _itemM.getPersistenData(stack, pd_name, PersistentDataType.STRING);
	}
	public void removeEnchNameData(ItemStack stack)
	{
		_itemM.removePersistenData(stack, pd_name);
	}
	
	public void setEnchData(ItemStack stack, Double[] price)
	{
		String str = price[0] + ":" + price[1] + ":" + price[2] + ":" + price[3];
		_itemM.setPersistenData(stack, pd_ench, PersistentDataType.STRING, str);
	}
	public Double[] getEnchPriceData(ItemStack stack)
	{

		String[] strs = _itemM.getPersistenData(stack, pd_ench,  PersistentDataType.STRING).split(":");
		Double[] data = {Double.parseDouble(strs[0]),Double.parseDouble(strs[1]),Double.parseDouble(strs[2]),Double.parseDouble(strs[3])};
		return data;
	}
	
	public void removeEnchPriceData(ItemStack stack)
	{
		_itemM.removePersistenData(stack, pd_ench);
	}
	public Double[] getEnchPrice(Enchantment ench)
	{
		return enchPrices.get(ench);
	}
	
	public void setModifyData(ItemStack stack)
	{
		_itemM.setPersistenData(stack, pd_modify, PersistentDataType.INTEGER, 1);
	}
	public void removeModifyData(ItemStack stack)
	{
		_itemM.removePersistenData(stack, pd_modify);
	}
	public boolean isModify(ItemStack stack)
	{
		Integer x = _itemM.getPersistenData(stack, pd_modify, PersistentDataType.INTEGER);
		if(x != null)
		{
			return true;
		}
		return false;
	}
	
	
}

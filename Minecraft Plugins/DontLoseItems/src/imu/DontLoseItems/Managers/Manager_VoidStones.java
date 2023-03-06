package imu.DontLoseItems.Managers;

import java.util.HashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

import imu.DontLoseItems.CustomItems.VoidStones.Unstable_Void_Stone;

import imu.DontLoseItems.Enums.VOID_STONE_TIER;
import imu.iAPI.FastInventory.Fast_Inventory;
import imu.iAPI.FastInventory.Manager_FastInventories;
import imu.iAPI.Other.Metods;

public class Manager_VoidStones	implements Listener
{
	public static Manager_VoidStones Instance;
	
//	ENCHANT_ADD_TAKE, // adds +1/+2 or -1/-2
//	REFORCE, // destroy, lower tier, upper tier
//	UNANOINTMENT, // removes one and adds new one not exist
	
	//public Unstable_Void_Stone VoidStone;
	public static HashMap<Enchantment, Integer> MAX_ENCH_LEVEL = new HashMap<>();
	public Manager_VoidStones()
	{
		Instance = this;
		//VoidStone = new Unstable_Void_Stone();
		InitMaxLevels();
	}
	
	private void InitMaxLevels()
	{
		MAX_ENCH_LEVEL.put(Enchantment.MENDING, 1);
		MAX_ENCH_LEVEL.put(Enchantment.ARROW_INFINITE, 1);
		MAX_ENCH_LEVEL.put(Enchantment.BINDING_CURSE, 1);
		MAX_ENCH_LEVEL.put(Enchantment.SILK_TOUCH, 1);
		MAX_ENCH_LEVEL.put(Enchantment.VANISHING_CURSE, 1);
	}
	
	public void SetTestItems()
	{
		Fast_Inventory fastInv= new Fast_Inventory("VoidStones", "&5VoidStones", null);
		fastInv.AddStack(new Unstable_Void_Stone().GetVoidStoneWithTier(VOID_STONE_TIER.NORMAL));
		fastInv.AddStack(new Unstable_Void_Stone().GetVoidStoneWithTier(VOID_STONE_TIER.RARE));
		Manager_FastInventories.Instance.RegisterFastInventory(fastInv);
	}
	@EventHandler
	public void Smithing(PrepareSmithingEvent e)
	{

		//if(_workInProgress) return; 
		
		SmithingInventory inv = e.getInventory();
		
		ItemStack stack1 = inv.getItem(0);
		ItemStack stack2 = inv.getItem(1);
		if(stack1 == null || stack2 == null) return;
		
		ItemStack result = null;
		if((Metods._ins.isTool(stack1) || Metods._ins.isArmor(stack1)) && Unstable_Void_Stone.IsVoidStone(stack2))
		{
			int enchantCount = stack1.getEnchantments().size();
			
			if(enchantCount < 3) return;
			
			result = new ItemStack(stack1.getType());
			//result.setAmount(1);
			String[] lores = new String[stack1.getEnchantments().size()];
			for(int i = 0; i < enchantCount; i++)
			{
				lores[i] = "&b&kTTTTTTT";
			}
			Metods._ins.SetLores(result, lores, false);
			Metods.setDisplayName(result, Metods._ins.GetItemDisplayName(stack1));
			//Metods.CloneEnchantments(stack1, result);
			e.setResult(result);
			return;
		}
		
		
		

	}
	
	
	
	@EventHandler
	public void SmithingClick(InventoryClickEvent e)
	{
		
		if(e.isCancelled()) return;
		
		if(!(e.getInventory() instanceof SmithingInventory)) return;
		
		if(e.getSlotType() != SlotType.RESULT) return;
		
		SmithingInventory inv = (SmithingInventory)e.getInventory();
				
		if(inv.getItem(0) == null || inv.getItem(1) == null) return;
		
		ItemStack stack1 = inv.getItem(0);
		ItemStack stack2 = inv.getItem(1);

		if(!(Metods._ins.isTool(stack1) || Metods._ins.isArmor(stack1))) return;
		
		if(Unstable_Void_Stone.IsVoidStone(stack2))
		{
			
			
			ItemStack stack = Unstable_Void_Stone.UseItem(stack1.clone(), Unstable_Void_Stone.GetVoidStoneTier(stack2));
			Metods._ins.InventoryAddItemOrDrop(stack, (Player)e.getWhoClicked());
			//inv.setItem(0, new ItemStack(Material.AIR));
			inv.getItem(0).setAmount(stack1.getAmount()-1);
			inv.getItem(1).setAmount(stack2.getAmount()-1);
			inv.getItem(2).setAmount(0);
			//inv.setItem(1, new ItemStack(Material.AIR));
			
		}

		
		
	}
}

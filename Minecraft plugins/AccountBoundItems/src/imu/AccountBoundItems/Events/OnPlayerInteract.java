package imu.AccountBoundItems.Events;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import imu.AccountBoundItems.Other.ItemABI;
import imu.AccountBoundItems.Other.ItemMetods;
import net.minecraft.server.v1_15_R1.ItemArmor;
import net.minecraft.server.v1_15_R1.ItemShield;

public class OnPlayerInteract implements Listener
{
	
	ItemABI itemAbi = new ItemABI();
	ItemMetods itemM = new ItemMetods();
	
	@EventHandler
	public void onDragInv(InventoryDragEvent e)
	{		
		if(e.getWhoClicked() instanceof Player)
		{
			ItemStack inMouse_stack = e.getOldCursor();
			if(itemM.hasLore(inMouse_stack, itemAbi.brokenStr))
			{
				e.setCancelled(true);
			}
		}
		
		
	}
	
	@EventHandler
	public void onSwapPlayer(PlayerSwapHandItemsEvent e)
	{
		ItemStack main_hand = e.getOffHandItem();
		if(itemM.hasLore(main_hand, itemAbi.brokenStr))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInv(InventoryClickEvent e)
	{
		ItemStack hover_stack = e.getCurrentItem();
		ItemStack inMouse_stack = e.getCursor();
		
		//System.out.println("====================");
		//System.out.println("CLICK TYPE: " + e.getClick());
		//System.out.println("ACTION: " + e.getAction());
		//System.out.println("SlotType: "+e.getSlotType());
		//System.out.println("hover: "+hover_stack);
		//System.out.println("inMouse: " + inMouse_stack);
		//System.out.println("RawSlot: "+ e.getRawSlot());
		//System.out.println("Slot: " + e.getSlot());
		
		Player player = null;
		if(e.getWhoClicked() instanceof Player)
		{
			player = (Player)e.getWhoClicked();
		}
		
		if(itemM.hasLore(hover_stack, itemAbi.boundStr))
		{
			if(player != null)
			{
				
				if(!itemAbi.getBindersName(hover_stack).equalsIgnoreCase(player.getName()))
				{
					e.setCancelled(true);
					player.closeInventory();
					player.sendMessage(ChatColor.RED + "This item doesn't belong to you!");
				}
				
			}
		}
		
		if(player != null && player.getOpenInventory().getType() == InventoryType.CRAFTING)
		{
			if(itemM.hasLore(inMouse_stack, itemAbi.brokenStr) || itemM.hasLore(hover_stack, itemAbi.brokenStr))
			{
				//System.out.println("HAS lore");
				SlotType slot_type = e.getSlotType();
					
				if(e.getRawSlot() != e.getSlot() && e.getSlot() > 8 )
				{
					if(slot_type == SlotType.ARMOR || slot_type == SlotType.QUICKBAR)
					{
						//System.out.println("Armor: "+ itemM.isInArmor(hover_stack, player));
						//System.out.println("Shield: " + itemM.isInShield(hover_stack, player) );
						if(!itemM.isInArmor(hover_stack, player) && !itemM.isInShield(hover_stack, player))
						{
							e.setCancelled(true);
							player.closeInventory();
							sendWarningMessage(player);
						}
						
						if(e.getAction() == InventoryAction.SWAP_WITH_CURSOR)
						{
							e.setCancelled(true);
							player.closeInventory();
							sendWarningMessage(player);
						}
										
					}
				}
				
				
				//System.out.println("CLICK TYPE: " + e.getClick());
				if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT)
				{
					e.setCancelled(true);
					player.closeInventory();
					sendWarningMessage(player);

				}

			
				
			}
			
		}
		
		
		
	}
	
	void sendWarningMessage(Player player)
	{
		player.sendMessage(ChatColor.RED + "You can't use broken armor! Broken armor is fragile and in use it might drop on the ground in any reason!"+ ChatColor.DARK_RED +" BE CAREFULL" );
	}
	

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		
		Player player = (Player) e.getPlayer();
		ItemStack main_stack = player.getInventory().getItemInMainHand();
		ItemStack off_stack = player.getInventory().getItemInOffHand();
		
		if(CraftItemStack.asNMSCopy(main_stack).getItem() instanceof ItemArmor || CraftItemStack.asNMSCopy(main_stack).getItem() instanceof ItemShield)
		{
			if(itemM.hasLore(main_stack, itemAbi.brokenStr) || itemM.hasLore(off_stack, itemAbi.brokenStr))
			{
				e.setCancelled(true);
				itemAbi.moveItemFirstFreeSpaceInv(main_stack, player, false);
				itemAbi.moveItemFirstFreeSpaceInv(off_stack, player, false);
			}
			
			
		}
		
		
		
		
	}

	
	
}

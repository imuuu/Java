package imu.GeneralStore.Invs;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.GeneralStore.Managers.ShopModManager;
import imu.GeneralStore.Other.CustomInvLayout;
import imu.GeneralStore.Other.Shop;
import imu.GeneralStore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class ShopModINV extends CustomInvLayout implements Listener
{
	ShopModManager _smm;
	ArrayList<ItemStack> _shop_stacks;
	
	int unique_slots = 0;
	int current_page = 0;
	
	String pd_buttonType = "gs.sModI.buttonType";
	
	Shop _shop;
	public ShopModINV(Main main, Player player, String name, Shop shop) 
	{
		super(main, player, name, 9*6);
		
		main.getServer().getPluginManager().registerEvents(this, _main);

		_shop_stacks = shop.getShopStacks();
		
		unique_slots = _size-9;
		_shop = shop;
		_smm = _main.getShopModManager();
	}

	enum BUTTON
	{
		NONE,
		GO_LEFT,
		GO_RIGHT,
		SHOP_ITEM;
		
	}
	
	int totalPages()
	{
		int pages =(int) Math.round(((_shop_stacks.size()-1)/(unique_slots))+0.5);
		return pages-1;
	}
	
	void chanceCurrentPage(int i)
	{
		current_page = current_page + i;
		if(current_page < 0)
		{
			current_page = 0;
		}
		if(current_page > totalPages())
		{
			current_page = totalPages();
		}
	}
	
	void setButton(ItemStack stack, BUTTON b)
	{
		itemM.setPersistenData(stack, pd_buttonType, PersistentDataType.STRING, b.toString());
	}
	
	BUTTON getButton(ItemStack stack)
	{
		String button = itemM.getPersistenData(stack, pd_buttonType, PersistentDataType.STRING);
		if(button != null)
			return BUTTON.valueOf(button);
		
		return BUTTON.NONE;
	}
	
	void makeInv()
	{
		ItemStack optionLine = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
		itemM.setDisplayName(optionLine, " ");
		
		for(int i = _size-1; i > unique_slots-1; --i)
		{
			_inv.setItem(i, optionLine);
		}
				
		ItemStack left_button = new ItemStack(Material.BIRCH_SIGN);
		ItemStack right_button = left_button.clone();
		itemM.setDisplayName(left_button, ChatColor.AQUA + "<<");
		itemM.setDisplayName(right_button, ChatColor.AQUA + ">>");
		
		setButton(left_button, BUTTON.GO_LEFT);
		setButton(right_button, BUTTON.GO_RIGHT);
		
		_inv.setItem(unique_slots, left_button);
		_inv.setItem(_size-1, right_button);
		
		//refreshItems();
	}
	
	@EventHandler
	public void onInvOpen(InventoryOpenEvent e)
	{
		if(isThisInv(e))
		{
			makeInv();
			refreshItems();
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			HandlerList.unregisterAll(this);
		}
	}
	
	@EventHandler
	public void onInvClickEvent(InventoryClickEvent e) 
	{
		int rawSlot = e.getRawSlot();
		int slot = e.getSlot();
		
		if(isThisInv(e) && (rawSlot == slot))
		{			
			e.setCancelled(true);
			ItemStack stack = e.getCurrentItem();
			
			BUTTON button = getButton(stack);
			int item_id = (current_page * unique_slots)+slot;
			switch (button) 
			{
			case NONE:
				
				break;
			case SHOP_ITEM:
				_smm.openModShopModifyInv(_player, _shop.getShopStacks().get(item_id), _shop, null);
				break;
			case GO_LEFT:				
				chanceCurrentPage(-1);
				refreshItems();
				return;

			case GO_RIGHT:
				chanceCurrentPage(1);
				refreshItems();
				return;
				
			default:
				break;
			}
			
		}	
	}
	
	void setTooltip(ItemStack stack)
	{
		String modifyStr =ChatColor.YELLOW +"== Click to modify ==";
		itemM.addLore(stack, modifyStr, false);
		
		String custom_amount, c_permission, c_price ,c_worlds, c_stock_delay_amount, c_soldBack, c_soldDistance;
		
		custom_amount = _shop.getPDCustomAmount(stack) != null ? _shop.getPDCustomAmount(stack).toString(): "None";
		c_permission = _shop.getPDCustomPermission(stack) != null ? _shop.getPDCustomPermission(stack) : "None";
		c_price = _shop.getPDCustomPrice(stack) != null ? _shop.getPDCustomPrice(stack).toString() : "None";
		c_worlds = _shop.getPDCustomWorlds(stack) != null ? _shop.getPDCustomWorlds(stack) : "None";
		c_stock_delay_amount = _shop.getPDCustomStockDelay(stack) != null ? _shop.getPDCustomStockDelay(stack).toString()+" "+_shop.getPDCustomStockAmount(stack).toString() : "None";
		c_soldBack = _shop.getPDCustomCanSoldBack(stack) != null ? "false" : "true";
		c_soldDistance = _shop.getPDCustomSoldDistance(stack) != null ? _shop.getPDCustomSoldDistance(stack).toString() : "None";
		
		String color = ChatColor.BLUE+"";
		String color2 = ChatColor.YELLOW+"";
		
		itemM.addLore(stack, color +"Custom amount: "+color2+custom_amount, true);
		itemM.addLore(stack, color +"Permission: "+color2+c_permission, true);
		itemM.addLore(stack, color +"S_Delay&Fill_amount: "+color2+c_stock_delay_amount, true);
		itemM.addLore(stack, color +"Custom Price: "+color2+c_price, true);
		itemM.addLore(stack, color +"World(s): "+color2+c_worlds, true);
		itemM.addLore(stack, color +"Can be Sold: "+color2+c_soldBack, true);
		itemM.addLore(stack, color +"Sold Distance&Loc: "+color2+c_soldDistance, true);
		
		
		
	}
	ItemStack removeTooltip(ItemStack stack)
	{
		String modifyStr =ChatColor.YELLOW +"== Click to modify ==";
		return itemM.removeLore(stack, modifyStr);
	}
	
	void refreshItems()
	{

		int start = 0 + current_page * (unique_slots);
		ItemStack empty = new ItemStack( Material.BLACK_STAINED_GLASS_PANE);
		ItemStack stack, copy;
		itemM.setDisplayName(empty, " ");
		
		for(int i = 0; i < unique_slots ; ++i)
		{
			int idx = i+start;
			if(idx < _shop_stacks.size())
			{			
				stack = _shop_stacks.get(idx);
				copy = stack.clone();
				if(_shop.isStackInf(stack))
				{
					setTooltip(copy);
					setButton(copy, BUTTON.SHOP_ITEM);					
				}else
				{
					setButton(copy, BUTTON.NONE);
				}
				_inv.setItem(i, copy);
			}
			else
			{
				_inv.setItem(i,empty);
			}
		}
	}
}

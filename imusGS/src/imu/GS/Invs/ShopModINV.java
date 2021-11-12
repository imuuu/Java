package imu.GS.Invs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import imu.GS.ENUMs.ITEM_MOD_DATA;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ShopItemModData;

import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;
import net.md_5.bungee.api.ChatColor;

public class ShopModINV extends CustomInvLayout
{
	ArrayList<ItemStack> _shop_stacks;
	
	int unique_slots = 0;
	int current_page = 0;
	
	String pd_buttonType = "gs.sModI.buttonType";
	
	ShopBase _shop;
	Main _main;
	boolean _isClosed = false;
	
	HashMap<UUID, ShopItemModData> _newModDatas = new HashMap<>();
	
	public ShopModINV(Main main, Player player, ShopBase shop) 
	{
		super(main, player, "Modding: " + shop.GetNameWithColor(), 9*6);
		_main = main;
		unique_slots = _size-9;
		_shop = shop;
		

	}

	
	
	enum BUTTON implements IButton
	{
		NONE,
		GO_LEFT,
		GO_RIGHT,
		SHOP_ITEM,
		SAVE_SHOP_DATA,
		OVERRIDE_ALL;
	}
		
	int TotalPages()
	{
		return _shop.get_items().size()-1;
	}
	
	void chanceCurrentPage(int i)
	{
		current_page += i;
		if(current_page < 0)
		{
			current_page = 0;
		}
		if(current_page > TotalPages())
		{
			current_page = TotalPages();
		}
	}
	
	void makeInv()
	{
		ItemStack optionLine = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
		ImusAPI._metods.setDisplayName(optionLine, " ");
		
		
		for(int i = _shop.get_items().get(current_page).length; i < _size; ++i)
		{
			_inv.setItem(i, optionLine);
		}
				
		ItemStack left_button = new ItemStack(Material.BIRCH_SIGN);
		ItemStack right_button = left_button.clone();
		
		ItemStack saveAll_button = new ItemStack(Material.GOLD_INGOT);
		ImusAPI._metods.setDisplayName(saveAll_button, ChatColor.AQUA + "Save shop items to config!");
		ImusAPI._metods.addLore(saveAll_button, ChatColor.BLUE + "Press this after you have edited some items", true);
		ImusAPI._metods.addLore(saveAll_button, ChatColor.BLUE + "Normally this will be done onDisabled", true);
		ImusAPI._metods.addLore(saveAll_button, ChatColor.BLUE + "If server crashes the onDisable never initialize(data lost) ", true);
		
		ItemStack override_button = new ItemStack(Material.PAPER);
		ImusAPI._metods.setDisplayName(override_button, ChatColor.AQUA +"Override all");
		ImusAPI._metods.addLore(override_button, ChatColor.BLUE + "Set same data to all", true);
		ImusAPI._metods.addLore(override_button, ChatColor.BLUE + "if modify is none, it will be removed from all items too", true);
		
		
		ImusAPI._metods.setDisplayName(left_button, ChatColor.AQUA + "<<");
		ImusAPI._metods.setDisplayName(right_button, ChatColor.AQUA + ">>");
		
		SetButton(left_button, BUTTON.GO_LEFT);
		SetButton(right_button, BUTTON.GO_RIGHT);
		SetButton(saveAll_button, BUTTON.SAVE_SHOP_DATA);
		SetButton(override_button, BUTTON.OVERRIDE_ALL);
		
		_inv.setItem(unique_slots, left_button);
		_inv.setItem(unique_slots+2, saveAll_button);
		_inv.setItem(_size-1, right_button);
		_inv.setItem(unique_slots+4, override_button);
		
		//refreshItems();
	}
	
	@EventHandler
	public void onInvOpen(InventoryOpenEvent e)
	{
		if(isThisInv(e))
		{
			_isClosed = _shop.HasLocked();
			_shop.SetLocked(true);
			_main.get_shopManager().RegisterOpenedInv(_player, this);
			makeInv();
			refreshItems();
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			_shop.SetLocked(_isClosed);
			_main.get_shopManager().UnRegisterOpenedInv(_player);
		}
	}
	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		ItemStack stack = e.getCurrentItem();
		if(stack == null) return;
		
		String bStr = getButtonName(stack);
		
		if(bStr == null) return;
		
		BUTTON button = BUTTON.valueOf(bStr);
		//int slot = e.getSlot();
		
		
		//int item_id = (current_page * unique_slots)+slot;
		
		switch (button) 
		{
		case NONE:
			
			break;
		case SHOP_ITEM:
			ShopItemStockable item = (ShopItemStockable)_shop.GetItem(current_page, e.getSlot());
			new ShopModModifyINV(_main, _player, item, item.GetModData()).openThis();
			break;
		case GO_LEFT:				
			chanceCurrentPage(-1);
			refreshItems();
			return;

		case GO_RIGHT:
			chanceCurrentPage(1);
			refreshItems();
			return;
		case SAVE_SHOP_DATA:
			_main.get_shopManager().SaveShop(_shop._name, false);
			_player.closeInventory();
			_player.sendMessage(ChatColor.GOLD + "(Shop) "+_shop.GetNameWithColor()+ " items has been saved!");
			break;
		case OVERRIDE_ALL:
			//_smm.openModShopModifyOVERRIDE_ALL_Inv(_player, stack, _shop, null);
			System.out.println("open override all");
			break;
		default:
			break;
		}
	}

	
	void SetTooltip(ItemStack stack, ShopItemModData _modData)
	{
		String modifyStr = ChatColor.YELLOW +"== Click to modify ==";
		ImusAPI._metods.addLore(stack, modifyStr, false);
		
		String custom_amount, c_permission, c_price ,c_worlds, c_fill_delay, c_fill_amount, c_soldBack, c_soldDistance, c_selltime;
		String none_color = ChatColor.RED + "";
		String true_color = ChatColor.AQUA + "";
		
		String none_str = none_color + "None";
		
		custom_amount =        _modData._maxAmount != -1 ?     		true_color+ _modData._maxAmount : none_str;
		c_permission =         _modData._permissions != null ? 		true_color+ ImusAPI._metods.CombineArrayToOneString(_modData._permissions.toArray(), "; ") : none_str;
		c_price =              _modData.GetValueStr(ITEM_MOD_DATA.CUSTOM_PRICE, null,null,none_str);
		c_worlds =  	       _modData._worldNames != null ?     	true_color+ ImusAPI._metods.CombineArrayToOneString(_modData._worldNames.toArray(), "; ") : none_str;
		c_fill_delay = 		   _modData._fillDelayMinutes != -1 ? 	true_color+ _modData._fillDelayMinutes : none_str;
		c_fill_amount = 	   _modData._fillAmount != -1 ? 		true_color+ _modData._fillAmount :none_str;
		//c_soldBack =           _shop.getPDCustomCanSoldBack(stack) != null ? none_color+"false" : ChatColor.AQUA + "true";
		String[] disStr = _modData.GetValueStr(ITEM_MOD_DATA.DISTANCE_LOC, null,null,none_str).split("; ");
		c_soldDistance = disStr.length > 1 ? true_color + "More than one" : disStr[0];		
		c_selltime =           _modData.GetValueStr(ITEM_MOD_DATA.SELL_TIME_START, null,null,none_str);
		String color = ChatColor.BLUE+"";
		String color2 = ChatColor.YELLOW+"";
		
		ImusAPI._metods.addLore(stack, color +"Custom amount: "+color2+custom_amount, true);
		ImusAPI._metods.addLore(stack, color +"Permission: "+color2+c_permission, true);
		ImusAPI._metods.addLore(stack, color +"Fill Delay: "+color2+c_fill_delay, true);
		ImusAPI._metods.addLore(stack, color +"Fill Amount: "+color2+c_fill_amount, true);
		ImusAPI._metods.addLore(stack, color +"Custom Price: "+color2+c_price, true);
		ImusAPI._metods.addLore(stack, color +"World(s): "+color2+c_worlds, true);
		//ImusAPI._metods.addLore(stack, color +"Can be Sold: "+color2+c_soldBack, true);
		ImusAPI._metods.addLore(stack, color +"Sold Distance&Loc: "+color2+c_soldDistance, true);
		//ImusAPI._metods.addLore(stack, c_soldDistance, _isClosed)
		
		ImusAPI._metods.addLore(stack, color +"Sell time: "+color2+c_selltime, true);
		
		
		
	}
	ItemStack removeTooltip(ItemStack stack)
	{
		String modifyStr = ChatColor.YELLOW +"== Click to modify ==";
		return ImusAPI._metods.removeLore(stack, modifyStr);
	}
	
	void refreshItems()
	{

		ItemStack empty = ImusAPI._metods.setDisplayName(new ItemStack( Material.BLACK_STAINED_GLASS_PANE), " ");
		ItemStack copy;
		
		for(int slot = 0; slot < _shop.get_items().get(current_page).length; slot++)
		{
			
			ShopItemBase sib = _shop.GetItem(current_page, slot);
			if( sib == null)
			{
				_inv.setItem(slot, SetButton(empty, BUTTON.NONE));
				continue;
			}
			
			if(!(sib instanceof ShopItemStockable))
			{
				ItemStack regularItem = new ItemStack(sib.GetRealItem().getType());
				ImusAPI._metods.addLore(regularItem, "&cCan't be modified!", false);
				_inv.setItem(slot, SetButton(regularItem, BUTTON.NONE));
				continue;
			}
			
			ShopItemStockable sis = (ShopItemStockable) sib;
			
			copy = sis.GetRealItem().clone();
			ShopItemModData modData;
			if(!_newModDatas.containsKey(sis.GetUUID()))
			{
				_newModDatas.put(sis.GetUUID(),(ShopItemModData)sis.GetModData().clone());
			}
			modData = _newModDatas.get(sis.GetUUID());
				
			SetTooltip(copy, modData);
			_inv.setItem(slot, SetButton(copy, BUTTON.SHOP_ITEM));
			
		}
				
	}

	@Override
	public void invClosed(InventoryCloseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void setupButtons() {
		// TODO Auto-generated method stub
		
	}
}

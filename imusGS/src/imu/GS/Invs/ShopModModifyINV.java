package imu.GS.Invs;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Strings;

import imu.GS.ENUMs.ITEM_MOD_DATA;
import imu.GS.Main.Main;
import imu.GS.Prompts.ConvPromptModModifyINV;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemModData;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;
import net.md_5.bungee.api.ChatColor;

public class ShopModModifyINV extends CustomInvLayout
{
	ShopItemSeller _sis;
	ItemStack copy_item;
	
	String pd_buttonType = "gs.sModModI.buttonType";
	String pd_buttonAnswer ="gs.sModModI.buttonAnser";

	ShopBase _shop;
	
	Main _main;
	ShopItemModData _modData;
	boolean _isClosed;
	public ShopModModifyINV(Main main, Player player,ShopItemSeller sis, ShopItemModData modData) 
	{
		super(main, player, "Modify item", 9*3);
		_main = main;
		_sis = sis;
		copy_item = _sis.GetRealItem().clone();
		_shop = sis.GetShop();
		
		SetModData((ShopItemModData)modData.clone());
	}
		
	enum BUTTON implements IButton
	{
		NONE,
		BACK,
		CONFIRM,
		CUSTOM_AMOUNT,
		SET_PERMISSION,
		SET_FILL_DELAY,
		SET_FILL_AMOUNT,
		SET_CUSTOM_PRICE,
		SET_WORLDS,
		SET_CAN_BE_SOLD,
		SET_DISTANCE,
		REMOVE_INF,
		SET_TIME_SELL;
						
	}
	
	public ShopModModifyINV SetModData(ShopItemModData modData)
	{
		_modData = modData;
		makeInv();
		return this;
	}
	
	void makeInv()
	{
		for(int i = 0; i < _inv.getContents().length; ++i)
		{
			_inv.setItem(i, ImusAPI._metods.setDisplayName(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " "));
		}
		
		String m1m2 = ImusAPI._metods.msgC("&bM1: &aSet &bM2: &cRemove");
		String setTo = ImusAPI._metods.msgC("&aSet To &1");
		String lore;
		int id = 0;
		setupButton(BUTTON.BACK, Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "<< BACK", 0);
		setupButton(BUTTON.CONFIRM, Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "<< CONFIRM", 9*2);
		_inv.setItem(9, new ItemStack(copy_item));
		setupButton(BUTTON.REMOVE_INF, Material.LAVA_BUCKET, ChatColor.RED + ""+ChatColor.BOLD + "Remove this items from shop", 8);
		
		id = 2;
		lore = _modData.GetValueStr(ITEM_MOD_DATA.MAX_AMOUNT,setTo ,null, m1m2);
		ItemStack custom_amount = setupButton(BUTTON.CUSTOM_AMOUNT, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE, ChatColor.DARK_PURPLE + "Custom amount", id);
		ImusAPI._metods.addLore(custom_amount, _modData.GetValueStr(ITEM_MOD_DATA.MAX_AMOUNT,setTo ,null, m1m2), false);
				
		id = 4;
		lore = _modData.GetValueStr(ITEM_MOD_DATA.PERMISSIONS,setTo ,null,  m1m2);
		ItemStack setPerms = setupButton(BUTTON.SET_PERMISSION, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE, ChatColor.DARK_PURPLE + "Set permission", id);
		ImusAPI._metods.addLore(setPerms, lore, false);
		
		id = 6;
		lore = _modData.GetValueStr(ITEM_MOD_DATA.FILL_DELAY,setTo ,null,  m1m2);
		ItemStack setFillDelay = setupButton(BUTTON.SET_FILL_DELAY, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE, ChatColor.DARK_PURPLE + "Set Stock FillDelay(min)", id);
		ImusAPI._metods.addLore(setFillDelay, lore, false);
		ImusAPI._metods.addLore(setFillDelay, ChatColor.YELLOW + "How often stock will be filled and", true);
		//ImusAPI._metods.addLore(setDelay_Amount, ChatColor.BLUE + "Minimum time: "+ _shop.getStockCheckTime()/60, true);
		
		id = 12;
		lore = _modData.GetValueStr(ITEM_MOD_DATA.FILL_AMOUNT,setTo ,null,  m1m2);
		ItemStack setFillAmount = setupButton(BUTTON.SET_FILL_AMOUNT, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE, ChatColor.DARK_PURPLE + "Set Stock FillAmount", id);
		ImusAPI._metods.addLore(setFillAmount, lore , false);
		ImusAPI._metods.addLore(setFillAmount, ChatColor.YELLOW + "How many it fills. Cant go over", true);
		ImusAPI._metods.addLore(setFillAmount, ChatColor.YELLOW + "shop amount(or custom amount)", true);
		//ImusAPI._metods.addLore(setDelay_Amount, ChatColor.BLUE + "Minimum time: "+ _shop.getStockCheckTime()/60, true);
		
		id = 14;
		lore = _modData.GetValueStr(ITEM_MOD_DATA.OWN_PRICE,setTo ,null,  m1m2);
		ItemStack setCusPrice = setupButton(BUTTON.SET_CUSTOM_PRICE, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE, ChatColor.DARK_PURPLE + "Set Custom Price", id);
		ImusAPI._metods.addLore(setCusPrice, lore, false);
		ImusAPI._metods.addLore(setCusPrice, ChatColor.YELLOW + "Set own price for item in this shop!", true);
		ImusAPI._metods.addLore(setCusPrice, ChatColor.YELLOW + "Be carefull with money explote! Cant be too cheap!", true);
		
		
		id = 20;
		lore = _modData.GetValueStr(ITEM_MOD_DATA.WORLD_NAMES,setTo , null,  m1m2);
		ItemStack setWorlds = setupButton(BUTTON.SET_WORLDS, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE, ChatColor.DARK_PURPLE + "Set Spesific worlds", id);
		ImusAPI._metods.addLore(setWorlds, lore, false);
		ImusAPI._metods.addLore(setWorlds, ChatColor.YELLOW + "Set worlds where you can get this item and", true);
		ImusAPI._metods.addLore(setWorlds, ChatColor.YELLOW + "Item will be removed(from inv) if entered wrong world", true);
				
//		id = 20;
//		ItemStack setCanBeSold = setupButton(BUTTON.SET_CAN_BE_SOLD, Material.GLASS_PANE,  ChatColor.DARK_PURPLE + "Can Item sold back to GS", id);
//		ImusAPI._metods.addLore(setCanBeSold, m1m2, false);
//		ImusAPI._metods.addLore(setCanBeSold, ChatColor.YELLOW + "If true, you can sold it back. If false you cant!", true);
				
		id = 22;
		//String locationStr = _modData.GetValueStr(ITEM_MOD_DATA.LOCATION, " " ,null, "");
		//System.out.println("location text: "+locationStr);
		lore = _modData.GetValueStr(ITEM_MOD_DATA.DISTANCE_LOC, null ,null, m1m2);
		ItemStack setDistance_item = setupButton(BUTTON.SET_DISTANCE, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE,  ChatColor.DARK_PURPLE + "Set Distance radius.", id);		
		ImusAPI._metods.addLore(setDistance_item, lore.split("; "), false);
		ImusAPI._metods.addLore(setDistance_item, setTo, false);
		ImusAPI._metods.addLore(setDistance_item, ChatColor.YELLOW + "Item will be shown inside given radius", true);
		
		id = 24;
		lore = _modData.GetValueStr(ITEM_MOD_DATA.SELL_TIME_START,setTo ,null,  m1m2);
		ItemStack setTimeSell = setupButton(BUTTON.SET_TIME_SELL, lore.equalsIgnoreCase(m1m2) ? Material.GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE,  ChatColor.DARK_PURPLE + "Set sell time", id);
		ImusAPI._metods.addLore(setTimeSell, lore , false);
		ImusAPI._metods.addLore(setTimeSell, ChatColor.YELLOW + "Set time when item will be apearing in shop", true);
			
	}
	
	@Override
	public void openThis() 
	{
		super.openThis();
		if(!HasRegistered())
			RegisterToEvents();
		
		_main.get_shopManager().RegisterOpenedInv(_player, this);
	}
	
	
	@EventHandler
	public void onInvOpen(InventoryOpenEvent e)
	{
		if(isThisInv(e))
		{
			_isClosed = _shop.HasLocked();
			_shop.SetLocked(true);
			//makeInv();
		}
	}
	

	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		ItemStack stack = e.getCurrentItem();
		if(stack == null) return;
		String bName = getButtonName(stack);
		
		if(Strings.isNullOrEmpty(bName)) return;
		
		BUTTON button = BUTTON.valueOf(bName);
		
		ConversationFactory cf = null;
		String question = null;
		Conversation conv = null;
		//int remove_state = 0;
		if(e.getClick() == ClickType.LEFT)
		{
			cf = new ConversationFactory(_main);
			switch (button) 
			{
			case NONE:
				
				break;
			case BACK:
				_player.closeInventory();
				new ShopModINV(_main, _player, _shop).openThis();
				return;
			case CONFIRM:
//				if(confirm())						
//					_smm.openModShopInv(_player, _shop);
//				
				((ShopItemStockable)_sis).SetModData(_modData);
				_player.closeInventory();
				new ShopModINV(_main, _player, _shop).openThis();
				return;
			case CUSTOM_AMOUNT:				
				question = ChatColor.DARK_PURPLE + "Give Custom amount?";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.MAX_AMOUNT, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;
			case SET_PERMISSION:
				question = ChatColor.DARK_PURPLE + "Give Permission name?";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.PERMISSIONS, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;
			case SET_FILL_DELAY:
				question = ChatColor.DARK_PURPLE + "Give Fill Delay";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.FILL_DELAY, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;
			case SET_FILL_AMOUNT:
				question = ChatColor.DARK_PURPLE + "Give Fill Amount";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.FILL_AMOUNT, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;
			case SET_CUSTOM_PRICE:				
				question = ChatColor.DARK_PURPLE + "Give Own Price?";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.OWN_PRICE, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;
			case SET_WORLDS:
				question = ChatColor.DARK_PURPLE + "Give World name?(ex: world world_nether this(takes world where you are) )";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.WORLD_NAMES, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;
			case SET_DISTANCE:
				question = ChatColor.DARK_PURPLE + "Give Distance and location?{distance worldName x y z}(ex: 50 world 1 55 23 OR 50 this(this= ur loc))";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.DISTANCE_LOC, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;
			case REMOVE_INF:
				System.out.println("REMOVE INF");
				_shop.RemoveItem(_sis.GetPage(), _sis.GetSlot());
				_player.closeInventory();
				new ShopModINV(_main, _player, _shop).openThis();
				return;
			case SET_TIME_SELL:
				question = ChatColor.DARK_PURPLE + "Give startTime and endTime  Seperate by space(ex: 0 13000(whole daytime)(min:0 max:24000)";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.SELL_TIME_START, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				break;	
			default:
				break;
			}
			if(conv != null) conv.begin();			
			_player.closeInventory();
		}
		else if(e.getClick() == ClickType.RIGHT)
		{
			switch (button) 
			{
			case CUSTOM_AMOUNT:
				_modData._maxAmount = -1;
				break;
			case SET_PERMISSION:
				_modData.ClearPermissions();
				break;
			case SET_FILL_DELAY:
				_modData._fillDelayMinutes = -1;
				break;
			case SET_FILL_AMOUNT:
				_modData._fillAmount = -1;
				break;
			case SET_CUSTOM_PRICE:
				_modData._ownPrice = -1;
				break;
			case SET_WORLDS:
				_modData.ClearWorldNames();
				break;
			case SET_DISTANCE:
				_modData.ClearLocations();
				break;
			case REMOVE_INF:
//				if(_answers[slot].equalsIgnoreCase("sure"))
//				{
//					_shop.removeItemFromShopNEW(orginal_item,true);
//					_main.getShopModManager().openModShopInv(_player, _shop);
//				}
				break;
			case SET_TIME_SELL:
				_modData._sellTimeStart = -1;
				_modData._sellTimeEnd = -1;
				break;
			
			}
		}
		makeInv();
		
	}
	
	

	@Override
	public void invClosed(InventoryCloseEvent e) 
	{
		_shop.SetLocked(_isClosed);
		_main.get_shopManager().UnRegisterOpenedInv(_player);
	}

	

	@Override
	public void setupButtons() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

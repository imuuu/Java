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
	public ShopModModifyINV(Main main, Player player, String name, ShopItemSeller sis, ShopItemModData modData) 
	{
		super(main, player, name, 9*3);
		_main = main;
		_modData = modData;
		_sis = sis;
		copy_item = _sis.GetRealItem().clone();
		_shop = sis.GetShop();
		_isClosed = _shop.HasLocked();


//		if(answers == null)
//			_newInv = true;
//
//		initAnswers(answers);
		
		
		//openThis();
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
	

	
	void makeInv()
	{
		for(int i = 0; i < _inv.getContents().length; ++i)
		{
			_inv.setItem(i, ImusAPI._metods.setDisplayName(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " "));
		}
		
		String m1m2 = ImusAPI._metods.msgC("&bM1: &aSet &bM2: &cRemove");
		
		int id = 0;
		setupButton(BUTTON.BACK, Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "<< BACK", 0);
		setupButton(BUTTON.CONFIRM, Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "<< CONFIRM", 9*2);
		_inv.setItem(9, new ItemStack(copy_item));
		setupButton(BUTTON.REMOVE_INF, Material.LAVA_BUCKET, ChatColor.RED + ""+ChatColor.BOLD + "Remove this items from shop", 8);
		
		
		id = 2;
		ItemStack custom_amount = setupButton(BUTTON.CUSTOM_AMOUNT, Material.GLASS_PANE, ChatColor.DARK_PURPLE + "Custom amount", id);
		ImusAPI._metods.addLore(custom_amount, _modData.GetValueStr(ITEM_MOD_DATA.MAX_AMOUNT, m1m2), false);
		
		
		id = 4;
		ItemStack setPerms = setupButton(BUTTON.SET_PERMISSION, Material.GLASS_PANE, ChatColor.DARK_PURPLE + "Set permission", id);
		ImusAPI._metods.addLore(setPerms, _modData.GetValueStr(ITEM_MOD_DATA.PERMISSIONS, m1m2), false);
		
		id = 6;
		ItemStack setFillDelay = setupButton(BUTTON.SET_FILL_DELAY, Material.GLASS_PANE, ChatColor.DARK_PURPLE + "Set Stock Delay(min) and FillAmount", id);
		ImusAPI._metods.addLore(setFillDelay, _modData.GetValueStr(ITEM_MOD_DATA.FILL_DELAY, m1m2), false);
		ImusAPI._metods.addLore(setFillDelay, ChatColor.YELLOW + "How often stock will be filled and", true);
		//ImusAPI._metods.addLore(setDelay_Amount, ChatColor.BLUE + "Minimum time: "+ _shop.getStockCheckTime()/60, true);
		
		id = 12;
		ItemStack setFillAmount = setupButton(BUTTON.SET_FILL_AMOUNT, Material.GLASS_PANE, ChatColor.DARK_PURPLE + "Set Stock FillAmount", id);
		ImusAPI._metods.addLore(setFillAmount, _modData.GetValueStr(ITEM_MOD_DATA.FILL_AMOUNT, m1m2), false);
		ImusAPI._metods.addLore(setFillAmount, ChatColor.YELLOW + "How many it fills. Cant go over", true);
		ImusAPI._metods.addLore(setFillAmount, ChatColor.YELLOW + "shop amount(or custom amount)", true);
		//ImusAPI._metods.addLore(setDelay_Amount, ChatColor.BLUE + "Minimum time: "+ _shop.getStockCheckTime()/60, true);
		
		id = 14;
		ItemStack setCusPrice = setupButton(BUTTON.SET_CUSTOM_PRICE, Material.GLASS_PANE, ChatColor.DARK_PURPLE + "Set Custom Price", id);
		ImusAPI._metods.addLore(setCusPrice, _modData.GetValueStr(ITEM_MOD_DATA.OWN_PRICE, m1m2), false);
		ImusAPI._metods.addLore(setCusPrice, ChatColor.YELLOW + "Set own price for item in this shop!", true);
		ImusAPI._metods.addLore(setCusPrice, ChatColor.YELLOW + "Be carefull with money explote! Cant be too cheap!", true);
		
		
		id = 20;
		ItemStack setWorlds = setupButton(BUTTON.SET_WORLDS, Material.GLASS_PANE, ChatColor.DARK_PURPLE + "Set Spesific worlds", id);
		ImusAPI._metods.addLore(setWorlds, _modData.GetValueStr(ITEM_MOD_DATA.WORLD_NAMES, m1m2), false);
		ImusAPI._metods.addLore(setWorlds, ChatColor.YELLOW + "Set worlds where you can get this item and", true);
		ImusAPI._metods.addLore(setWorlds, ChatColor.YELLOW + "Item will be removed(from inv) if entered wrong world", true);
				
//		id = 20;
//		ItemStack setCanBeSold = setupButton(BUTTON.SET_CAN_BE_SOLD, Material.GLASS_PANE,  ChatColor.DARK_PURPLE + "Can Item sold back to GS", id);
//		ImusAPI._metods.addLore(setCanBeSold, m1m2, false);
//		ImusAPI._metods.addLore(setCanBeSold, ChatColor.YELLOW + "If true, you can sold it back. If false you cant!", true);
				
		id = 22;
		ItemStack setDistance_item = setupButton(BUTTON.SET_DISTANCE, Material.GLASS_PANE,  ChatColor.DARK_PURPLE + "Set Distance radius. You position as anchor!", id);
		ImusAPI._metods.addLore(setDistance_item, _modData.GetValueStr(ITEM_MOD_DATA.DISTANCE, m1m2), false);
		ImusAPI._metods.addLore(setDistance_item, ChatColor.YELLOW + "Set your location and item will be shown in given radius", true);
		
		id = 24;
		ItemStack setTimeSell = setupButton(BUTTON.SET_TIME_SELL, Material.GLASS_PANE,  ChatColor.DARK_PURPLE + "Set sell time", id);
		ImusAPI._metods.addLore(setTimeSell, _modData.GetValueStr(ITEM_MOD_DATA.SELL_TIME_START, m1m2), false);
		ImusAPI._metods.addLore(setTimeSell, ChatColor.YELLOW + "Set time when item will be apearing in shop", true);
		
		
	}
	

	
	
	@EventHandler
	public void onInvOpen(InventoryOpenEvent e)
	{
		if(isThisInv(e))
		{
			_shop.SetLocked(true);
			makeInv();
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			_shop.SetLocked(_isClosed);
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
			switch (button) 
			{
			case NONE:
				
				break;
			case BACK:
				//_smm.openModShopInv(_player, _shop);
				break;
			case CONFIRM:
//				if(confirm())						
//					_smm.openModShopInv(_player, _shop);
//				
				break;
			case CUSTOM_AMOUNT:

				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give Custom amount?";
				//conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_main, _player, orginal_item, _shop,_answers, slot,question)).withLocalEcho(true).buildConversation(_player);
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.MAX_AMOUNT, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();
				
				_player.closeInventory();
				break;
			case SET_PERMISSION:
				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give Permission name?";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.PERMISSIONS, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();
				
				_player.closeInventory();
				
				break;
			case SET_FILL_DELAY:
				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give Fill Delay";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.FILL_DELAY, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();
				_player.closeInventory();
				break;
			case SET_FILL_AMOUNT:
				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give Fill Amount";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.FILL_AMOUNT, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();	
				_player.closeInventory();
				break;
			case SET_CUSTOM_PRICE:				
				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give price";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.OWN_PRICE, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();
				
				_player.closeInventory();
				break;
			case SET_WORLDS:
				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give Worlds. Seperate by space(ex: world world_nether this(takes world where you are) )";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.WORLD_NAMES, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();
				
				_player.closeInventory();
			
			case SET_DISTANCE:
				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give Distance?";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.DISTANCE, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();
				
				_player.closeInventory();
				break;
			case REMOVE_INF:
				System.out.println("REMOVE INF");
				break;
			case SET_TIME_SELL:
				cf = new ConversationFactory(_main);
				question = ChatColor.DARK_PURPLE + "Give startTime and endTime  Seperate by space(ex: 0 13000(whole daytime)(min:0 max:24000)";
				conv = cf.withFirstPrompt(new ConvPromptModModifyINV(_player, ITEM_MOD_DATA.SELL_TIME_START, this, _modData, question)).withLocalEcho(true).buildConversation(_player);
				conv.begin();
				_player.closeInventory();
				break;	
			default:
				break;
			}
		}
		else if(e.getClick() == ClickType.RIGHT)
		{
			switch (button) 
			{
			case CUSTOM_AMOUNT:
				_modData._maxAmount = -1;
				break;
			case SET_PERMISSION:
				_modData._permissions = null;
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
				_modData._worldNames = null;
				break;
			case SET_DISTANCE:
				_modData._distance = -1;
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
	public void invClosed(InventoryCloseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void setupButtons() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

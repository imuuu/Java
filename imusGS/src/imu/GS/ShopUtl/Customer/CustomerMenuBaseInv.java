package imu.GS.ShopUtl.Customer;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.GS.ENUMs.TransactionAction;
import imu.GS.Invs.BuyCustomPriceINV;
import imu.GS.Invs.CustomerInv;
import imu.GS.Main.Main;
import imu.GS.Managers.ShopManager;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ItemPrice.PriceCustom;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;

public class CustomerMenuBaseInv extends CustomerInv
{
	ArrayList<ShopItemCustomer[]> _shopItemCustomer;
	//ShopItemCustomer[] _playerItemsOthers;
	ItemStack[] p_state_display = new ItemStack[2];
	
	int _player_slots_start = 36;
	int _shop_slot_start = 0;
	
	//PLAYER_INV_STATE p_state = PLAYER_INV_STATE.NORMAL;
	
	ItemStack empty_display;	
	int _playerInvPage = 0;
	int _shopInvPage = 0;
	ShopBase _shopBase;
	Main _main;
	ShopManager _sm;
	HashMap<Material, ArrayList<ShopItemCustomer>> players_materialCompares;
	int materialCompares_counter = 0;
	
	private PlayerTab _tab = PlayerTab.BLOCKS;
	private int index_tab = 0;
	
	boolean _lock_player_sell = false;
	
	BukkitTask _task_loadPlayerInv;
	
	public CustomerMenuBaseInv(Plugin main, Player player, ShopBase shopBase) {
		super(main, player, shopBase.GetNameWithColor(), 6*9);
		_main = (Main)main;
		_sm = _main.get_shopManager();
		_shopBase = shopBase;
		setupButtons();
		ResetPlayerShopItemList();
		
		LoadShopInv();
		LoadPlayerInv();
	}
	
	enum PlayerTab
	{
		BLOCKS,
		ARMOR_TOOLS
	}
	
	class ClickInfo
	{
		public BUTTON _button;
		public int _click_amount;
		public ClickType _clickType;
		public ShopItemBase _shopItemBase;
		public int _slot;
		public ClickInfo(BUTTON button,int slot ,int amount, ClickType cType, ShopItemBase shopItemBase) 
		{
			_button = button;
			_click_amount = amount;
			_clickType = cType;
			_shopItemBase = shopItemBase;
			_slot = slot;
		}
		
		public void Print()
		{
			System.out.println("============================");
			System.out.println("button: "+_button);
			System.out.println("click amount: "+_click_amount);
			System.out.println("click type: "+_clickType);
			System.out.println("_shopitemBase: "+_shopItemBase);
			if(_shopItemBase != null ) System.out.println("item: "+_shopItemBase.GetRealItem().getType());
			if(_shopItemBase != null ) System.out.println("item amount: "+_shopItemBase.GetRealItem().getAmount());
			System.out.println("slot: "+_slot);
			System.out.println("============================");
		}
	}

	protected enum BUTTON implements IButton
	{
		NONE,
		SHOP_ITEM,
		PLAYER_ITEM,
		GO_LEFT_SHOP,
		GO_RIGHT_SHOP,
		GO_LEFT_PLAYER,
		GO_RIGHT_PLAYER,
		STATE_PLAYER_INV;
	}
	
	
	
	void SwitchTab()
	{
		index_tab = (index_tab+1) >= PlayerTab.values().length ? 0 : index_tab+1;
		_tab = PlayerTab.values()[index_tab];
		System.out.println("tab idx: "+index_tab);
		_inv.setItem(31, p_state_display[index_tab]);
	}
	
	void ResetPlayerShopItemList()
	{
		players_materialCompares = new HashMap<>();
		_shopItemCustomer = new ArrayList<>();
		_shopItemCustomer.add(new ShopItemCustomer[18]);
	}
	
	@Override
	public void invClosed(InventoryCloseEvent e) 
	{
		UnRegisterItems();
		_shopBase.RemoveCustomer(_player.getUniqueId(), false);
		if(!_shopBase.HasCustomers()) 
		{
			_shopBase.ArrangeShopItems(true);
		}
			
		if(_task_loadPlayerInv != null) _task_loadPlayerInv.cancel();
	}

	public void onClickInsideInv(InventoryClickEvent e) 
	{
		if(_shopBase.HasInteractLock())
		{
			System.out.println("interact lock!");
			return;		
		}
			
		ItemStack stack = e.getCurrentItem();
		if(stack == null)
			return;
		String buttonName = getButtonName(e.getCurrentItem());
		if(buttonName == null)
			return;

		BUTTON button = BUTTON.valueOf(buttonName);
		
		ClickType c_type = e.getClick();
		int amount = 0;
		ShopItemBase si =  GetCustoemrItemViaSlot(e.getSlot());
		
		if(si == null) si = GetShopItemViaSlot(e.getSlot());
		
		//if(si == null) return;
		
		switch(c_type)
		{
			case LEFT:
				amount = 1;
				break;
			case RIGHT:
				amount = 8;
				break;
			case SHIFT_LEFT:
				amount = 64;
				break;
			case SHIFT_RIGHT:
				amount = si.Get_amount(); //all
				break;
		default:
			break;
		}
		
		if(si != null && amount > si.Get_amount())
			amount = si.Get_amount();
		
		
		ClickSorter(new ClickInfo(button,e.getSlot(),amount ,c_type, si));
	}

	@Override
	public void setupButtons() 
	{
		setupButton(BUTTON.GO_LEFT_SHOP, Material.DARK_OAK_SIGN, Metods.msgC("&9<< Shop"), 27);
		setupButton(BUTTON.GO_RIGHT_SHOP, Material.DARK_OAK_SIGN, Metods.msgC("&9Shop >>"), 35);
		setupButton(BUTTON.GO_LEFT_PLAYER, Material.BIRCH_SIGN, Metods.msgC("&9<< Inv"), 30);
		setupButton(BUTTON.GO_RIGHT_PLAYER, Material.BIRCH_SIGN, Metods.msgC("&9Inv >>"), 32);
		
		
		p_state_display[1] = _metods.hideAttributes(setupButton(BUTTON.STATE_PLAYER_INV, Material.NETHERITE_CHESTPLATE, Metods.msgC("&9Tools, Armor..."), 31));
		p_state_display[0] = setupButton(BUTTON.STATE_PLAYER_INV, Material.STONE, Metods.msgC("&9Blocks, Ores..."), 31);
		
		
		ItemStack redLine = Metods.setDisplayName(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ");
		_inv.setItem(28, redLine);_inv.setItem(29, redLine);_inv.setItem(35-1, redLine);_inv.setItem(35-2, redLine);
		
		redLine.setType(Material.BLUE_STAINED_GLASS_PANE);
		if(_shopBase.GetCustomersCanOnlyBuy()){for(int i = _size-1; i > _size-19; i--) {_inv.setItem(i, redLine);}}		
		
	}
	
	
	public void ClickSorter(ClickInfo cInfo)
	{
		//cInfo.Print();
		switch (cInfo._button) 
		{
		case GO_LEFT_PLAYER:
			ChangeCustomerPage(-1);
			RefreshPlayerDisplayPageSlots();
			break;			
		case GO_RIGHT_PLAYER:
			ChangeCustomerPage(1);
			RefreshPlayerDisplayPageSlots();
			break;
		case GO_LEFT_SHOP:
			//System.out.println("s go left");
			ChangeShopPage(-1);
			LoadShopInv();
			break;
		case GO_RIGHT_SHOP:
			//sSystem.out.println("s go right");
			ChangeShopPage(1);
			LoadShopInv();
			
			break;

		case PLAYER_ITEM:
			//System.out.println("player item: "+cInfo._click_amount);
			if(_lock_player_sell) return;
			Sell(cInfo);
			break;
		case SHOP_ITEM:
			Buy(cInfo);
			break;
		case STATE_PLAYER_INV:
			SwitchTab();
			LoadPlayerInv();
			break;
		default:
			break;				
		}
	
	}
	void Buy(ClickInfo cInfo)
	{
		//System.out.println("buy");
		if(cInfo._shopItemBase == null) return;
		
		//System.out.println("==> buy 11");
		if(cInfo._shopItemBase.Get_amount() <= 0)
			return;
		
		//System.out.println("==> buy 22");
		
		if(cInfo._shopItemBase.GetItemPrice() instanceof PriceCustom)
		{
			new BuyCustomPriceINV(_main, _player, _shopBase, (ShopItemSeller)cInfo._shopItemBase).openThis();
			return;
		}
		cInfo._shopItemBase.AddAmount(cInfo._click_amount * -1);
		cInfo._shopItemBase.UpdateItem();
		ShopItemCustomer sic = new ShopItemCustomer(_main,_shopBase ,_player,cInfo._shopItemBase.GetRealItem(), cInfo._click_amount);
		
		//FindCustomerItem(sic,true);
		ImusAPI._metods.InventoryAddItemOrDrop(cInfo._shopItemBase.GetRealItem(), _player, cInfo._click_amount);
		_main.get_shopManager().GetShopManagerSQL().LogPurchaseAsync(_player, sic, cInfo._click_amount, TransactionAction.BUY);
		LoadPlayerInv();
		
		
	}
	
	void Sell(ClickInfo cInfo)
	{
		//System.out.println("sell");
		if(cInfo._shopItemBase == null) return;
		
		if(cInfo._shopItemBase.Get_amount() <= 0)
			return;
		//System.out.println("==> sell 1");
		ShopItemSeller sis;
		sis = new ShopItemSeller(_main, _shopBase, cInfo._shopItemBase.GetRealItem(), cInfo._click_amount);
		((ShopItemCustomer)cInfo._shopItemBase).AddAmountToPlayer(cInfo._click_amount * -1);
		_shopBase.AddNewItem(sis,false);	
		cInfo._shopItemBase.UpdateItem();
		_main.get_shopManager().GetShopManagerSQL().LogPurchaseAsync(_player, sis, cInfo._click_amount, TransactionAction.SELL);
		
		
	}
	public void ChangeCustomerPage(int amount)
	{
		_playerInvPage += amount;
		if(_playerInvPage < 0)
			_playerInvPage = _shopItemCustomer.size()-1;
		
		if(_playerInvPage > _shopItemCustomer.size()-1)
			_playerInvPage = 0;
		
	}
	
	public void ChangeShopPage(int amount)
	{
		_shopInvPage += amount;
		if(_shopInvPage < 0)
			_shopInvPage = _shopBase.get_items().size()-1;
		
		if(_shopInvPage > _shopBase.get_items().size()-1)
			_shopInvPage = 0;
		
	}
	
	public void UpdateCustomerSlot(ShopItemCustomer sic,int page, int slot)
	{
		_shopItemCustomer.get(page)[slot] = sic;
		if(page != _playerInvPage)
			return;
		
		RefreshSlot(slot+_player_slots_start);
	}
	
	public void UpdateShopSlot(int page, int slot)
	{
		if(page != _shopInvPage)
			return;
		
		RefreshSlot(slot);
		
	}
	
	@Override
	public void SetShopSlot(ShopItemSeller sis, int page, int slot) {
		
		if(page != _shopInvPage)
			return;
		
		if(!sis.CanShowToPlayer(_player))
		{
			System.out.println("cant show item: "+slot);
			_inv.setItem(slot, SetButton(sis.GetDisplayItemNotAvailable(), BUTTON.NONE));
			//_inv.setItem(slot, null);
			return;
		}
		_inv.setItem(slot, SetButton(sis.GetDisplayItem(), BUTTON.SHOP_ITEM));
	}
	
	boolean CheckTabItem(ItemStack stack)
	{
		if(_tab == PlayerTab.BLOCKS && (ImusAPI._metods.isArmor(stack) || ImusAPI._metods.isTool(stack))) return false;
		if(_tab == PlayerTab.ARMOR_TOOLS)
		{
			if((ImusAPI._metods.isArmor(stack) || ImusAPI._metods.isTool(stack))) return true;
			return false;
		}

		return true;
	}
	
	public void LoadPlayerInv()
	{
		System.out.println("load player inv");
		if(_shopBase.GetCustomersCanOnlyBuy()) return;
		_lock_player_sell = true;
		CustomerMenuBaseInv inv = this;
		if(_task_loadPlayerInv != null) _task_loadPlayerInv.cancel();
		_task_loadPlayerInv = new BukkitRunnable() 
		{		
			@Override
			public void run() 
			{
				ResetPlayerShopItemList();
				
				for (ItemStack itemStack : _player.getInventory().getContents()) 
				{
					if(itemStack == null) continue;
						
					if(!CheckTabItem(itemStack)) continue;
					
					boolean found = false;

					for(int i = 0; i < _shopItemCustomer.size(); ++i) //page
					{
						for(int l = 0; l < _shopItemCustomer.get(i).length; ++l) //slot
						{
							ShopItemCustomer sic =  _shopItemCustomer.get(i)[l];
							if(sic == null)
							{					
								//new item found

								_shopItemCustomer.get(i)[l] = new ShopItemCustomer(_main,_shopBase,_player,itemStack, itemStack.getAmount());
								_shopItemCustomer.get(i)[l].AddPlayerItemStackRef("loadINV1", itemStack);
								_shopItemCustomer.get(i)[l].RegisterSlot(_inv, inv,i, l, false);

								found = true;
								break;
							}
							
							if(sic.IsSameKind(itemStack))
							{
								sic.AddAmount(itemStack.getAmount());
								sic.AddPlayerItemStackRef("loadInv2",itemStack);
								found = true;
								break;
							}
						}
						if(found)
							break;
					}
					
					if(!found)
					{
						//System.out.println("MAKE2 new page for customer!");
						_shopItemCustomer.add(new ShopItemCustomer[18]);
						int page = _shopItemCustomer.size()-1;
						int slot = 0;
						_shopItemCustomer.get(page)[slot] = new ShopItemCustomer(_main,_shopBase, _player, itemStack, itemStack.getAmount());
						_shopItemCustomer.get(page)[slot].AddPlayerItemStackRef("loadINV3", itemStack);
						_shopItemCustomer.get(page)[slot].RegisterSlot(_inv, inv, page, slot, false);
					}
					
				}
				
				RefreshPlayerDisplayPageSlots();
				_lock_player_sell = false;
				_task_loadPlayerInv = null;
			}
		}.runTaskAsynchronously(_main);
		
		//System.out.println("PlayerInvLoaded");
		
	}
//	void FindCustomerIte(ShopItemCustomer sic, boolean AddToPlayerToo)
//	{
//		
//		int[] freeSlots = null;
//		for(int page = 0; page < _shopItemCustomer.size(); ++page)
//		{
//			for(int i = 0; i < _shopItemCustomer.get(page).length; ++i)
//			{
//				ShopItemCustomer customerItem = _shopItemCustomer.get(page)[i];
//				if(customerItem == null)
//				{
//					if(freeSlots == null)
//						freeSlots = new int[] {page,i};
//					continue;
//				}
//									
//				if(customerItem.IsSameKind(sic))
//				{
//					if(AddToPlayerToo)
//					{
//						customerItem.AddAmountToPlayer(sic.Get_amount());
//					}
//					else
//					{
//						customerItem.AddAmount(sic.Get_amount());
//						customerItem.AddPlayerItemStackRef("new", sic.GetRealItem());
//					}
//					
//					customerItem.UpdateItem();
//					return;
//				}
//				
//			}
//		}
//		//jos ei oo spacii
//		if(freeSlots == null)
//		{
//			System.out.println("MAKE new page for customer!");
//			_shopItemCustomer.add(new ShopItemCustomer[18]);
//			freeSlots = new int[] {_shopItemCustomer.size()-1,0};
//		}
//			
//		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]] = new ShopItemCustomer(_main, _shopBase,_player,sic.GetRealItem(), 0);
//		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]].RegisterSlot(_inv, this,freeSlots[0], freeSlots[1], false);
//
//		System.out.println("adding to player as NEW: "+sic.Get_amount());
//		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]].AddAmountToPlayer(sic.Get_amount());
//		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]].UpdateItem();
//	}
	
	
	ShopItemSeller GetShopItemViaSlot(int slot)
	{
		Integer ref_page = _sm.GetSISPage(_inv.getItem(slot));
		Integer ref_slot = _sm.GetSISSlot(_inv.getItem(slot));	

		if(ref_page == null || ref_slot == null) return null;
		System.out.format("page %d", ref_page);
		System.out.format("slot %d", ref_slot);
		return (ShopItemSeller)_shopBase.GetItem(ref_page, ref_slot);
	}
	
	ShopItemCustomer GetCustoemrItemViaSlot(int slot)
	{
		if(slot >= _player_slots_start && slot <_size)
		{
			return _shopItemCustomer.get(_playerInvPage)[slot-_player_slots_start];						
		}
		return null;
	}
	
	void RefreshSlot(int slot)
	{
		if(slot >= 0 &&  slot <= 27)
		{			
			ShopItemSeller sis = GetShopItemViaSlot(slot);
			if(sis == null )//|| !sis.CanShowToPlayer(_player))
			{
				_inv.setItem(slot, null);
				return;
			}
			
			if(!sis.CanShowToPlayer(_player))
			{
				_inv.setItem(slot, sis.GetDisplayItemNotAvailable());
				return;
			}
			//_inv.setItem(slot, SetButton(_shopBase.get_items().get(_shopInvPage)[slot].GetDisplayItem(), BUTTON.SHOP_ITEM));
			SetShopSlot(sis, _shopInvPage, slot);
		}
		if(slot >= _player_slots_start && slot < _size)
		{
			if(_shopItemCustomer.get(_playerInvPage)[slot-_player_slots_start] == null)
			{
				_inv.setItem(slot, null);
				return;
			}				
			_inv.setItem(slot, SetButton(_shopItemCustomer.get(_playerInvPage)[slot-_player_slots_start].GetDisplayItem(), BUTTON.PLAYER_ITEM));
		}
	}
	void AddItemToCustomer(ShopItemBase shopItemBase, int amount)
	{
		if(shopItemBase.Get_amount() <= amount)
			amount = shopItemBase.Get_amount();
		
		
	}
	
	public void LoadShopInv()
	{		
		System.out.println("load shop inv");
		int idx = 0;
		for(int i = 0; i < _shopBase.get_items().get(_shopInvPage).length; ++i)
		{
			ShopItemSeller sis = _shopBase.get_items().get(_shopInvPage)[i];
			if(sis == null)// || !sis.CanShowToPlayer(_player))
			{
				//UpdateShopSlot(_shopInvPage, i);		
				if(!_shopBase.IsAbsoluteItemPositions()) idx--;
				continue;
			}
			
			if(!sis.CanShowToPlayer(_player) || !_shopBase.IsAbsoluteItemPositions()) // => tollon näkyy ettei available//if(!sis.CanShowToPlayer(_player) && !_shopBase.IsAbsoluteItemPositions()) 
			{
				idx--;
				continue;
			}
		
			sis.RegisterSlot(_inv, this, _shopInvPage, i+idx, true);
			//_inv.setItem(i+idx, sis.GetDisplayItem());
			
			sis.UpdateItem();
			//UpdateShopSlot(_shopInvPage, i);
		}
		
	}
	
	void UnRegisterItems()
	{		
		//System.out.println("Unregister items!");
		for(int i = 0; i < _shopItemCustomer.size(); ++i)
		{
			for(int l = 0; l < _shopItemCustomer.get(i).length; ++l)
			{
				if(_shopItemCustomer.get(i)[l] == null)
					continue;
				_shopItemCustomer.get(i)[l].UnRegisterSlot(_inv);
			}
		}
		
		_shopBase.UnRegisterItems(_inv);
		
	}
	
	void RefreshPlayerDisplayPageSlots()
	{
		for(int i = 0; i < _shopItemCustomer.get(_playerInvPage).length; ++i) //page
		{
			RefreshSlot(i+_player_slots_start);
		}
	}

	
	


}

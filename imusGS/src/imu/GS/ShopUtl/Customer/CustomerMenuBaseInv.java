package imu.GS.ShopUtl.Customer;


import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.GS.ENUMs.TransactionAction;
import imu.GS.Invs.BuyCustomPriceINV;
import imu.GS.Invs.CustomerInv;
import imu.GS.Main.Main;
import imu.GS.Managers.ShopManager;
import imu.GS.Managers.UniqueManager;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ShopItemResult;
import imu.GS.ShopUtl.ItemPrice.ItemPrice;
import imu.GS.ShopUtl.ItemPrice.PriceCustom;
import imu.GS.ShopUtl.ItemPrice.PriceMaterial;
import imu.GS.ShopUtl.ItemPrice.PriceUnique;
import imu.GS.ShopUtl.ShopItems.ShopItemCustomerShulkerBox;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;

public class CustomerMenuBaseInv extends CustomerInv
{
	ArrayList<ShopItemCustomer[]> _shopItemCustomer;
	//ShopItemCustomer[] _playerItemsOthers;
	ItemStack[] p_state_display = new ItemStack[3];
	ItemStack[] p_state_display_hotbar_armor = new ItemStack[2];
	
	final int _player_slots_start = 36;
	final int _shop_slot_start = 0;
	
	//PLAYER_INV_STATE p_state = PLAYER_INV_STATE.NORMAL;
	final double _cdButtonClick = 0.3;
	int _playerInvPage = 0;
	int _shopInvPage = 0;
	ShopBase _shopBase;
	Main _main;
	ShopManager _sm;
	UniqueManager _uniqueManager;
	int materialCompares_counter = 0;
	
	private PlayerTab _tab = PlayerTab.BLOCKS;
	private int index_tab = 0;
	
	boolean _lock_player_sell = false;
	private boolean _transaction_inprogress = false;
	private boolean _includePlayerHotbarAndArmorslots = false;
	
	BukkitTask _task_loadPlayerInv;
	
	Cooldowns _cd = new Cooldowns();
	
	public CustomerMenuBaseInv(Plugin main, Player player, ShopBase shopBase) {
		super(main, player, shopBase.GetNameWithColor(), 6*9);
		_main = (Main)main;
		_sm = _main.get_shopManager();
		_uniqueManager = _sm.GetUniqueManager();
		_shopBase = shopBase;
		setupButtons();
		ResetPlayerShopItemList();
		
		LoadShopInv();
		LoadPlayerInv();
	}
	
	enum PlayerTab
	{
		BLOCKS,
		ARMOR_TOOLS,
		SHULKER_BOXES
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
		STATE_PLAYER_INV,
		STATE_HOTBAR_ARMOR_SLOTS,
	}
	
	
	
	void SwitchTab()
	{
		index_tab = (index_tab+1) >= PlayerTab.values().length ? 0 : index_tab+1;
		_tab = PlayerTab.values()[index_tab];
		//System.out.println("tab idx: "+index_tab);
		_inv.setItem(31, p_state_display[index_tab]);
	}
	
	void SwitchStateHotbarArmorSlots()
	{
		if(_includePlayerHotbarAndArmorslots)
		{
			_includePlayerHotbarAndArmorslots = false;
			_inv.setItem(33, p_state_display_hotbar_armor[0]);
		}
		else
		{
			_includePlayerHotbarAndArmorslots = true;
			_inv.setItem(33, p_state_display_hotbar_armor[1]);
		}
		
	}
	
	void ResetPlayerShopItemList()
	{
		_shopItemCustomer = new ArrayList<>();
		_shopItemCustomer.add(new ShopItemCustomer[18]);
	}
	
	@EventHandler
	public void ReceivedDrop(EntityPickupItemEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			if(((Player)e.getEntity()).getUniqueId().equals(_player.getUniqueId()))
			{
				LoadPlayerInv();
			}		
		}
	}
	
	@Override
	public void invClosed(InventoryCloseEvent e) 
	{
		UnRegisterItems();
		_shopBase.RemoveCustomer(_player.getUniqueId(), false);
		if(!_shopBase.HasCustomers()) 
		{
			_shopBase.ArrangeShopItems(true);
			_shopBase.SaveIfPossible();
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
		
		if(_transaction_inprogress) 
		{
			System.out.println("trans action in progress");
			return;
		}
		
		
		if(_shopBase._temp_lock) 
		{
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
		ItemStack redLine = Metods.setDisplayName(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ");
		_inv.setItem(28, redLine);_inv.setItem(29, redLine);_inv.setItem(35-1, redLine);
		
		setupButton(BUTTON.GO_LEFT_SHOP, Material.DARK_OAK_SIGN, Metods.msgC("&9<< Shop"), 27);
		setupButton(BUTTON.GO_RIGHT_SHOP, Material.DARK_OAK_SIGN, Metods.msgC("&9Shop >>"), 35);
		setupButton(BUTTON.GO_LEFT_PLAYER, Material.BIRCH_SIGN, Metods.msgC("&9<< Inv"), 30);
		setupButton(BUTTON.GO_RIGHT_PLAYER, Material.BIRCH_SIGN, Metods.msgC("&9Inv >>"), 32);
		p_state_display_hotbar_armor[1] = setupButton(BUTTON.STATE_HOTBAR_ARMOR_SLOTS, Material.SHIELD, Metods.msgC("&9Hotbar and Armor slots &2Included!"), 33);
		p_state_display_hotbar_armor[0] = setupButton(BUTTON.STATE_HOTBAR_ARMOR_SLOTS, Material.CHEST, Metods.msgC("&9Hotbar and Armor slots &cExcluded!"), 33);
		
		
		p_state_display[2] = _metods.hideAttributes(setupButton(BUTTON.STATE_PLAYER_INV, Material.CYAN_SHULKER_BOX, Metods.msgC("&9CONTENT OF SHULKER BOXES"), 31));
		p_state_display[1] = _metods.hideAttributes(setupButton(BUTTON.STATE_PLAYER_INV, Material.NETHERITE_CHESTPLATE, Metods.msgC("&9Tools, Armor..."), 31));
		p_state_display[0] = setupButton(BUTTON.STATE_PLAYER_INV, Material.STONE, Metods.msgC("&9Blocks, Ores..."), 31);
		
		
		
		
		redLine.setType(Material.BLUE_STAINED_GLASS_PANE);
		if(_shopBase.GetCustomersCanOnlyBuy()){for(int i = _size-1; i > _size-19; i--) {_inv.setItem(i, redLine);}}		
		
	}
	
	
	@SuppressWarnings("incomplete-switch")
	public void ClickSorter(ClickInfo cInfo)
	{
		//cInfo.Print();
		switch (cInfo._button) 
		{
		case PLAYER_ITEM:
			//System.out.println("player item: "+cInfo._click_amount);
			if(_lock_player_sell) return;
			PrepareSell(cInfo);
			return;
		case SHOP_ITEM:
			//Buy(cInfo);
			PrepareBuy(cInfo);
			return;
		
				
		}
		if(!_cd.isCooldownReady("buttonPress")) return;
		
		switch (cInfo._button) 
		{
		case STATE_PLAYER_INV:
			SwitchTab();
			LoadPlayerInv();
			break;
		case NONE:
			break;			
		case GO_LEFT_PLAYER:
			ChangeCustomerPage(-1);
			RefreshPlayerDisplayPageSlots();
			break;			
		case GO_RIGHT_PLAYER:
			ChangeCustomerPage(1);
			RefreshPlayerDisplayPageSlots();
			break;
		case GO_LEFT_SHOP:
			ChangeShopPage(-1);
			LoadShopInv();
			break;
		case GO_RIGHT_SHOP:
			ChangeShopPage(1);
			LoadShopInv();
			break;
		case STATE_HOTBAR_ARMOR_SLOTS:
			SwitchStateHotbarArmorSlots();
			LoadPlayerInv();
			break;
		}
		
		_cd.setCooldownInSeconds("buttonPress", _cdButtonClick);
	
	}
	
	void PrepareBuy(ClickInfo cInfo)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				_transaction_inprogress = true;
				Transaction();
				_transaction_inprogress = false;
			}
			
			void Transaction()
			{
				if(cInfo._shopItemBase == null) return;
				
				//System.out.println("==> buy 11");
				if(cInfo._shopItemBase.Get_amount() <= 0)
					return;
				
				if(cInfo._shopItemBase.GetItemPrice() instanceof PriceCustom)
				{
					new BukkitRunnable() {
						
						@Override
						public void run() 
						{
							new BuyCustomPriceINV(_main, _player, _shopBase, (ShopItemSeller)cInfo._shopItemBase).openThis();
						}
					}.runTask(_main);
					return;
				}
				
				if(_shopBase.BuyConfirmation(_player, cInfo._shopItemBase, cInfo._click_amount))
				{
					Buy(cInfo);
				}
			}
		}.runTaskAsynchronously(_main);
	}
	
	void PrepareSell(ClickInfo cInfo)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				_transaction_inprogress = true;
				Transaction();
				_transaction_inprogress = false;
			}
			
			void Transaction()
			{
				if(cInfo._shopItemBase == null) return;
				
				if(cInfo._shopItemBase.Get_amount() <= 0)
					return;
				
				if(!((ShopItemCustomer)cInfo._shopItemBase).EnoughItems(cInfo._click_amount)) //!ConfirmPlayerHasEnoughItems(_player, cInfo._shopItemBase, cInfo._click_amount)
				{
					System.out.println(_player.getName()+" didnt have enough items!");
					_player.sendMessage(Metods.msgC("&cSomething went wrong with transaction. Please try again. If this message shows up. Inform it for admin!")); 
					// if this happens player doesnt have enough items in inventory
					LoadPlayerInv();
					return;
				}
				
				if(_shopBase.SellConfirmation(_player, cInfo._shopItemBase, cInfo._click_amount))
				{
					Sell(cInfo);
				}
				else
				{
					System.out.println("sell isnt confirmed");
				}
			}
		}.runTaskAsynchronously(_main);
	}
	
	void Buy(ClickInfo cInfo)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				ShopItemResult[] resultItems = cInfo._shopItemBase.GetTransactionResultItemStack();
				cInfo._shopItemBase.AddAmount(cInfo._click_amount * -1);
				cInfo._shopItemBase.UpdateItem();
				ShopItemCustomer sic = new ShopItemCustomer(_main,_shopBase ,_player,resultItems[0]._stack, cInfo._click_amount);
				
				//FindCustomerItem(sic,true);
				ImusAPI._metods.InventoryAddItemOrDrop(resultItems[0]._stack, _player, cInfo._click_amount);
				
				//_main.get_shopManager().GetShopManagerSQL().LogPurchaseAsync(_player, sic, cInfo._click_amount, TransactionAction.BUY); // sic have to change in future if resultItem will be null!
				//_main.get_shopManager().GetShopManagerSQL().ShopItemAddUpdateAsync((ShopItemSeller)cInfo._shopItemBase, cInfo._click_amount * -1);
				LoadPlayerInv();
			}
		}.runTask(_main);
			
	}
	
	void Sell(ClickInfo cInfo)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				ShopItemResult[] resultItems = cInfo._shopItemBase.GetTransactionResultItemStack();
				((ShopItemCustomer)cInfo._shopItemBase).AddAmountToPlayer(cInfo._click_amount * -1);
				if(!ImusAPI._metods.isShulkerBox(cInfo._shopItemBase.GetRealItem()))
				{
					AddItem(resultItems[0]._stack, cInfo._click_amount);		
					
					//_main.get_shopManager().GetShopManagerSQL().ShopItemAddUpdateAsync(sis, cInfo._click_amount);
				}
				else
				{
					for(ShopItemResult result : resultItems)
					{
						AddItem(result._stack, result._amount);
					}
				}
				
				cInfo._shopItemBase.UpdateItem();
							
			}
			
			void AddItem(ItemStack stack, int amount)
			{
				ShopItemSeller sis;
				sis = new ShopItemSeller(_main, _shopBase, stack, amount);
				if(_uniqueManager.IsUnique(stack))
				{
					sis.SetItemPrice(new PriceUnique().SetPrice(_main.get_shopManager().GetUniqueManager().GetPriceItem(stack).GetPrice()));
				}else
				{
					sis.SetItemPrice(_main.get_shopManager().GetPriceMaterialAndCheck(stack));
				}
				
				_shopBase.AddNewItem(sis,false);
				
				//_main.get_shopManager().GetShopManagerSQL().LogPurchaseAsync(_player, new ShopItemSeller(_main, _shopBase, stack, cInfo._click_amount), amount, TransactionAction.SELL);
			}
		}.runTask(_main);
		
		
		
	}
	
//	boolean ConfirmPlayerHasEnoughItems(Player player, ShopItemBase sib, int amountNeeded)
//	{
//		int count = 0;
//		ShopItemCustomer sic = (ShopItemCustomer)sib;
//		sic.
//		for(ItemStack stack : player.getInventory().getContents())
//		{
//			if(stack != null && stack.isSimilar(sib.GetRealItem()))
//			{
//				count += stack.getAmount();
//			}
//			if(count >= amountNeeded) return true;
//		}
//		return false;
//	}
	public void ChangeCustomerPage(int amount)
	{
		_playerInvPage += amount;
		if(_playerInvPage < 0)
			_playerInvPage = 0;
		
		if(_playerInvPage > _shopItemCustomer.size()-1)
			_playerInvPage = _shopItemCustomer.size()-1;
		
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
		if(_tab == PlayerTab.BLOCKS && (ImusAPI._metods.isArmor(stack) || ImusAPI._metods.isTool(stack) || ImusAPI._metods.isShulkerBox(stack))) return false;
		if(_tab == PlayerTab.ARMOR_TOOLS)
		{
			if((ImusAPI._metods.isArmor(stack) || ImusAPI._metods.isTool(stack)) && ImusAPI._metods.getDurabilityProsent(stack) == 1.0) return true;
			return false;
		}
		
		if(_tab == PlayerTab.SHULKER_BOXES && !ImusAPI._metods.isShulkerBox(stack)) return false;
		
		return true;
	}
	
	
	void PutCustomerItem(ItemStack stack, int page, int slot)
	{
		ShopItemCustomer sis;
		ItemPrice itemPrice;
		if(_tab != PlayerTab.SHULKER_BOXES)
		{
			sis = new ShopItemCustomer(_main,_shopBase,_player,stack, stack.getAmount());
			sis.AddPlayerItemStackRef(stack);
			sis.RegisterSlot(_inv, this, page, slot, false);

			if(_uniqueManager.IsUnique(stack))
			{
				itemPrice = new PriceMaterial();
				itemPrice.SetPrice(_uniqueManager.GetPriceItem(stack).GetPrice());
				//System.out.println("stack is unique: "+itemPrice.GetPrice());
			}else
			{
				itemPrice = _main.get_shopManager().GetPriceMaterialAndCheck(stack);
			}
			sis.SetItemPrice(itemPrice);
			_shopItemCustomer.get(page)[slot] = sis;
			return;
		}
		
		sis = new ShopItemCustomerShulkerBox(_main, _shopBase, _player, stack, stack.getAmount());
		sis.AddPlayerItemStackRef(stack);
		sis.RegisterSlot(_inv, this, page, slot, false);
		itemPrice = ((ShopItemCustomerShulkerBox)sis).GetCalculatedPriceFromContent();
		
		if(itemPrice.GetPrice() <= 0) return;
		
		((ShopItemCustomerShulkerBox)sis).SetItemPrice(itemPrice);
		
		_shopItemCustomer.get(page)[slot] = sis;
	}
	
	
	public void LoadPlayerInv()
	{
		//System.out.println("load player inv");
		if(_shopBase.GetCustomersCanOnlyBuy()) return;
		_lock_player_sell = true;
		if(_task_loadPlayerInv != null) _task_loadPlayerInv.cancel();
		
		_task_loadPlayerInv = new BukkitRunnable() 
		{		
			@Override
			public void run() 
			{
				ResetPlayerShopItemList();
				ItemStack itemStack;
				for (int invSlot = 0; invSlot <  _player.getInventory().getContents().length ; invSlot++) 
				{
					if(!_includePlayerHotbarAndArmorslots && (invSlot < 9  || invSlot > _player.getInventory().getContents().length-6)) continue;
					
					itemStack = _player.getInventory().getContents()[invSlot];
					
					if(itemStack == null) continue;
						
					if(!CheckTabItem(itemStack)) continue;
					
					//if(_tab == PlayerTab.SHULKER_BOXES) 
//					ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(Arrays.asList(itemStack)) ;
//					
//					for(ItemStack stack : stacks)
//					{
//						
//					}
					
					boolean found = false;

					for(int i = 0; i < _shopItemCustomer.size(); ++i) //page
					{
						for(int l = 0; l < _shopItemCustomer.get(i).length; ++l) //slot
						{
							ShopItemCustomer sic =  _shopItemCustomer.get(i)[l];
							if(sic == null)
							{					
								//new item found
								
								
								PutCustomerItem(itemStack, i, l);

								found = true;
								break;
							}
							
							if(_tab != PlayerTab.SHULKER_BOXES && sic.IsSameKind(itemStack))
							{
								sic.AddAmount(itemStack.getAmount());
								sic.AddPlayerItemStackRef(itemStack);
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
//						_shopItemCustomer.get(page)[slot] = new ShopItemCustomer(_main,_shopBase, _player, itemStack, itemStack.getAmount());
//						_shopItemCustomer.get(page)[slot].AddPlayerItemStackRef("loadINV3", itemStack);
//						_shopItemCustomer.get(page)[slot].RegisterSlot(_inv, inv, page, slot, false);
						PutCustomerItem(itemStack, page, slot);
					}
					
				}
				
				ChangeCustomerPage(0);
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
//		System.out.format("page %d", ref_page);
//		System.out.format("slot %d", ref_slot);
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
		//System.out.println("load shop inv");
		int idx = 0;
		for(int i = 0; i < _shopBase.get_items().get(_shopInvPage).length; ++i)
		{
			ShopItemSeller sis = _shopBase.get_items().get(_shopInvPage)[i];
			if(sis == null)// || !sis.CanShowToPlayer(_player))
			{
				//UpdateShopSlot(_shopInvPage, i);		
				if(!_shopBase.IsAbsoluteItemPositions()) idx--;
				_inv.setItem(i, null);
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
			
			//System.out.println("loading inv: "+sis.GetRealItem().getType()+ " price type: "+sis.GetItemPrice());
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

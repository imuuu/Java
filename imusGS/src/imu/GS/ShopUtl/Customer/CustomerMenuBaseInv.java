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

import imu.GS.Invs.ShopUI.PLAYER_INV_STATE;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ShopItemSeller;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.CustomInvLayout;

public class CustomerMenuBaseInv extends CustomInvLayout
{


	ArrayList<ShopItemCustomer[]> _shopItemCustomer = new ArrayList<>();
	
	//ShopItemCustomer[] _playerItemsOthers;
	ItemStack[] p_state_display = new ItemStack[2];
	
	int _player_slots_start = 36;
	int _shop_slot_start = 0;
	
	PLAYER_INV_STATE p_state = PLAYER_INV_STATE.NORMAL;
	
	ItemStack empty_display;

	
	int _playerInvPage = 0;
	int _shopInvPage = 0;
	ShopBase _shopBase;
	
	HashMap<Material, ArrayList<ShopItemCustomer>> players_materialCompares= new HashMap<>();
	int materialCompares_counter = 0;
	public CustomerMenuBaseInv(Plugin main, Player player, ShopBase shopBase) {
		super(main, player, shopBase.GetNameWithColor(), 6*9);
		_shopBase = shopBase;
		setupButtons();
		_shopItemCustomer.add(new ShopItemCustomer[18]);
		
		LoadShopInv();
		LoadPlayerInv();
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
	@Override
	public void invClosed(InventoryCloseEvent arg0) 
	{
		UnRegisterItems();
	}

	public void onClickInsideInv(InventoryClickEvent e) 
	{
		ItemStack stack = e.getCurrentItem();
		if(stack == null)
			return;
		
		String buttonName = getButtonName(e.getCurrentItem());
		if(buttonName == null)
			return;
		
		BUTTON button = BUTTON.valueOf(buttonName);
		
		ClickType c_type = e.getClick();
		int amount = 0;
		ShopItemBase si = GetItem(e.getSlot());
		
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
//	
//	@EventHandler
//	public void OnPickUp(EntityPickupItemEvent event)
//	{
//		System.out.println("entity pickup");
//		LoadPlayerInv();
//	}
//	
	@Override
	public void setupButtons() 
	{
		setupButton(BUTTON.GO_LEFT_SHOP, Material.DARK_OAK_SIGN, _metods.msgC("&9<< Shop"), 27);
		setupButton(BUTTON.GO_RIGHT_SHOP, Material.DARK_OAK_SIGN, _metods.msgC("&9Shop >>"), 35);
		setupButton(BUTTON.GO_LEFT_PLAYER, Material.BIRCH_SIGN, _metods.msgC("&9<< Inv"), 30);
		setupButton(BUTTON.GO_RIGHT_PLAYER, Material.BIRCH_SIGN, _metods.msgC("&9Inv >>"), 32);
		
		p_state_display[1] = setupButton(BUTTON.STATE_PLAYER_INV, Material.STONE, _metods.msgC("&9Blocks, Ores..."), 31);
		p_state_display[0] = _metods.hideAttributes(setupButton(BUTTON.STATE_PLAYER_INV, Material.NETHERITE_CHESTPLATE, _metods.msgC("&9Tools, Armor..."), 31));
		
		
		ItemStack redLine = _metods.setDisplayName(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ");
		_inv.setItem(28, redLine);_inv.setItem(29, redLine);_inv.setItem(35-1, redLine);_inv.setItem(35-2, redLine);
	}
	
	
	public void ClickSorter(ClickInfo cInfo)
	{
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
			System.out.println("s go left");
			ChangeShopPage(-1);
			LoadShopInv();
			break;
		case GO_RIGHT_SHOP:
			System.out.println("s go right");
			ChangeShopPage(1);
			LoadShopInv();
			
			break;

		case PLAYER_ITEM:
			System.out.println("player item: "+cInfo._click_amount);
			ShopItemSeller sis = new ShopItemSeller(cInfo._shopItemBase.GetRealItem(), cInfo._click_amount);
			((ShopItemCustomer)cInfo._shopItemBase).AddAmountToPlayer(cInfo._click_amount * -1);
			_shopBase.AddNewItem(sis);	
			RefreshSlot(cInfo._slot);
			break;
		case SHOP_ITEM:
			_shopBase.RemoveItem(_shopInvPage, cInfo._slot, cInfo._click_amount * -1);
			ShopItemCustomer sic = new ShopItemCustomer(_player,cInfo._shopItemBase.GetRealItem(), cInfo._click_amount);
			FindCustomerItem(sic,true);
			
			
			break;
		case STATE_PLAYER_INV:
			System.out.println("p state");
			break;
		default:
			break;				
		}
	
	}
	
	public void ChangeCustomerPage(int amount)
	{
		_playerInvPage += amount;
		if(_playerInvPage < 0)
			_playerInvPage = _shopItemCustomer.size()-1;
		
		if(_playerInvPage > _shopItemCustomer.size()-1)
			_playerInvPage = 0;
		
		System.out.println("Player page: "+ _playerInvPage);
	}
	
	public void ChangeShopPage(int amount)
	{
		_shopInvPage += amount;
		if(_shopInvPage < 0)
			_shopInvPage = _shopBase.get_items().size()-1;
		
		if(_shopInvPage > _shopBase.get_items().size()-1)
			_shopInvPage = 0;
		
		System.out.println("Shop page: "+ _playerInvPage);
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
	
	void LoadPlayerInv()
	{
		players_materialCompares = new HashMap<>();
		
		for (ItemStack itemStack : _player.getInventory().getContents()) 
		{
			if(itemStack == null)
				continue;

			boolean found = false;
//			ShopItemCustomer sic = new ShopItemCustomer(_player,itemStack, itemStack.getAmount());
//			FindCustomerItem(sic,false);
			for(int i = 0; i < _shopItemCustomer.size(); ++i) //page
			{
				for(int l = 0; l < _shopItemCustomer.get(i).length; ++l) //slot
				{
					ShopItemCustomer sic =  _shopItemCustomer.get(i)[l];
					if(sic == null)
					{					
						//new item found

						_shopItemCustomer.get(i)[l] = new ShopItemCustomer(_player,itemStack, itemStack.getAmount());
						_shopItemCustomer.get(i)[l].RegisterSlot(_inv, this,i, l, false);

						found = true;
						break;
					}
					
					if(sic.IsSameKind(itemStack))
					{
						sic.AddAmount(itemStack.getAmount());
						sic.AddPlayerItemStackRef("loadInv",itemStack);
						found = true;
						break;
					}
				}
				if(found)
					break;
			}
			
			if(!found)
			{
				System.out.println("MAKE2 new page for customer!");
				_shopItemCustomer.add(new ShopItemCustomer[18]);
				int page = _shopItemCustomer.size()-1;
				int slot = 0;
				_shopItemCustomer.get(page)[slot] = new ShopItemCustomer(_player, itemStack, itemStack.getAmount());
				_shopItemCustomer.get(page)[slot].RegisterSlot(_inv, this, page, slot, false);
			}
			
		}
		
		RefreshPlayerDisplayPageSlots();
		
		System.out.println("PlayerInvLoaded");
		
	}
	void FindCustomerItem(ShopItemCustomer sic, boolean AddToPlayerToo)
	{
		int[] freeSlots = null;
		for(int page = 0; page < _shopItemCustomer.size(); ++page)
		{
			for(int i = 0; i < _shopItemCustomer.get(page).length; ++i)
			{
				ShopItemCustomer customerItem = _shopItemCustomer.get(page)[i];
				if(customerItem == null)
				{
					if(freeSlots == null)
						freeSlots = new int[] {page,i};
					continue;
				}
									
				if(customerItem.IsSameKind(sic))
				{
					if(AddToPlayerToo)
					{
						customerItem.AddAmountToPlayer(sic.Get_amount());
					}else
					{
						customerItem.AddAmount(sic.Get_amount());
						customerItem.AddPlayerItemStackRef("new", sic.GetRealItem());
					}
					
					customerItem.UpdateItem();
					return;
				}
				
			}
		}
		//jos ei oo spacii
		if(freeSlots == null)
		{
			System.out.println("MAKE new page for customer!");
			_shopItemCustomer.add(new ShopItemCustomer[18]);
			freeSlots = new int[] {_shopItemCustomer.size()-1,0};
		}
			
		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]] = new ShopItemCustomer(_player, sic.GetRealItem(), 0);
		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]].RegisterSlot(_inv, this,freeSlots[0], freeSlots[1], false);

		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]].AddAmountToPlayer(sic.Get_amount());
		_shopItemCustomer.get(freeSlots[0])[freeSlots[1]].UpdateItem();
	}
	
	
	void RefreshSlot(int slot)
	{
		if(slot >= 0 &&  slot <= 27)
		{
			if(_shopBase.get_items().get(_shopInvPage)[slot] == null)
			{
				_inv.setItem(slot, null);
				return;
			}
			_inv.setItem(slot, SetButton(_shopBase.get_items().get(_shopInvPage)[slot].GetDisplayItem(), BUTTON.SHOP_ITEM));
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
		
		for(int i = 0; i < _shopBase.get_items().get(_shopInvPage).length; ++i)
		{
			ShopItemSeller sis = _shopBase.get_items().get(_shopInvPage)[i];
			if(sis == null)
			{
				UpdateShopSlot(_shopInvPage, i);
				continue;
			}
			sis.RegisterSlot(_inv, this, _shopInvPage, i, true);
			UpdateShopSlot(_shopInvPage, i);
		}
		
	}
	
	void UnRegisterItems()
	{		
		System.out.println("Unregister items!");
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
	
	
	
	protected ShopItemBase GetItem(int slot)
	{
		if(slot >= 0 && slot < 27) //shopside
		{
			return _shopBase.GetItem(_shopInvPage, slot);
		}
		
		if(slot >= _player_slots_start && slot <_size)
		{
			if(p_state == PLAYER_INV_STATE.NORMAL)
			{
				return _shopItemCustomer.get(_playerInvPage)[slot-_player_slots_start];
			}
			
//			if(p_state == PLAYER_INV_STATE.OTHER_STUFF)
//			{
//				return _shopItemCustomer[_playerInvPage][slot-_player_slots_start];
//			}
			
		}
		return null;
	}
	
//	void AddItemStackMaterialCompares(ItemStack itemStack)
//	{
//		if(players_materialCompares.containsKey(itemStack.getType()))
//		{			
//			for(ShopItemCustomer sic : players_materialCompares.get(itemStack.getType()))
//			{
//				if(sic.IsSameKind(itemStack))
//				{
//					sic.AddAmount(itemStack.getAmount());
//					sic.AddPlayerItemStackRef(itemStack);
//					return;
//				}
//			}			
//		}
//		
//		ShopItemCustomer sic = new ShopItemCustomer(materialCompares_counter++,itemStack,itemStack.getAmount());
//		sic.AddPlayerItemStackRef(itemStack);
//		if(players_materialCompares.containsKey(itemStack.getType()))
//		{
//			players_materialCompares.get(itemStack.getType()).add(sic);
//			
//		}else
//		{
//			ArrayList<ShopItemCustomer> ar = new ArrayList<ShopItemCustomer>();
//			ar.add(sic);
//			players_materialCompares.put(itemStack.getType(), ar);
//		}	
//	}
	

	
	
//	final void loadPlayerInv() 
//	{
//		HashMap<Material, ShopItem> mats_i = new HashMap<>();
//		//HashMap<Integer, Material> i_mats = new HashMap<>();
//		_player_inv_refs = new HashMap<>();
//		
//		ItemStack[] content = _player.getInventory().getContents();
//		for(int i = 0; i < content.length ;++i)
//		{
//			ItemStack s = content[i];
//			
//			if(s == null)
//				continue;
//			
//			if(mats_i.containsKey(s.getType()) )
//			{
//				//System.out.println("found same type: "+s.getType());
//				if(mats_i.get(s.getType()).getRealItem().isSimilar(s))
//				{
//					//System.out.println("found same type2: "+s.getType());
//					mats_i.get(s.getType()).addAmount(s.getAmount());
//					_player_inv_refs.get(s.getType()).put(i,s);
//					continue;
//				}
//				
//				
//			}
//			ShopItem shopitem = new ShopItem(_mainn,i ,s,s.getAmount());
//			mats_i.put(s.getType(), shopitem);
//			HashMap<Integer,ItemStack> ar = new HashMap<>();
//			ar.put(i,s);
//			_player_inv_refs.put(s.getType(), ar);
//			//i_mats.put(i,s.getType());
//			
//		}
//		
////		List<Material> test = new ArrayList<>(i_mats.values());
////		Collections.sort(test);
//		int count = 0;
//		int count2 = 0;
//		for(ShopItem si : mats_i.values())
//		{
//			ItemStack displayItem = si.getDisplayItem();
//			setButton(displayItem, BUTTON.PLAYER_ITEM);
//			if(_metods.isArmor(si.getRealItem()) || _metods.isTool(si.getRealItem()))
//			{
//				 _playerItemsOthers[count2++] = si;
//				continue;
//			}
//						
//			_playerItemsNormal[count] = si;		
//			if(count < 18)
//			{
//				_inv.setItem(count+_player_slots_start, displayItem);
//			}
//			count++;
////			if(++count > _playerItemsNormal.length)
////				break;
//		}		
//	}
//	ShopItemBase GetItem(int slot)
//	{
//		if(slot >= 0 && slot < 27) //shopside
//		{
//			//return _shopItems.get(getShopSlot(slot));
//		}
//		
//		if(slot >= _player_slots_start && slot <_size)
//		{
//			if(p_state == PLAYER_INV_STATE.NORMAL)
//			{
//				return _playerItemsNormal[slot-_player_slots_start];
//			}
//			
//			if(p_state == PLAYER_INV_STATE.OTHER_STUFF)
//			{
//				return _playerItemsOthers[slot-_player_slots_start];
//			}
//			
//		}
//		return null;
//	}
}

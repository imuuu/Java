package imu.GS.Invs;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Strings;

import imu.GS.Main.Main;
import imu.GS.Other.CustomPriceData;
import imu.GS.Prompts.ConvCCPINV;
import imu.GS.ShopUtl.ShopItemModData;
import imu.GS.ShopUtl.ItemPrice.PriceCustom;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;

public class CreateCustomPriceInv extends CustomInvLayout
{
	ShopItemStockable _sis;
	ShopModModifyINV _smmi;
	ShopItemModData _modData;
	Boolean[] _slotLock;
	CPriceItem[] _priceItems;
	Main _main;
	public CreateCustomPriceInv(Plugin main, Player player, ShopModModifyINV smmi, ShopItemStockable sis, ShopItemModData modData) 
	{
		super(main, player, "Create custom price", 9*6);
		_main = (Main)main;
		_sis = sis;
		_smmi = smmi;
		_modData = modData;
		_denyItemMove = DENY_ITEM_MOVE.NONE;
		_slotLock = new Boolean[_size];
		_priceItems = new CPriceItem[_size];
		Arrays.fill(_slotLock, false);
		setupButtons();
		LoadModData();
		//System.out.println("CONSTRUCT");
		
	}
	public class CCPdata
	{
		public int _slot;
		public double value;
		CCPdata(int slot){_slot = slot;}
	}
	
	class CPriceItem
	{
		public ItemStack _realStack;
		private ItemStack _displayStack;
		public int _slot;
		private double _value = 1;
		boolean roundIt = true;
		public CPriceItem(ItemStack real, int slot)
		{
			//System.out.println("construct of cprice: "+ real.getType());
			_value = real.getAmount();
			_realStack = new ItemStack(real);
			_displayStack =  new ItemStack(real);
			real.setAmount(0);
			_displayStack.setAmount(1);
			_slot = slot;
			
			
			Tooltip();
			//_inv.setItem(slot, _displayStack);
		}
		
		public double GetAmount()
		{
			return _value;
		}
		
		public ItemStack GetDisplayItem()
		{
			if(_displayStack == null || _displayStack.getType() == Material.AIR)
			{
				if(_realStack != null) 
				{
					System.out.println("NEED TO COPY");
					_displayStack = _realStack.clone();
					
				}
				
			}
			Tooltip();
			return _displayStack;
		}
		
		public void SetAmount(double amount)
		{
			_value = amount;
			if(roundIt) _value = Math.round(amount);
			
			Tooltip();
		}
		
		void Tooltip()
		{
			SetButton(_displayStack, BUTTON.PRICE_ITEM);
			String[] lores =
			{
				"&bM1: &2Set new Amount  &bM2: &cRemove",
				"&9Amount: &1"+_value,
			};
			ImusAPI._metods.SetLores(_displayStack, lores, false);
			//System.out.println("setting value of  cprice: "+amount);
		}
	}
	enum BUTTON implements IButton
	{
		NONE,
		BACK,
		CONFIRM,
		PRICE_ITEM,
	}
	
	void LoadModData()
	{
		if(!(_modData._itemPrice instanceof PriceCustom))
			return;
		
		int i = 0;
		for(CustomPriceData data : ((PriceCustom)_modData._itemPrice).GetItems())
		{
			_priceItems[i] = new CPriceItem(data._stack,i);
			_priceItems[i++].SetAmount(data._amount);
		}
		LoadPriceItems();
	}
	
	public void SetData(CCPdata data)
	{
		_priceItems[data._slot].SetAmount(data.value);
		//_inv.setItem(data._slot, _priceItems[data._slot]._displayStack);
		System.out.println("setting data: "+data.value+ "slot: "+data._slot);
		LoadPriceItems();
	}
	
	@Override
	public void setupButtons() {
		
		setupButton(BUTTON.BACK, Material.RED_STAINED_GLASS_PANE,"&cBACK", _size-9); _slotLock[_size-9] = true;
		setupButton(BUTTON.CONFIRM, Material.GREEN_STAINED_GLASS_PANE,"&1CONFIRM", _size-1); _slotLock[_size-1] = true;	
		for(int i = _size-2; i > _size-9; i--) {setupButton(BUTTON.NONE, Material.BLACK_STAINED_GLASS_PANE," ", i); _slotLock[i] = true;}
		//setupButton(BUTTON.PRICE_ITEM, Material.PAPER,"&9ADD Money", _size-5);// _slotLock[_size-5] = true;
		LockItem(setupButton(BUTTON.PRICE_ITEM, Material.PAPER,"&9Add Money", _size-5), _size-5);
		_priceItems[_size-5].SetAmount(0);
	}
	
	
	boolean CheckSlotLock(int slot)
	{
		if(slot < 0 || slot >= _size) return false;
		return _slotLock[slot];
	}
	
	void LoadPriceItems()
	{
		//System.out.println("loading price items");
		for(int i = 0; i < _inv.getSize()-10; i++) {_inv.setItem(i, null); _slotLock[i] = false;}
		for(CPriceItem cpi : _priceItems)
		{
			//System.out.println("cpi: "+cpi);	
			if(cpi == null) continue;
			//System.out.println("==> cpi slot: "+cpi._slot +" display: "+cpi.GetDisplayItem().getType() + "real: "+cpi._realStack.getType() + " value: "+cpi._value);
			_inv.setItem(cpi._slot, cpi.GetDisplayItem());
			_slotLock[cpi._slot] = true;

		}
	}
	
	void CheckInv()
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				System.out.println("loadInv");
				for(int i= 0; i < _size-9; i++)
				{
					ItemStack stack  = _inv.getItem(i);
					if(stack == null) 
					{
						//System.out.println("slot: "+i +" is null");
						_slotLock[i] = false;
						continue;
					}
					boolean found = false;
					for(CPriceItem cpi : _priceItems)
					{
						if(cpi != null && cpi._realStack.isSimilar(stack) && cpi._slot != i)
						{
							cpi.SetAmount(1);
							System.out.println("removing :"+stack.getType()+ " slot: "+i);
							stack.setAmount(0);
							_slotLock[i] = false;
							found = true;
							break;
						}
					}
					if(found) continue;
					
					//if(_slotLock[i]) continue;
					LockItem(stack, i);
				}
			}
		}.runTaskLater(_plugin,1);
		
		LoadPriceItems();
	}
	
	void LockItem(ItemStack stack, int slot)
	{
		if(stack == null || stack.getType() == Material.AIR) return ;
		if(slot < 0 || slot > _size-1) return;
		if(_priceItems[slot] != null) return;
		//System.out.println("new lock: "+stack.getType() + " slot: "+ slot);
		_priceItems[slot] = new CPriceItem(stack, slot);		
		
		_slotLock[slot] = true;
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				_inv.setItem(slot, _priceItems[slot].GetDisplayItem());
			}
		}.runTaskLater(_main, 1);
		
		
	}
	
	
	
	@EventHandler
	public void OnDrag(InventoryDragEvent e)
	{		
		if(isThisInv(e))
		{
			CheckInv();			
		}
	}
	
	@EventHandler
	public void onClickk(InventoryClickEvent e)
	{
		//System.out.println("click2");
		if(isThisInv(e))
		{
			if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) 
			{
				CheckInv();
				return;
			}
		}
		
	}
	
	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		//System.out.println("click");
		if(CheckSlotLock(e.getSlot())) e.setCancelled(true);
		
		
		
		switch (GetBUTTON(e.getCurrentItem())) 
		{
		case NONE:
			break;
		case BACK:
			for(CPriceItem item : _priceItems) {if(item != null && item._slot != _size-5)ImusAPI._metods.InventoryAddItemOrDrop(item._realStack, _player);}
			_player.closeInventory();
			_smmi.openThis();
			return;
		case CONFIRM:
			System.out.println("Confirm!");
			ArrayList<CustomPriceData> stacks = new ArrayList<CustomPriceData>();
			double money = 0;
			for(CPriceItem cpi : _priceItems)
			{
				if(cpi == null) continue;
				if(cpi._slot == _size-5) 
				{
					money = cpi._value;
					continue;
				}
				stacks.add(new CustomPriceData(cpi._realStack.clone(), (int)cpi._value));
			}
			CustomPriceData[] array = new CustomPriceData[stacks.size()];
			for(int i = 0; i < array.length; ++i) {array[i] = stacks.get(i);}
			_modData._itemPrice = new PriceCustom().SetItemsAndPrice(array, money);
			_player.closeInventory();
			_smmi.SetModData(_modData);
			_smmi.openThis();
			return;
		case PRICE_ITEM:
			if(e.getClick() == ClickType.RIGHT) 
			{
				//System.out.println("remove");
				if(e.getSlot() == _size-5) {
					_priceItems[e.getSlot()].SetAmount(0);
					_inv.setItem(_priceItems[e.getSlot()]._slot, _priceItems[e.getSlot()].GetDisplayItem());
					return;
				}
				ItemStack stack =_priceItems[e.getSlot()]._realStack.clone();
				ImusAPI._metods.InventoryAddItemOrDrop(stack, _player);
				_priceItems[e.getSlot()]._realStack.setAmount(0);
				_priceItems[e.getSlot()]._displayStack.setAmount(0);
				_priceItems[e.getSlot()] = null;
				_slotLock[e.getSlot()] = false;
				_inv.setItem(e.getSlot(), null);
				//CheckInv();
				
				return;
			}
			ConversationFactory cf = new ConversationFactory(_main);
			String question = "&3Give new amount";
			Conversation conv = cf.withFirstPrompt(new ConvCCPINV(_main, this, new CCPdata(e.getSlot()), question)).withLocalEcho(true).buildConversation(_player);
			conv.begin();
			_player.closeInventory();
			return;

		default:
			break;
		
		}
		
		if(e.getClick() != ClickType.RIGHT) 
		{
			LockItem(_droppedStack, _droppedSlot);
			e.getCursor().setAmount(0);
			//System.out.println("cursor: "+);
		}
	}
	
	BUTTON GetBUTTON(ItemStack stack)
	{
		if(stack == null) return BUTTON.NONE;
		String bName = getButtonName(stack);		
		if(Strings.isNullOrEmpty(bName)) return BUTTON.NONE;
		return BUTTON.valueOf(bName);
	}

	
	
	@Override
	public void invClosed(InventoryCloseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	
	

}

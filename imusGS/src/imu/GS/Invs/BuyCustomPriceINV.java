package imu.GS.Invs;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import imu.GS.Main.Main;
import imu.GS.Other.CustomPriceData;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.Customer.ShopItemCustomer;
import imu.GS.ShopUtl.ItemPrice.PriceCustom;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;

public class BuyCustomPriceINV extends CustomerInv
{
	ShopBase _shopBase;
	ShopItemSeller _sis;
	Main _main;
	
	int _selected_amount = 1;
	public BuyCustomPriceINV(Plugin main, Player player, ShopBase shopBase, ShopItemSeller sis) {
		super(main, player, "Buying "+ImusAPI._metods.GetItemDisplayName(sis.GetDisplayItem()), 6*9);
		_main=(Main)main;
		_shopBase = shopBase;
		_sis = sis;
		_sis.RegisterSlot(_inv, this, -1, _size-5, true);
		_main.RegisterInv(this);
		setupButtons();
	}
	
	enum BUTTON implements IButton
	{
		NONE,
		BACK,
		CONFIRM,
		CHANGE_AMOUNT;
	}

	@Override
	public void invClosed(InventoryCloseEvent e) 
	{
		_sis.UnRegisterSlot(_inv);
		_main.UnregisterInv(this);
	}

	

	@Override
	public void setupButtons() 
	{
		for(int i = _size-9; i < _size; i++) {setupButton(BUTTON.NONE, Material.YELLOW_STAINED_GLASS_PANE," ", i);}
		setupButton(BUTTON.BACK, Material.RED_STAINED_GLASS_PANE,"&c<= BACK", _size-9);
		setupButton(BUTTON.CONFIRM, Material.GREEN_STAINED_GLASS_PANE,"&9BUY", _size-1);
		
		ItemStack stack = new ItemStack(Material.IRON_BLOCK);
		Metods.setDisplayName(stack, "&9BUY&6 1");
		ImusAPI._metods.setPersistenData(stack, "buyAmount", PersistentDataType.INTEGER, 1);
		_inv.setItem(_size-26, SetButton(stack, BUTTON.CHANGE_AMOUNT));
		
		stack = new ItemStack(Material.COPPER_BLOCK);
		Metods.setDisplayName(stack, "&9BUY&6 4");
		ImusAPI._metods.setPersistenData(stack, "buyAmount", PersistentDataType.INTEGER, 4);
		_inv.setItem(_size-25, SetButton(stack, BUTTON.CHANGE_AMOUNT));
		
		stack = new ItemStack(Material.GOLD_BLOCK);
		Metods.setDisplayName(stack, "&9BUY&6 8");
		ImusAPI._metods.setPersistenData(stack, "buyAmount", PersistentDataType.INTEGER, 8);
		_inv.setItem(_size-24, SetButton(stack, BUTTON.CHANGE_AMOUNT));
		
		stack = new ItemStack(Material.DIAMOND_BLOCK);
		Metods.setDisplayName(stack, "&9BUY&6 16");
		ImusAPI._metods.setPersistenData(stack, "buyAmount", PersistentDataType.INTEGER, 16);
		_inv.setItem(_size-23, SetButton(stack, BUTTON.CHANGE_AMOUNT)); 
		
		stack = new ItemStack(Material.EMERALD_BLOCK);
		Metods.setDisplayName(stack, "&9BUY&6 32");
		ImusAPI._metods.setPersistenData(stack, "buyAmount", PersistentDataType.INTEGER, 32);
		_inv.setItem(_size-22, SetButton(stack, BUTTON.CHANGE_AMOUNT)); 
		
		stack = new ItemStack(Material.NETHERITE_BLOCK);
		Metods.setDisplayName(stack, "&9BUY&6 64");
		ImusAPI._metods.setPersistenData(stack, "buyAmount", PersistentDataType.INTEGER, 64);
		_inv.setItem(_size-21, SetButton(stack, BUTTON.CHANGE_AMOUNT)); 
			
		SetShopSlot(_sis, 0, _size-5);
		CheckSelectedAmount();
		RefreshBuyStacks();
	}
	
	@Override
	public void SetShopSlot(ShopItemSeller sis, int page, int slot) 
	{
		_sis = sis;
		ItemStack displayItem =  _sis.GetRealItem().clone();
		SetButton(displayItem, BUTTON.NONE);
		ImusAPI._metods.addLore(displayItem, "&9Amount: &a"+_sis.Get_amount(), false);
		_inv.setItem(slot, displayItem);
		
		ItemStack stack = new ItemStack(Material.CRYING_OBSIDIAN);
		Metods.setDisplayName(stack, "&9BUY ALL&6 "+_sis.Get_amount());
		ImusAPI._metods.setPersistenData(stack, "buyAmount", PersistentDataType.INTEGER, -1);
		_inv.setItem(_size-20, SetButton(stack, BUTTON.CHANGE_AMOUNT)); 
	}
	
	
	void RefreshBuyStacks()
	{
		CustomPriceData[] data = ((PriceCustom)_sis.GetItemPrice()).GetItems();
		ItemStack stack;
		for(int i = 0; i < data.length; ++i)
		{
			if(i >= 27) break;
			
			stack = data[i]._stack.clone();
			ImusAPI._metods.addLore(stack, "&9Amount:&6 "+(data[i]._amount * _selected_amount), false);
			_inv.setItem(i, stack);
			
		}
	}
	
	boolean CheckIfPlayerHasItems()
	{
		CustomPriceData[] items = ((PriceCustom)_sis.GetItemPrice()).GetItems();
		CustomPriceData[] datas = new CustomPriceData[items.length];
		for(int i = 0; i < items.length; i++) {datas[i] = new CustomPriceData(items[i]._stack.clone(), items[i]._amount * _selected_amount);}
		//ArrayList<ItemStack> ref_stacks = new ArrayList<>();
		ItemStack[] newStacks = new ItemStack[_player.getInventory().getContents().length];
		
		int i = 0;
		for(ItemStack stack : _player.getInventory().getContents())
		{
			ItemStack copy = null;
			if(stack != null)
			{
				copy = stack.clone();
				for(CustomPriceData data : datas)
				{
					if(data._stack.isSimilar(copy) && data._amount > 0) 
					{			
						System.out.println("data: "+stack.getType()+ " dataAmount: "+data._amount +" stackA: "+copy.getAmount());
						data._amount -= copy.getAmount();
						if(data._amount < 0)
						{
							System.out.println("==> left: "+Math.abs(data._amount));
							copy.setAmount(Math.abs(data._amount));
						}
						else
						{
							
							copy.setAmount(0);
							System.out.println("==> all gone: " + copy);
							
						}
						System.out.println("=====> dataAmount after: "+data._amount);
						
						
					}
				}
			}
			
			
			newStacks[i++] = copy;
		}
		
		for(CustomPriceData data : datas) { if(data._amount > 0) return false; }
		
		System.out.println("setting data!");
		_player.getInventory().setContents(newStacks);
		return true;
	}
	
	void CheckSelectedAmount()
	{
		for(int i = _size-18; i < _size-9; ++i) {_inv.setItem(i, null);}
		int slot = -1;
		int offSet = _size-26+9;
		switch (_selected_amount) 
		{
		case 1:	{slot = offSet;break;}
		case 4:	{slot = offSet+1; break;}
		case 8:	{slot = offSet+2; break;}
		case 16:{slot = offSet+3; break;}
		case 32:{slot = offSet+4; break;}
		case 64:{slot = offSet+5; break;}
		case -1:{slot = offSet+6; break;}
		}
		
		if(slot == -1) return;
		
		_inv.setItem(slot, Metods.setDisplayName(new ItemStack(Material.REDSTONE_TORCH), "&a^SELECTED^") );
		
	}
	BUTTON GetButtonPress(InventoryClickEvent e)
	{
		ItemStack stack = e.getCurrentItem();
		if(stack == null)
			return BUTTON.NONE;
		
		String buttonName = getButtonName(e.getCurrentItem());
		if(buttonName == null)
			return BUTTON.NONE;
		
		return BUTTON.valueOf(buttonName);
	}
	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		BUTTON button = GetButtonPress(e);
		
		switch (button) 
		{
		case NONE:
			return;
		case BACK:
			break;
		case CHANGE_AMOUNT:
			_selected_amount = ImusAPI._metods.getPersistenData(e.getCurrentItem(), "buyAmount", PersistentDataType.INTEGER);
			CheckSelectedAmount();
			RefreshBuyStacks();
			break;
		case CONFIRM:
			System.out.println("buing");
			if(!CheckIfPlayerHasItems()) return;
			ShopItemBase sib = _shopBase.GetItem(_sis.GetPage(), _sis.GetSlot());
			//ShopItemCustomer sic = new ShopItemCustomer(_main, _shopBase, _player, sib.GetRealItem().clone(), _selected_amount);
			//sic.AddAmountToPlayer(_selected_amount * -1);
			
			ImusAPI._metods.InventoryAddItemOrDrop(sib.GetRealItem().clone(), _player);
			
			//sib.AddAmount(_selected_amount * -1);
			sib.UpdateItem();
			break;
		default:
			break;
		}
		
	}

}

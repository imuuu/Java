package imu.GS.ShopUtl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import imu.GS.ENUMs.ShopItemType;
import imu.GS.Invs.CustomerInv;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.Customer.CustomerMenuBaseInv;
import imu.GS.ShopUtl.Customer.ShopItemCustomer;
import imu.GS.ShopUtl.ItemPrice.ItemPrice;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;
import imu.GS.ShopUtl.ItemPrice.PriceOwn;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;

public abstract class ShopItemBase 
{
	protected UUID _uuid;
	protected ItemStack _real_stack;
	protected ShopItemType _type = ShopItemType.NORMAL;
	protected ItemStack _display_stack;
	ItemStack _display_stack_not_available;
	final ItemStack _display_out_of_stock = Metods.setDisplayName(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "&9-");
	
	int _amount = 0;
	
	String lore_amount_str;
	String lore_price_str;
	
	protected String[] _lores;
	protected Metods _metods;

	HashMap<Inventory, SlotInfo> _slotPositions = new HashMap<>();
	ItemPrice _price;
	protected Main _main;
	protected ShopBase _shopBase;
	
	Set<String> _tags = new HashSet<>();
	
	public ShopItemBase(Main main, ShopBase shopBase, ItemStack real, int amount) 
	{
		_metods = ImusAPI._metods;
		_main = main;
		SetUUID(UUID.randomUUID());	
		_shopBase = shopBase;
		_real_stack = real.clone();
		_display_stack = real.clone();
		_display_stack.setAmount(1);
		_display_stack_not_available = ImusAPI._metods.addLore(_display_stack.clone(), Metods.msgC("&cNOT AVAILABLE"), false);
			
		_amount = amount;
		LoadLores();
		_price = _main.get_shopManager().GetPriceMaterial(real.getType());
		//SetItemPrice((ItemPrice)(_main.get_shopManager().GetPriceMaterial(real.getType())));
		
		
		//toolTip();
		
	}
	enum LoreSpot
	{
		AMOUNT;
	}
	
	public boolean AddTag(String tagName)
	{
		if(_tags.contains(tagName.toLowerCase())) return false;
		_tags.add(tagName.toLowerCase());
		return true;
	}
	
	public void RemoveTag(String tagName)
	{
		_tags.remove(tagName.toLowerCase());
	}
	
	public boolean HasTag(String tagName)
	{
		return _tags.contains(tagName.toLowerCase());
	}
	
	public void ClearTags()
	{
		_tags.clear();
	}
	
	class SlotInfo
	{
		Inventory _inv = null;
		CustomerInv _customerInv;
		int _page = -1;
		int _slot = -1;
		boolean _shopItem = false;
		public SlotInfo(Inventory _inv, CustomerInv customerInv,int _page, int _slot, boolean _shopItem) 
		{
			this._inv = _inv;
			this._page = _page;
			this._slot = _slot;
			this._shopItem = _shopItem;
			this._customerInv = customerInv;
		}
	}
	
	public ShopBase GetShop()
	{
		return _shopBase;
	}
	
	public ShopItemType GetItemType()
	{
		return _type;
	}
	
	public abstract JsonObject GetJsonData();
	public abstract void ParseJsonData(JsonObject data);
	
	public void SetUUID(UUID uuid)
	{
		_uuid = uuid;
	}
	
	public UUID GetUUID()
	{
		return _uuid;
	}
	
	public void SetItemPrice(ItemPrice price)
	{
//		if(price.getClass().equals(PriceMaterial.class)) //price instanceof PriceMoney && !(price instanceof PriceOwn) 
//		{
//			//((PriceMoney)price).SetPrice(_main.get_shopManager().GetMaterialPrice(GetRealItem().getType()));
//			price.SetPrice(_main.get_shopManager().GetPriceMaterial(_real_stack.getType()).GetPrice());
//		}
//		
		
//		if(price instanceof PriceUnique && !(GetItemPrice() instanceof PriceOwn))
//		{
//			_price = price;
//		}
		System.out.println("Setting price type: "+price);
		_price = price;
			
		if(price instanceof PriceOwn)
		{
			//System.out.println("it is priceMoney: "+price.GetPrice());
			((PriceMoney)price).SetShowPrice(price.GetPrice());
		}
		else
		{
			SetShowPrice(price);	
		}

		toolTip();
	}
	protected abstract void SetShowPrice(ItemPrice price);
	
	public ItemPrice GetItemPrice()
	{
		return _price;
	}
	
	public void RegisterSlot(Inventory inv, CustomerInv customerInv, int page, int slot, boolean shopItem)
	{
		_slotPositions.put(inv, new SlotInfo(inv, customerInv, page, slot, shopItem));
	}
	
	public void UnRegisterSlot(Inventory inv)
	{
		_slotPositions.remove(inv);
	}
	
	void LoadLores()
	{
		_lores = new String[5];
		String front = (this instanceof ShopItemSeller ? "&2BUY " : "&3SELL ");
		_lores[0] =  Metods.msgC("&9____ &6Amount: &a");
		_lores[1] =  Metods.msgC(" &bM1  "+front+" :  &e1  : &5");
		_lores[2] =  Metods.msgC(" &bM2  "+front+" :  &e8  : &5");
		_lores[3] =  Metods.msgC("&9S&bM1  "+front+" : &e64 : &5");
		_lores[4] =  Metods.msgC("&9S&bM2  "+front+" : &eAll : &5");

	}
	
	protected void toolTip()
	{
		if(_amount <= 0)
		{
			_display_stack = _display_out_of_stock;
			return;
		}
		_display_stack = _real_stack.clone();
		_display_stack.setAmount(1);
		String empty = "&4-";
		String[] lores = new String[_lores.length];
		lores[0] = _lores[0] + _amount+" &9____";
		lores[1] = 	(_amount 	>= 1 	? 	_lores[1]+_price.GetShowPriceOfAmountStr(1) 		: empty);
		lores[2] = 	(_amount  	>= 8 	? 	_lores[2]+_price.GetShowPriceOfAmountStr(8) 		: empty);
		lores[3] = 	(_amount  	>= 64 	? 	_lores[3]+_price.GetShowPriceOfAmountStr(64) 		: empty);
		if(lores[3].equalsIgnoreCase(empty))
		{
			lores[3] = 	(_amount  	>  8 	? 	_lores[4]+_price.GetShowPriceOfAmountStr(_amount) 	: empty);
		}
		else
		{
			lores[4] = 	(_amount  	>= 64 	? 	_lores[4]+_price.GetShowPriceOfAmountStr(_amount) 	: empty);
		}
		

		_metods.addLore(_display_stack, lores);
		//_metods.setlo
	}
	
	public SlotInfo GetSlotInfo(Inventory inv)
	{
		return _slotPositions.get(inv);
	}

	public ItemStack GetDisplayItem()
	{
		toolTip();
		return _display_stack;
	}
	
	public ItemStack GetDisplayItemNotAvailable()
	{
		return _display_stack_not_available;
	}
	
	public ItemStack GetRealItem()
	{
		return _real_stack;
	}
	
	public Boolean IsSameKind(ShopItemBase item)
	{
		return item.GetRealItem().isSimilar(_real_stack);
	}
	
	public Boolean IsSameKind(ItemStack item)
	{
		return item.isSimilar(_real_stack);
	}
	
	public void UpdateItem()
	{
		for(SlotInfo sInfo : _slotPositions.values())
		{
			if(!sInfo._shopItem)
			{
				
				((CustomerMenuBaseInv)sInfo._customerInv).UpdateCustomerSlot((ShopItemCustomer)this, sInfo._page, sInfo._slot);
				return;
			}else
			{
				//sInfo._cmbi.UpdateShopSlot(sInfo._page, sInfo._slot);
				sInfo._customerInv.SetShopSlot((ShopItemSeller)this, sInfo._page, sInfo._slot);
			}
		}
	}
	
	public void AddAmount(int amount)
	{
		_amount+= amount;		
		//CheckDisplayItem();		
	}
	
	void CheckDisplayIte()
	{
		if(_amount <= 0)
		{
			_amount = 0;
			GetDisplayItem().setType(Material.BLACK_STAINED_GLASS_PANE);
		}
		else
		{
			if(GetDisplayItem().getType() != GetRealItem().getType())
			{
				GetDisplayItem().setType(GetRealItem().getType());
			}
		}
		
		//SetLoreAtSpot(LoreSpot.AMOUNT);
		toolTip();
	}
	
	public int Get_amount() {
		return _amount;
	}


	public void Set_amount(int _amount) 
	{
		this._amount = _amount;
		//CheckDisplayItem();
	}
}

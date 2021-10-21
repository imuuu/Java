package imu.GS.ShopUtl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.Customer.Customer;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;
import imu.iAPI.Interfaces.ITuple;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Tuple;
import net.md_5.bungee.api.ChatColor;

public abstract class ShopBase 
{
	Main _main;
	public String _name;
	public String _displayName;
	public int shopHolderSize = 27;
	
	//ArrayList<ShopItem> _items = new ArrayList<>();
	//HashMap<Integer, ShopItemBase[]> _items = new HashMap<>();
	ArrayList<ShopItemSeller[]> _items = new ArrayList<ShopItemSeller[]>();
	
	
	double _sellM = 1.0;
	double _buyM  = 1.0;
	
	double _expire_percent = 0.1f;
	int _expire_cooldown_m = 30;
	String _cd_expire = "expire";
	
	Cooldowns _cds;
	
	HashMap<UUID, Customer> _hCustomers = new HashMap<>();
	
	public ShopBase(Main main, String name, int pages)
	{
		_main = main;
		_displayName = name;
		
		_name =  _main.GetMetods().StripColor(name);
		
		_cds = new Cooldowns();
		
		for(int i = 0; i < pages; ++i)
		{
			get_items().add(new ShopItemSeller[shopHolderSize]);
		}
		
		//_items.put(0, new ShopItemBase[shopHolderSize]);
	}
	
	public String GetNameWithColor()
	{
		return ChatColor.translateAlternateColorCodes('&', _displayName);
	}
	
	
	public void AddNewCustomer(Player player)
	{
		_hCustomers.put(player.getUniqueId(), new Customer(_main, player,this).Open());
	}
	
	public void RemoveCustomer(Player player, boolean closeInv)
	{
		if(!_hCustomers.containsKey(player.getUniqueId()))
			return;
		
		Customer customer = _hCustomers.get(player.getUniqueId());
		
		if(closeInv)
			customer.Close();
		
		_hCustomers.remove(player.getUniqueId());
		
		if(_hCustomers.size() == 0)
		{
			System.out.println("No customers Save shop data!");
			_main.get_shopManager().SaveShop(_name, false);
		}
	}
	public void RemoveCustomerALL()
	{
		for(Customer cus : _hCustomers.values())
		{
			cus.Close();
		}
		_hCustomers.clear();
	}
	
	public void SetItem(ShopItemSeller sis, int page, int slot)
	{
		//SetPrice(sis);
		get_items().get(page)[slot] = sis;
	}
	
	public ShopItemBase GetItem(int page, int index)
	{
		if(page <get_items().size())
		{
			return get_items().get(page)[index];
		}
		return null;
	}
	
	public void RemoveItem(int page, int idx, int amount)
	{
		ShopItemBase sib = get_items().get(page)[idx];
		sib.AddAmount(amount);
		sib.UpdateItem();
	}
	
	public void UnRegisterItems(Inventory inv)
	{
		for(int i = 0; i < get_items().size(); ++i)
		{
			for(int l = 0; l < get_items().get(i).length; ++l)
			{
				if(get_items().get(i)[l] == null)
					continue;
				
				get_items().get(i)[l].UnRegisterSlot(inv);
			}
		}
	}
	
	void RegisterAndLoadNewItemsClients()
	{
		//System.out.println("LOAD all clients shop inv!");
		for(Customer customer : _hCustomers.values())
		{
			//_items.get(page)[index].RegisterSlot(customer._shopInv.GetInv(), customer._shopInv, page, index, true);
			customer._shopInv.LoadShopInv();
			//System.out.println("====>" + customer._player.getName());
		}
		
	}
	
//	void SetPrice(ShopItemSeller sis)
//	{
//		if(sis.GetItemPrice() instanceof PriceMoney)
//		{
//			double price = ((PriceMoney)sis.GetItemPrice()).GetPrice();
//			((PriceMoney)sis.GetItemPrice()).SetShowPrice(price * _sellM);
//			sis.toolTip();
//		}
//	}
	
	public void AddNewItem(ShopItemSeller sis)
	{
		int page = 0;
		
		//SetPrice(sis);
		
		if(sis.GetItemPrice() instanceof PriceMoney)
		{
			double price = ((PriceMoney)sis.GetItemPrice()).GetPrice();
			((PriceMoney)sis.GetItemPrice()).SetShowPrice(price * _sellM);
		}
		
		ITuple<Integer, Integer> firstFree = null;
		for(; page < get_items().size(); ++page)
		{
			for(int i = 0; i < get_items().get(page).length; ++i)
			{
				ShopItemBase sib = get_items().get(page)[i];
				if(sib == null)
				{
					if(firstFree == null)
						firstFree = new Tuple<Integer, Integer>(page,i);
					continue;
				}
				
				if(sib.IsSameKind(sis))
				{
					sib.AddAmount(sis.Get_amount());
					//UpdateClients(page, i);
					sib.UpdateItem();
					//System.out.println("Same kind of item update it");
					return;
				}
			}
		}
		
		if(firstFree == null)
		{
			System.out.println("Shop: Not free space found, make new page!");
			get_items().add(new ShopItemSeller[shopHolderSize]);
			get_items().get(get_items().size()-1)[0] =  sis;
			//UpdateClients(page+1, 0);
			RegisterAndLoadNewItemsClients();
			return;
		}
		System.out.println("Shop: Adding to free slot");
		get_items().get(firstFree.GetKey())[firstFree.GetValue()] = 	sis;
		//UpdateClients(firstFree.GetKey(), firstFree.GetValue());
		RegisterAndLoadNewItemsClients();
		return;
	}

	public ArrayList<ShopItemSeller[]> get_items() {
		return _items;
	}
	
	public double get_sellM() {
		return _sellM;
	}

	public void set_sellM(double _sellM) {
		this._sellM = _sellM;
	}

	public double get_buyM() {
		return _buyM;
	}

	public void set_buyM(double _buyM) {
		this._buyM = _buyM;
	}

	public double get_expire_percent() {
		return _expire_percent;
	}

	public void set_expire_percent(double _expire_percent) {
		this._expire_percent = _expire_percent;
	}

	public int get_expire_cooldown_m() {
		return _expire_cooldown_m;
	}

	public void set_expire_cooldown_m(int _expire_cooldown_m) {
		this._expire_cooldown_m = _expire_cooldown_m;
	}

	public String get_cd_expire() {
		return _cd_expire;
	}

	public void set_cd_expire(String _cd_expire) {
		this._cd_expire = _cd_expire;
	}
	
	
	
}

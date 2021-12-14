package imu.GS.ShopUtl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.Customer.Customer;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Interfaces.ITuple;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Tuple;
import net.md_5.bungee.api.ChatColor;

public abstract class ShopBase 
{
	protected Main _main;
	private String _name;
	private String _displayName;
	private UUID _uuid;
	private  int shopHolderSize = 27;
	
	private ArrayList<ShopItemSeller[]> _items = new ArrayList<ShopItemSeller[]>();
	
	
	private double _sellM = 1.0;
	private double _buyM  = 1.0;
	
	private double _expire_percent = 0.1f;
	private int _expire_cooldown_m = 1;
	private boolean _absoluteItemPosition = true;
	private String _cd_expire = "expire";
	
	private Cooldowns _cds;
	
	private HashMap<UUID, Customer> _hCustomers = new HashMap<>();
	private boolean _locked = false;
	private boolean _intererActlocked = false;
	private boolean _customers_can_only_buy = false;
	
	
	public ShopBase(Main main, UUID uuid,String name, int pages)
	{
		_main = main;
		
		SetName(name);
		_cds = new Cooldowns();
		SetNewExpire();
		_uuid = uuid;
		for(int i = 0; i < pages; ++i)
		{
			get_items().add(new ShopItemSeller[shopHolderSize]);
		}
		
		//_items.put(0, new ShopItemBase[shopHolderSize]);
	}
	public void SetCustomersCanOnlyBuy(boolean canBuy)
	{
		_customers_can_only_buy = canBuy;
	}
	
	public boolean GetCustomersCanOnlyBuy()
	{
		return _customers_can_only_buy;
	}
	public UUID GetUUID()
	{
		return _uuid;
	}
	
	public boolean IsAbsoluteItemPositions()
	{
		return _absoluteItemPosition;
	}
	
	public String GetNameWithColor()
	{
		return ChatColor.translateAlternateColorCodes('&', _displayName);
	}
	
	public boolean HasExpired()
	{
		return _cds.isCooldownReady(_cd_expire);
	}
	
	public void SetNewExpire()
	{
		_cds.setCooldownInSeconds(_cd_expire, _expire_cooldown_m * 60);
	}
	
	public boolean HasCustomers()
	{
		return _hCustomers.size() > 0;
	}
	
	public boolean HasLocked()
	{
		return _locked;
	}
	
	public void SetLocked(boolean locked)
	{
		_locked = locked;
		if(_locked && HasCustomers())
		{
			for(Customer customer : _hCustomers.values())
			{
				RemoveCustomer(customer._player.getUniqueId(), true);
			}
		}
	}
	
	public void SetLockToInteract(boolean lock)
	{
		_intererActlocked = lock;
	}
	
	public boolean HasInteractLock() 
	{
		return _intererActlocked;
	}
	
	public void AddNewCustomer(Player player)
	{
		//System.out.println("add customer");
		_hCustomers.put(player.getUniqueId(), new Customer(_main, player,this).Open());
	}
	
	public void RemoveCustomer(UUID uuid, boolean closeInv)
	{
		if(!_hCustomers.containsKey(uuid))
			return;
		
		Customer customer = _hCustomers.get(uuid);
		
		if(closeInv)
			customer.Close();
		
		_hCustomers.remove(uuid);
	}
	
	public void LoadCustomerInvs()
	{
		for(Customer customer : _hCustomers.values())
		{
			customer._shopInv.LoadShopInv();
			customer._shopInv.LoadPlayerInv();		
		}
	}
	
	public void SaveData()
	{
		new BukkitRunnable() 
		{			
			@Override
			public void run() 
			{
				boolean lock = HasLocked();
				SetLocked(true);
				ArrangeShopItems();
				_main.get_shopManager().SaveShop(_name, false);
				SetLocked(lock);
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void RemoveCustomerALL()
	{
		for(Customer cus : _hCustomers.values())
		{
			cus.Close();
		}
		_hCustomers.clear();
	}
	
	public void ArrangeShopItems()
	{
		//System.out.println("arrange shop");
		for(int page = _items.size()-1; page >= 0 ; page--)
		{
			boolean isEmpty = true;

			ShopItemSeller[] items = new ShopItemSeller[shopHolderSize];
			int idx = 0;
			//int[] stockSlots = new int[shopHolderSize];
			Set<Integer> stockSlots = new HashSet<>();
			for(int slot = 0;  slot < _items.get(page).length; ++slot)
			{
				ShopItemBase sib = _items.get(page)[slot];
				if( sib != null && sib.Get_amount() > 0 || (sib instanceof ShopItemStockable))
				{
					//System.out.println("==> shopItem found");
					if(sib instanceof ShopItemStockable)
					{
						//System.out.println("stocable");
						stockSlots.add(slot);
						items[slot]=_items.get(page)[slot];
						
					}else
					{
						if(stockSlots.contains(idx)) while(stockSlots.contains(++idx));
						
						
						items[idx] =_items.get(page)[slot].SetPageAndSlot(page, idx);
						idx++;
					}
					
					isEmpty = false;
				}
			}
			
			if(isEmpty && page != 0)
			{
				_items.remove(_items.size()-1);
			}
			else
			{
				_items.set(page, items);
			}
		}
	}
	
	public void SetItem(ShopItemSeller sis, int page, int slot)
	{
		get_items().get(page)[slot] = sis.SetPageAndSlot(page, slot);
	}
	
	public ShopItemBase GetItem(int page, int index)
	{
		if(page < get_items().size())
		{
			return get_items().get(page)[index];
		}
		return null;
	}
	
	public void RemoveItem(int page, int idx)
	{
		//ShopItemBase sib = get_items().get(page)[idx];
		_items.get(page)[idx] = null;
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
	

	public void AddNewItem(ShopItemSeller sis, boolean setAmount)
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
					if(setAmount)
					{
						sib.Set_amount(sis.Get_amount());
					}else
					{
						sib.AddAmount(sis.Get_amount());
					}
					

					sib.UpdateItem();

					return;
				}
			}
		}
		
		if(firstFree == null)
		{
			//System.out.println("Shop: Not free space found, make new page!");
			get_items().add(new ShopItemSeller[shopHolderSize]);
			get_items().get(get_items().size()-1)[0] =  sis.SetPageAndSlot(get_items().size()-1, 0);
			RegisterAndLoadNewItemsClients();
			return;
		}
		//System.out.println("Shop: Adding to free slot");
		get_items().get(firstFree.GetKey())[firstFree.GetValue()] = sis.SetPageAndSlot(firstFree.GetKey(), firstFree.GetValue());
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
	
	public void SetName(String name)
	{
		_displayName = name;		
		_name =  _main.GetMetods().StripColor(name);
	}
	
	public String GetName() {return _name;}
	
	public String GetDisplayName() {return _displayName;}
	
	public void SetAbsolutePosBool(boolean b)
	{
		_absoluteItemPosition = b;
	}
	
	public boolean GetAbsolutePosBool() {return _absoluteItemPosition;}
	
}

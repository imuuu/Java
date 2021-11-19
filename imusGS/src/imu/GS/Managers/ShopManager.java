package imu.GS.Managers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopNormal;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.ImusTabCompleter;
import imu.iAPI.Other.Metods;

public class ShopManager 
{
	Main _main;
	
	ArrayList<ShopBase> _shops;
	
	HashMap<Material, Double> _material_prices = new HashMap<>();
	BukkitTask RunnableAsyncTask;
	ShopManagerSQL _shopManagerSQL;
	UniqueManager _uniqueManager;
	
	HashMap<UUID, CustomInvLayout> _opened_invs = new HashMap<>();
	
	int _shopCheckTime = 10;
	
	public final String pd_page="gs.page";
	public final String pd_slot="gs.slot";
	
	public ShopManager(Main main)
	{
		_main = main;
		_shops = new ArrayList<>();
			
	}
	
	public void Init()
	{
		_shopManagerSQL = new ShopManagerSQL(_main, this);
		_uniqueManager = new UniqueManager(_main, this, _shopManagerSQL);
		
		new BukkitRunnable() 
		{			
			@Override
			public void run() 
			{
				_shopManagerSQL.LoadTables();
				_shopManagerSQL.LoadMaterialPrices();
				LoadShops();
				_shopManagerSQL.LoadUniques();
			}
		}.runTaskAsynchronously(_main);
		
		
		RunnableAsync();
	}
	
	public Integer GetSISSlot(ItemStack stack)
	{
		if(stack == null) return null;
		return ImusAPI._metods.getPersistenData(stack, pd_slot, PersistentDataType.INTEGER);
	}
	
	public Integer GetSISPage(ItemStack stack)
	{
		if(stack == null) return null;
		return ImusAPI._metods.getPersistenData(stack, pd_page, PersistentDataType.INTEGER);
	}
	
	public UniqueManager GetUniqueManager()
	{
		return _uniqueManager;
	}
	
	public void RegisterOpenedInv(Player player, CustomInvLayout inv)
	{
		_opened_invs.put(player.getUniqueId(), inv);
	}
	
	public void UnRegisterOpenedInv(Player player)
	{
		_opened_invs.remove(player.getUniqueId());
	}
	
	
	
	void RunnableAsync()
	{
		RunnableAsyncTask = new BukkitRunnable() 
		{			
			@Override
			public void run() 
			{
				//System.out.println("Check expires");
				for(ShopBase shop : _shops)
				{
					if(shop.HasExpired()) // && 
					{
						if(!shop.HasCustomers())
						{
//							boolean locked = shop.HasLocked();
//							shop.SetLocked(true);
							
//							shop.SetLocked(locked);
						}
						System.out.println("Check shop");
						shop.SetLockToInteract(true);
						CheckShopItems(shop);
					
						shop.SetNewExpire();
						shop.ArrangeShopItems();
						
						if(shop.HasCustomers())
							shop.LoadCustomerInvs();
						
						shop.SetLockToInteract(false);
					}
				}			
			}
		}.runTaskTimerAsynchronously(_main, 20 * _shopCheckTime, 20 * _shopCheckTime);
		
		
	}
	void CheckShopItems(ShopBase sBase)
	{

		for(ShopItemSeller[] siss : sBase.get_items())
		{
			for(ShopItemSeller sis : siss)
			{
				if(sis == null)
					continue;
				
				if(sis instanceof ShopItemStockable)
				{
					if(((ShopItemStockable)sis).AbleToFill())
					{
						((ShopItemStockable)sis).Fill();
						((ShopItemStockable)sis).RefreshFillCD();
					}
					return;
				}
				
				int amount = sis.Get_amount();
				int newAmount = (int)Math.floor(amount * (1.0 - sBase.get_expire_percent()));
				if(newAmount < 0)
					newAmount = 0;
				
				sis.Set_amount(newAmount);
				//sis.UpdateItem();
			}
		}
	}
	public double GetMaterialPrice(Material material)
	{
		return _material_prices.get(material);
	}
	
	public boolean OpenShop(Player p,String name)
	{
		ShopBase shop = GetShop(name);
		if(shop != null)
		{			
			if(shop.HasLocked())
			{
				p.sendMessage(Metods.msgC("&2Shop has closed. Please come laiter"));
				return false;
			}
			shop.AddNewCustomer(p);
			return true;
		}
		return false;
	}
	
	public void onDisabled()
	{	
		closeShopInvs();
		if(RunnableAsyncTask != null)
			RunnableAsyncTask.cancel();
	}
	
	void closeShopInvs()
	{
		for(ShopBase sb : _shops)
		{
			sb.RemoveCustomerALL();
		}
		
	}
	
	public void CreateShop(String name)
	{
		ShopBase shop = new ShopNormal(_main, name, 1);
		AddShop(shop);
	}
	
	public void AddShop(ShopBase shopBase)
	{
		_shops.add(shopBase);
		SaveShop(shopBase.GetName(), true);
		UpdateTabCompliters();
	}
	
	public void RemoveShop(String name)
	{
		ShopBase s = GetShop(name);
		if(s == null)
			return;
		
		s.RemoveCustomerALL();
		_shops.remove(s);

		_shopManagerSQL.DeleteShop(s);
	}
	
	public ShopBase GetShop(String name)
	{
		String searchName = _main.GetMetods().StripColor(name);
		for(ShopBase s : _shops)
		{
			if(s.GetName().toLowerCase().contains(searchName.toLowerCase()))
				return s;
		}
		return null;
	}
	
	public void SaveShop(String name, boolean dontsavedatabase)
	{
		if(_main.GetSQL() == null || dontsavedatabase)
			return;
		
		ShopBase sBase = GetShop(name);
		
		if(sBase == null)
			return;
		
		_shopManagerSQL.SaveShop(sBase);
	}
	
	public void LoadShops()
	{
		_shopManagerSQL.LoadShops();	
	}
	
	
	public void UpdateTabCompliters()
	{
		
		String[] shopNames = new String[_shops.size()];
		for(int i = 0; i < _shops.size(); ++i)
		{
			shopNames[i] = _shops.get(i).GetName();
		}
		ImusTabCompleter tab = _main.get_tab_cmd1();
		tab.setArgumenrs("shop", shopNames);
	}
	
	
}

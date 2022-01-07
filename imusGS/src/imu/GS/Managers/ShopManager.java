package imu.GS.Managers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.GS.Main.Main;
import imu.GS.Other.EnchantINFO;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ShopNormal;
import imu.GS.ShopUtl.ItemPrice.PriceCustom;
import imu.GS.ShopUtl.ItemPrice.PriceMaterial;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;
import imu.iAPI.Other.Tuple;

public class ShopManager 
{
	private Main _main;
	
	private ArrayList<ShopBase> _shops;
	
	private HashMap<Material, PriceMaterial> _material_prices = new HashMap<>();
	
	private BukkitTask RunnableAsyncTask;
	private ShopManagerSQL _shopManagerSQL;
	private UniqueManager _uniqueManager;
	
	private HashMap<UUID, CustomInvLayout> _opened_invs = new HashMap<>();
	private HashMap<UUID, ArrayList<Tuple<String, PriceCustom>>> _savedPriceCustoms = new HashMap<>();
	
	private int _shopCheckTime_s = 20;
	
	public final String pd_page="gs.page";
	public final String pd_slot="gs.slot";
	public double _durability_penalty = 0.1; // 0.0 => 0%
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
				_shopManagerSQL.LoadUniques();
				_shopManagerSQL.LoadShops();	
				_shopManagerSQL.LoadShopItems();
				_main.GetShopEnchantManager().CreateTables();
				_main.GetShopEnchantManager().LoadEnchantInfos();
				_main.GetTagManager().LoadMaterialTags();
				
				_main.GetTagManager().LoadAllShopItemTagsNamesAsync();
			}
		}.runTaskAsynchronously(_main);
		
		
		RunnableAsync();
	}
	
	void PutMaterialPrice(Material mat, double price)
	{		
		PriceMaterial pm = new PriceMaterial();
		pm.SetPrice(price);
		_material_prices.put(mat, pm);
	}
	
	public PriceMaterial GetPriceMaterial(Material mat)
	{
		return _material_prices.get(mat);
	}
	
	public double GetDurabilityReduction(ItemStack stack)
	{
		double durProsent = Metods._ins.getDurabilityProsent(stack);
		if(durProsent < 1.0)
		{
			durProsent = durProsent - _durability_penalty;
			
		}
		if(durProsent < 0) durProsent = 0;
		if(durProsent > 1.0) durProsent = 1;
		
		return durProsent;
	}
	
	public PriceMaterial GetPriceMaterialAndCheck(ItemStack stack)
	{
		PriceMaterial priceMaterial = new PriceMaterial();
		priceMaterial.SetPrice(0);
		if(stack == null || stack.getType() == Material.AIR) return priceMaterial;
		//if((ImusAPI._metods.isArmor(stack) || ImusAPI._metods.isTool(stack)) &&  ImusAPI._metods.getDurabilityProsent(stack) != 1.0) return priceMaterial; // if material has eny durability lost it will be 0
		
		double price = _material_prices.get(stack.getType()).GetPrice();
		
		
		double addedEnchantPrice = 0;
		for(Map.Entry<Enchantment, Integer> entry  : Metods._ins.GetEnchantsWithLevels(stack).entrySet())
		{			
			Enchantment ench = entry.getKey();
			int level = entry.getValue();
			EnchantINFO eInfo = _main.GetShopEnchantManager().GetInfo(ench);
			double priceValue = eInfo.GetPrice(level).GetPrice();
			addedEnchantPrice += stack.getType() == Material.ENCHANTED_BOOK ? (priceValue * eInfo.Get_rawMultiplier()) : priceValue;
		}
		
		//price.SetPrice(price.GetPrice()+addedEnchantPrice);
		priceMaterial.SetPrice((price +addedEnchantPrice) * GetDurabilityReduction(stack));
	
		
		return priceMaterial;
	}
	
	public ShopManagerSQL GetShopManagerSQL()
	{
		return _shopManagerSQL;
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
	
	public void SavePriceCustom(UUID playerUUID, String name,PriceCustom pc)
	{
		if(!_savedPriceCustoms.containsKey(playerUUID)) _savedPriceCustoms.put(playerUUID, new ArrayList<>());
		_savedPriceCustoms.get(playerUUID).add(new Tuple<String, PriceCustom>(name,pc));
	}
	
	public ArrayList<Tuple<String,PriceCustom>> GetSavedPlayerPriceCustoms(UUID playerUUID)
	{
		return (_savedPriceCustoms.containsKey(playerUUID) ? _savedPriceCustoms.get(playerUUID) : new ArrayList<Tuple<String,PriceCustom>>());
	}
	
	public ArrayList<ShopBase> GetShops()
	{
		return _shops;
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
						//System.out.println("Check shop");
						shop.SetLockToInteract(true);
						CheckShopItems(shop,true);				
						shop.SetNewExpire();
						
						shop.ArrangeShopItems(shop.HasCustomers() ? false : true, false); // => interact fill be false
												
						//shop.SetLockToInteract(false);
					}
					else
					{
						shop.SetLockToInteract(true);
						CheckShopItems(shop,false);
						shop.SetLockToInteract(false);
					}
				}			
			}
		}.runTaskTimerAsynchronously(_main, 20 * _shopCheckTime_s, 20 * _shopCheckTime_s);
		
		
	}
	void CheckShopItems(ShopBase sBase, boolean checkExpires)
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
					continue;
				}
				
				if(!checkExpires) continue;
				
				int amount = sis.Get_amount();
				
				int newAmount = (int)Math.floor(amount * (1.0 - sBase.get_expire_percent()));
				if(newAmount < 0)
					newAmount = 0;

				sis.Set_amount(newAmount);
			}
		}
	}

	public void SaveMaterialPrice(Material mat, double price)
	{
		if(price < 0) 
		{
			price = 0;
		}else
		{
			price = Math.round(price * 100.00) / 100.00;
		}
		
		PutMaterialPrice(mat, price);
		
		final double pricee = price;
		

		for(ShopBase shop : _shops)
		{
			boolean removeCustomers = false;
			for(ShopItemBase[] pages : shop.get_items())
			{
				for(ShopItemBase sib : pages)
				{
					if(sib == null) continue;
					
					if(sib.GetItemPrice() instanceof PriceMaterial && sib.GetRealItem().getType() == mat)
					{
						sib.SetItemPrice(GetPriceMaterial(mat));
						removeCustomers = true;
					}
				}
			}
			if(removeCustomers) shop.RemoveCustomerALL();
		}
		new BukkitRunnable() 
    	{
			@Override
			public void run() 
			{				
				_shopManagerSQL.SaveMaterialPrice(mat, pricee);
			}
		}.runTaskAsynchronously(_main);	
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
	
	public void CreateNewShop(String name)
	{
		ShopBase shop = new ShopNormal(_main, UUID.randomUUID(), name, 1);
		AddShop(shop);
	}
	
	public void AddShop(ShopBase shopBase)
	{
		_shops.add(shopBase);
		SaveShop(shopBase.GetUUID(), true);
		UpdateTabCompliters();
	}
	
	public void RemoveShop(UUID uuid)
	{
		ShopBase s = GetShop(uuid);
		if(s == null)
			return;
		
		s.RemoveCustomerALL();
		_shops.remove(s);

		_shopManagerSQL.DeleteShopAsync(s);
	}
	
	public ShopBase GetShop(UUID uuid)
	{
		for(ShopBase s : _shops)
		{
			if(s.GetUUID().equals(uuid)) return s;
		}
		return null;
	}
	
	public ShopBase GetShop(String name)
	{
		String searchName = ImusAPI._metods.StripColor(name);
		for(ShopBase s : _shops)
		{
			if(s.GetName().toLowerCase().contains(searchName.toLowerCase()))
				return s;
		}
		return null;
	}
	
	public void SaveShop(UUID uuid, boolean dontsavedatabase)
	{
		if(_main.GetSQL() == null || dontsavedatabase)
			return;
		
		ShopBase sBase = GetShop(uuid);
		
		if(sBase == null)
			return;
		
		_shopManagerSQL.SaveShopAsync(sBase);
	}
	

	
	
	public void UpdateTabCompliters()
	{
		
		String[] shopNames = new String[_shops.size()];
		for(int i = 0; i < _shops.size(); ++i)
		{
			shopNames[i] = _shops.get(i).GetName();
		}
		
		_main.UpdateShopNames(shopNames);
	}
	
	
}

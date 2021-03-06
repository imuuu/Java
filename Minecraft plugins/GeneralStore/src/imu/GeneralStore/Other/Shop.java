package imu.GeneralStore.Other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.type.Switch;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.datafixers.util.Pair;

import imu.GeneralStore.main.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_16_R3.Item;



public class Shop implements Listener
{
	String _displayName = "";
	String _name = "";
	String _fileNameShopYML="";
	String _fileNameSelledMaterialCount="";
	int _size=9*6;
	
	boolean _shopOnlySell = false;

	HashMap<Player,Inventory> player_invs = new HashMap<>();
	HashMap<Player,Integer> player_currentLabel = new HashMap<>();
	HashMap<Player,Integer> player_currentShopPage = new HashMap<>();
	HashMap<Player,Integer> player_currentPlayerPage = new HashMap<>();
	
	HashMap<Player,Integer> player_clicks = new HashMap<>();
	HashMap<Player,Integer> player_clicks_warnings = new HashMap<>();
	HashMap<Player,BukkitRunnable> player_runnables = new HashMap<>();
	
	HashMap<Player, ArrayList<ItemStack>> player_emptyStacks = new HashMap<>();
	
	
	ItemMetods itemM = null;
	
	
	Main _main = null;
	
	Economy _econ = null;
	
	ShopManager shopManager=null;
	EnchantsManager enchantsManager = null;
	
	int _firstPlayerSlot = 36;
	int _firstShopSlot = 0;
	int _firstMiddleSlot = 27;
		
	HashMap<Player,HashMap<ItemStack, Integer>> player_stuff=new HashMap<>();
	HashMap<Player,HashMap<ItemStack, ArrayList<ItemStack>>> player_refs=new HashMap<>();
	
	ArrayList<ItemStack> shop_stuff_stacks = new ArrayList<>();
	ArrayList<Integer> shop_stuff_values = new ArrayList<>();
	
	HashMap<Integer,ItemStack> label_icons = new HashMap<>();
	
	String pd_isArmor = "gs.Isarmor";
	String pd_switcher= "gs.Switch";
	String pd_text = "gs.text";
	String pd_count = "gs.count";
	String pd_shopSwitchButton= "gs.shopsSwitchButton";
	String pd_playerSwitchButton= "gs.playerSwitchButton";
	String pd_pone= "gs.priceOne";
	String pd_peight= "gs.priceEight";
	String pd_pstack= "gs.priceStack";
	String pd_pall= "gs.priceAll";
	String pd_infItem="gs.infItem";
	
	String pd_type="gs.itemType";
	
	int _maxClicksInHalfSecond=10/2;
	
	Cooldowns cds = new Cooldowns();
	String cdName = "expireTime";
	
	String[] str_lores = {  ChatColor.GREEN+ "M1  :"+ChatColor.DARK_PURPLE+"  1   : "+ChatColor.GOLD+" ",
							ChatColor.GREEN+ "M2  :"+ChatColor.DARK_PURPLE+"  8   : "+ChatColor.GOLD+" ",
							ChatColor.GREEN+ "SM1 :"+ChatColor.DARK_PURPLE+" 64  : "+ChatColor.GOLD+" ",
							ChatColor.GREEN+ "SM2 :"+ChatColor.DARK_PURPLE+" All  : "+ChatColor.GOLD+" "};
	
	//DATA Gather
	ArrayList<Pair<Material, Integer> > sellCountValues = new ArrayList<>();
	
	
	public Shop(Main main, String shopName, boolean realShop, boolean onlySelling) 
	{
		_main = main;
		_econ = main.getEconomy();
		itemM = main.getItemM();
		
		if(realShop)
		{
			_displayName = shopName;
			_name = ChatColor.stripColor(shopName);
			_fileNameShopYML = "shop_"+_name+".yml";
			_fileNameSelledMaterialCount=_name+"_SelledMaterialCount.yml";
			
			_maxClicksInHalfSecond =(int)(_main.getClickPerSecond()/2);
			_main.getServer().getPluginManager().registerEvents(this, _main);
			setupConfig();
			setLabelIcons();
			//makeShop();
			cds.addCooldownInSeconds(cdName, _main.getExpireTime());
			runnable();
		
		}
		shopManager = _main.getShopManager();
		enchantsManager = _main.getEnchManager();
		
		setOnlySell(onlySelling);
	
	}
	
	public enum LABELS
	{
		STUFF(0),
		ARMOR(1);
		
		private int type;
		
		LABELS(int i)
		{
			this.type = i;
		}
		public int getType()
		{
			return type;
		}
		
	}
	
	public enum ShopItemType
	{
		NONE,
		EMPTY,
		INFINITY;
		
		
	}
		
	void setLabelIcons()
	{
		ItemStack armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack stuff = new ItemStack(Material.STONE);
		ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		
		itemM.setPersistenData(armor, pd_switcher, PersistentDataType.INTEGER, (int)1);
		itemM.setPersistenData(stuff, pd_switcher, PersistentDataType.INTEGER, (int)1);
		
		itemM.setPersistenData(armor, pd_text, PersistentDataType.STRING, ChatColor.AQUA + "Armor, Tools, Weapons");
		itemM.setPersistenData(stuff, pd_text, PersistentDataType.STRING, ChatColor.AQUA + "Other stuff");
		
		
		String emptyDisName = ChatColor.GRAY + "-";
		itemM.setPersistenData(empty, pd_text, PersistentDataType.STRING, emptyDisName);
		itemM.setPersistenData(empty, pd_type, PersistentDataType.STRING, ShopItemType.EMPTY.toString());
		itemM.setDisplayName(empty, emptyDisName);

		label_icons.put(0,armor);
		label_icons.put(1,stuff);
		label_icons.put(2,empty);
		
	}
	
	public ShopItemType getShopItemType(ItemStack stack)
	{
		String type = itemM.getPersistenData(stack, pd_type, PersistentDataType.STRING);
		if(type != null)
		{
			return ShopItemType.valueOf(type);
		}
		return ShopItemType.NONE;

	}
	
	public void setOnlySell(boolean onlySell)
	{
		_shopOnlySell = onlySell;
	}
	public boolean getOnlySell()
	{
		return _shopOnlySell;
	}
	public String getName()
	{
		return _name;
	}
	public String getDisplayName()
	{
		return _displayName;
	}
	
	public String getFileName()
	{
		return _fileNameShopYML.substring(0, _fileNameShopYML.lastIndexOf("."));
	}
	
	public String getFileNameYml()
	{
		return _fileNameShopYML;
	}
	void runnable()
	{
		int refTime = _main.getRunnableDelay();
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{

				if(cds.isCooldownReady(cdName))
				{
					checkExpireTime();				
					cds.addCooldownInSeconds(cdName, _main.getExpireTime());
				}
				
			}
		}.runTaskTimer(_main, 0, 20 * refTime);
	}
	
	void checkExpireTime()
	{
		if(shop_stuff_stacks.size() <= 0)
		{
			return;
		}
		double removeP=_main.getExpireProsent()/100;
		for(int i = 0; i < shop_stuff_stacks.size(); ++i)
		{			
			ItemStack stack = shop_stuff_stacks.get(i);
			
			if(isStackInf(stack))
			{
				continue;
			}
			
			Integer amount = getShopStackAmount(stack);
			double removeAmount = Math.round(((amount * removeP)+0.5));
			int now_amount = (int) (amount-removeAmount);
			setShopStackAmount(stack, now_amount);
			shop_stuff_values.set(i, now_amount);			
			
		}
		RefresAllInvs();
	}
	
	public Integer getShopStackAmount(ItemStack stack)
	{
		return itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
	}
	public void setShopStackAmount(ItemStack stack, int amount)
	{
		itemM.setPersistenData(stack, pd_count, PersistentDataType.INTEGER,amount);
	}
	
	void setStackInfItem(ItemStack stack)
	{
		itemM.setPersistenData(stack, pd_infItem, PersistentDataType.INTEGER,1);
	}
	
	void removeInfItemPd(ItemStack stack)
	{
		itemM.removePersistenData(stack, pd_infItem);
	}
	
	boolean isStackInf(ItemStack stack)
	{
		Integer inf = itemM.getPersistenData(stack, pd_infItem, PersistentDataType.INTEGER);
		if(inf != null && inf > 0)
		{
			return true;
		}
		return false;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void setupConfig()
	{
		ConfigMaker cm = new ConfigMaker(_main, _fileNameShopYML);
		FileConfiguration config = cm.getConfig();
		if(!cm.isExists())
		{
			config.set("shop_stacks", shop_stuff_stacks);
			config.set("shop_values",shop_stuff_values);
			cm.saveConfig();
		}
		else
		{
			shop_stuff_stacks = new ArrayList<ItemStack>((List)config.getList("shop_stacks"));
			shop_stuff_values = new ArrayList<Integer>((List)config.getList("shop_values"));
		}
	}
	
	public void configSaveContent()
	{
		ConfigMaker cm = new ConfigMaker(_main, _fileNameShopYML);
		FileConfiguration config = cm.getConfig();
		if(cm.isExists())
		{
			
			if(shop_stuff_stacks.size() <= 0 || shop_stuff_values.size() <= 0)
			{
				System.out.println("CLEAR shop");
				shop_stuff_stacks.clear();
				shop_stuff_values.clear();
			}
			
			for(int i = 0 ; i < shop_stuff_stacks.size(); ++i)
			{
				Integer count = getShopStackAmount(shop_stuff_stacks.get(i));
				if(count == null || count <= 0)
				{
					shop_stuff_stacks.remove(i);					
				}
			}
			
			for(int i = 0 ; i < shop_stuff_values.size(); ++i)
			{
				int count = shop_stuff_values.get(i);
				if(count <= 0)
				{
					shop_stuff_values.remove(i);				
				}
			}
			
			config.set("shop_stacks", shop_stuff_stacks);
			config.set("shop_values",shop_stuff_values);
			cm.saveConfig();
		}
	}
	
	public void configSelledItemCount(ItemStack stack,int amount)
	{
		sellCountValues.add(new Pair<Material,Integer>(stack.getType(), amount));
	}
	
	public void configSellLogItem(Player player, ItemStack stack, int amount)
	{
		Date date = new Date(System.currentTimeMillis());
		String str = _name+":"+player.getName() +":"+stack.getType().name()+":"+amount;
		_main.getShopManager().logAddSold(new Pair<Date, String>(date,str));
	}
	
	boolean checkIfAbleToSaveData()
	{
		if(!isShopsOpened())
		{
			if(sellCountValues.size() > 0)
			{
				ConfigMaker cm = new ConfigMaker(_main, _fileNameSelledMaterialCount);
				FileConfiguration config = cm.getConfig();
				
				if(!cm.isExists())
				{
					cm.saveConfig();
				}
				
				for(Pair<Material, Integer> values : sellCountValues)
				{
					String mName = values.getFirst().name();
					int am = values.getSecond();

					if(!config.contains(mName))
					{
						config.set(mName, 1);
					}else
					{
						int count = config.getInt(mName)+am;
						config.set(mName, count);
					}
				}
				cm.saveConfig();
				sellCountValues.clear();
			}
			
			return true;
		}
		
		return false;
	}
	
	void makeShop(Player player)
	{
		Inventory inv = _main.getServer().createInventory(null, _size, _displayName);
				
		ItemStack panel = itemM.setDisplayName(
				new ItemStack(Material.RED_STAINED_GLASS_PANE)," ");		
				
		for(int i = 28; i < _firstPlayerSlot-1; ++i)
		{
			inv.setItem(i, panel);
		}
		
		player_invs.put(player, inv);
		
		BukkitRunnable r = new BukkitRunnable() 
		{
			Player p = player;
			@Override
			public void run() 
			{
				Integer clicks = player_clicks.get(p);
				if(clicks != null && clicks >_maxClicksInHalfSecond)
				{
					player_clicks.put(p, -100);
				}else
				{
					if(clicks != null && clicks > -1)
					{
						player_clicks.put(p, 0);
					}
					
				}
								
				if(!player_invs.containsKey(p))
				{
					this.cancel();
				}
			}
		};
		
		r.runTaskTimerAsynchronously(_main, 0, 10);
		player_runnables.put(player, r);
		//0-26 => shop items
		//36-54 => player items
	}
	
	public boolean isShopsOpened()
	{
		if(player_invs.size() > 0)
		{
			return true;
		}

		return false;
		
	}
	public void closeShopInvs()
	{
		for(Player p : player_invs.keySet())
		{
			p.closeInventory();
		}
	}
	
	int shopPageCount()
	{
		int pages =(int) Math.round(((shop_stuff_stacks.size()-1)/(_firstMiddleSlot))+0.5);
		return pages-1;
	}
	
	
	void setMiddleLINE(Inventory inv, int label)
	{
		ItemStack left_shop_button = new ItemStack(Material.DARK_OAK_SIGN);
		ItemStack right_shop_button = new ItemStack(Material.DARK_OAK_SIGN);
		
		ItemStack left_player_button = new ItemStack(Material.BIRCH_SIGN);
		ItemStack right_player_button = new ItemStack(Material.BIRCH_SIGN);
		
		
		itemM.setPersistenData(left_shop_button, pd_shopSwitchButton, PersistentDataType.INTEGER, (int)-1);
		itemM.setPersistenData(right_shop_button, pd_shopSwitchButton, PersistentDataType.INTEGER, (int) 1);
		itemM.setPersistenData(left_player_button, pd_playerSwitchButton, PersistentDataType.INTEGER, (int) -1);
		itemM.setPersistenData(right_player_button, pd_playerSwitchButton, PersistentDataType.INTEGER, (int) 1);
		
		
		ItemStack label_icon = label_icons.get(label);
		
		inv.setItem((_firstMiddleSlot+35)/2, itemM.setDisplayName(label_icon,itemM.getPersistenData(label_icon, pd_text, PersistentDataType.STRING) ));
		inv.setItem((_firstMiddleSlot+35)/2-1, itemM.setDisplayName(left_player_button, ChatColor.DARK_GREEN + "<< inv"));
		inv.setItem((_firstMiddleSlot+35)/2+1, itemM.setDisplayName(right_player_button,ChatColor.DARK_GREEN + "inv >> "));
		
		inv.setItem(_firstMiddleSlot, itemM.setDisplayName(left_shop_button, ChatColor.AQUA + "<< Shop"));
		inv.setItem(_firstPlayerSlot-1, itemM.setDisplayName(right_shop_button, ChatColor.AQUA + "Shop >>"));
	}
	
	public void openShopInv(Player player)
	{
		makeShop(player);
		player.openInventory(player_invs.get(player));
	}
	
	public void closeShopInv(Player player)
	{
		player_currentLabel.remove(player);
		player_invs.remove(player);
		player_clicks.remove(player);
		
		player_refs.remove(player);
		player_stuff.remove(player);
		player_currentPlayerPage.remove(player);
		player_currentShopPage.remove(player);
		player_currentLabel.remove(player);
		
		player_runnables.remove(player);		 // oli kommentoitou ?
		clearEmptysINinv(player);
	}
	
	@EventHandler
	void invOpen(InventoryOpenEvent e)
	{
		if(e.getPlayer() instanceof Player)
		{		
			Player player = (Player) e.getPlayer();
			InventoryView view = e.getView();
			if(view.getTitle().equalsIgnoreCase(_displayName))
			{
				player_currentLabel.put(player, LABELS.STUFF.getType());
				player_currentShopPage.put(player, 0);
				player_currentPlayerPage.put(player, 0);
				setStuffPlayerSlots(player, LABELS.STUFF.getType());			
			}			
		}		
	}
	
	@EventHandler
	void invClose(InventoryCloseEvent e)
	{
		if(e.getPlayer() instanceof Player)
		{		
			Player player = (Player) e.getPlayer();
			InventoryView view = e.getView();
			if(view.getTitle().equalsIgnoreCase(_displayName))
			{
				closeShopInv(player);
				checkIfAbleToSaveData();
				_main.getShopManager().checkIfAbleToSaveData();
			}			
		}		
	}
	
	
	@EventHandler
	void invClickEvent(InventoryClickEvent e)
	{
		InventoryView view = e.getView();
		if(e.getWhoClicked() instanceof Player)
		{
			Player player = (Player)e.getWhoClicked();
			
			if(view.getTitle().equalsIgnoreCase(_displayName))
			{
				Integer click_count = player_clicks.get(player);
				if(click_count == null)
				{
					click_count = 0;
				}
				if(click_count < 0)
				{
					player.closeInventory();
					int warnings_count = player_clicks_warnings.containsKey(player) ? player_clicks_warnings.get(player) : 0;     //player_clicks_warning => ei koskaan poisteta ?
					player_clicks_warnings.put(player, ++warnings_count);
					player.sendMessage(ChatColor.RED + "Please click slower! WARNING!");
					if(player_clicks_warnings.get(player) > 2)
					{
						player_clicks_warnings.put(player,1);
						player.kickPlayer(ChatColor.RED + "Please CLICK SLOWER!");
						
					}
					
					return;
				}
				player_clicks.put(player, ++click_count);
				
				e.setCancelled(true);
				
				int raw_slot = e.getRawSlot();
				ItemStack stack = e.getCurrentItem();
				ClickType click = e.getClick();
				
				if(stack == null || stack.getType() == Material.AIR)
				{
					return;
				}
				
				if(player.getGameMode() == GameMode.CREATIVE && click == ClickType.MIDDLE)
				{
					return;
				}
					
				ShopItemType shopItemType = getShopItemType(stack);
				if(shopItemType == ShopItemType.EMPTY)
				{
					return;
				}
				
				//shop side
				if(raw_slot > -1 && raw_slot < 27)
				{					
					//BUY FROM SHOP
					int stack_count = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
					double price_one = itemM.getPersistenData(stack, pd_pone, PersistentDataType.DOUBLE);
					
					clearEmptysINinv(player);
					
					if(click == ClickType.LEFT && withdrawPlayerHasMoney(player, price_one))
					{
						putItemToPlayerInv(player, stack, 1, false);
						return;
					}
					
					double price_eight = itemM.getPersistenData(stack, pd_peight, PersistentDataType.DOUBLE);
					if(click == ClickType.RIGHT && withdrawPlayerHasMoney(player, price_eight))
					{
						int c = 8;
						if(stack_count < 8)
							c = stack_count;
						putItemToPlayerInv(player, stack, c,false);
						return;
					}
					
					double price_stack = itemM.getPersistenData(stack, pd_pstack, PersistentDataType.DOUBLE);
					if(click == ClickType.SHIFT_LEFT && withdrawPlayerHasMoney(player, price_stack))
					{
						int c = 64;
						if(stack_count < 64)
							c = stack_count;
						putItemToPlayerInv(player, stack, c,false);
						return;
					}
					
					double price_all = itemM.getPersistenData(stack, pd_pall, PersistentDataType.DOUBLE);
					if(click == ClickType.SHIFT_RIGHT && withdrawPlayerHasMoney(player, price_all))
					{
						
						putItemToPlayerInv(player, stack, stack_count,false);
						return;
					}
					return;
				}
				
				//player side in shop
				if(raw_slot > _firstPlayerSlot-1 && raw_slot < _size && !_shopOnlySell)
				{
					
					//SELL TO THE SHOP
					int stack_count = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
					double price_one = itemM.getPersistenData(stack, pd_pone, PersistentDataType.DOUBLE);

					if(click == ClickType.LEFT)
					{						
				
						if(removeItemPlayerInv(player, stack, 1)) // if its stack has gone 0 => true
						{
							setEmptyToInv(player, e.getInventory(), raw_slot);
							
						}
						
						putItemToShop(stack, 1,false);
						depositMoney(player, price_one);
						return;
						
					}
					
					double price_eight = itemM.getPersistenData(stack, pd_peight, PersistentDataType.DOUBLE);
					if(click == ClickType.RIGHT)
					{
						int c = 8;
						if(stack_count < 8)
							c = stack_count;
						
						if(removeItemPlayerInv(player, stack, c))
						{
							setEmptyToInv(player, e.getInventory(), raw_slot);
						}
						
						putItemToShop(stack, c,false);
						depositMoney(player, price_eight);
						return;
					}
					
					double price_stack = itemM.getPersistenData(stack, pd_pstack, PersistentDataType.DOUBLE);
					if(click == ClickType.SHIFT_LEFT)
					{
						int c = 64;
						if(stack_count < 64)
							c = stack_count;
						
						if(removeItemPlayerInv(player, stack, c))
						{
							setEmptyToInv(player, e.getInventory(), raw_slot);
						}
						putItemToShop(stack, c,false);
						depositMoney(player, price_stack);
						return;
					}
					
					double price_all = itemM.getPersistenData(stack, pd_pall, PersistentDataType.DOUBLE);
					if(click == ClickType.SHIFT_RIGHT)
					{
						if(removeItemPlayerInv(player, stack, stack_count))
						{
							setEmptyToInv(player, e.getInventory(), raw_slot);
						}
						putItemToShop(stack, stack_count,false);
						depositMoney(player, price_all);
						return;
					}
					return;
				}
				if(raw_slot > _firstMiddleSlot-1 && raw_slot < _firstPlayerSlot)
				{
					//mid panel
					Integer data = itemM.getPersistenData(stack, pd_switcher, PersistentDataType.INTEGER);
					if(data != null && data == 1)
					{		
						clearEmptysINinv(player);
						setStuffPlayerSlots(player, switcher2000(player_currentLabel.get(player)));
						return;
					}
					
					data = itemM.getPersistenData(stack, pd_shopSwitchButton, PersistentDataType.INTEGER);
					if(data != null && data != 0)
					{

						
						int currentPage = player_currentShopPage.get(player);
						if(data == -1)
						{
							if(currentPage > 0)
							{
								int page_next = currentPage-1;
								if(page_next < 0)
								{
									page_next = 0;
								}
								player_currentShopPage.put(player, page_next);

								setStuffShopSlots(player);
								
								return;
							}
							
						}
						
						if(data == 1)
						{							
							if(shopPageCount() > currentPage)
							{
								int page_next = currentPage + 1;
								if(page_next > shopPageCount())
								{
									page_next = shopPageCount();
								}

								player_currentShopPage.put(player, page_next);

								setStuffShopSlots(player);

								return;
							}
						}
					}
					data = itemM.getPersistenData(stack, pd_playerSwitchButton, PersistentDataType.INTEGER);
					if(data != null && data != 0)
					{
						clearEmptysINinv(player);
						
						int cur_player_page = player_currentPlayerPage.get(player);
						if(data == -1)
						{
							if(cur_player_page > 0)
							{
								int page_next = cur_player_page-1;
								if(page_next < 0)
								{
									page_next = 0;
								}
								player_currentPlayerPage.put(player,page_next);
							}
							setStuffPlayerSlots(player, player_currentLabel.get(player));
							return;
						}
						
						if(data == 1)
						{
							int page_next = cur_player_page + 1;							
							player_currentPlayerPage.put(player, page_next);
							setStuffPlayerSlots(player, player_currentLabel.get(player));
							return;
						}
					}
					return;
				}
			}
		}
		
	}
	
	public Boolean clearEmptysINinv(Player p)
	{
		ArrayList<ItemStack> empties = player_emptyStacks.get(p);
		boolean something_remove = false;
		if(empties == null || empties.isEmpty())
		{
			return false;
		}
		
		for(ItemStack s : empties)
		{
			if(s != null)
			{
				s.setAmount(0);
				something_remove = true;
			}
			
		}		
		player_emptyStacks.remove(p);
		return something_remove;
		
	}
	
	public void setEmptyToInv(Player p, Inventory inv, int slot)
	{
		ArrayList<ItemStack> empties = player_emptyStacks.get(p);
		if(empties == null)
		{
			empties = new ArrayList<ItemStack>();
		}
		inv.setItem(slot, label_icons.get(2));
		empties.add(inv.getItem(slot));
		player_emptyStacks.put(p, empties);
	}
	
	public void depositMoney(Player player, double price)
	{
		if(_econ == null)
		{
			player.sendMessage(ChatColor.RED + "Economy plugin null");
			return;
		}
		
		double balance = _econ.getBalance(player);
		EconomyResponse res = _econ.depositPlayer(player, price);
		player.sendMessage(ChatColor.DARK_PURPLE+ "You have received: "+ ChatColor.WHITE +res.amount+" " +ChatColor.GOLD +_econ.currencyNameSingular()+ ChatColor.GRAY +" Total balance: "+ Math.round((balance+price)*100.0)/100.0 +" "+ ChatColor.GRAY + _econ.currencyNameSingular());
	}
	public boolean withdrawPlayerHasMoney(Player player, double price)
	{
		if(_econ == null)
		{
			player.sendMessage(ChatColor.RED + "Economy plugin null");
			return false;
		}
		double balance = _econ.getBalance(player);
		if(balance > price)
		{
			_econ.withdrawPlayer(player, price);
			player.sendMessage(ChatColor.DARK_PURPLE+ "Your balance now: "+ ChatColor.GOLD +""+ Math.round((balance-price)*100.0)/100.0);
			return true;
		}
		player.sendMessage(ChatColor.RED + "You don't have enough money!");
		player.sendMessage(ChatColor.DARK_PURPLE+ "Your balance is: "+ ChatColor.GOLD +""+ Math.round(balance*100.0)/100.0 +" "+ChatColor.DARK_PURPLE+"and that cost you: "+ChatColor.GOLD+""+price);
		return false;
	}
	
	void putItemToPlayerInv(Player player, ItemStack stack, int add_amount, boolean removeTotaly)
	{		
		ItemStack testing= new ItemStack(stack);
		
		removeAddedShopPDdata(testing);
		removeToolTip(testing);
		
		ItemStack copy = null;
		int i = 0;
		boolean remove = false;
		//System.out.println("Shop stuff stack size: "+shop_stuff_stacks.size());
		//itemM.printArray("test", shop_stuff_stacks.toArray());
		for(; i < shop_stuff_stacks.size() ; ++i)
		{
			ItemStack s = new ItemStack(shop_stuff_stacks.get(i));
			
			removeAddedShopPDdata(s);
			removeToolTip(s);
//			
//			System.out.println("====================");
//			System.out.println("testing: ===>"+testing);
//			System.out.println("   ");
//			System.out.println("   ");
//			System.out.println("   ");
//			System.out.println("S:       ===>"+ s);
//			System.out.println("====================");
//			
			if(s.isSimilar(testing))
			{
				copy = new ItemStack(s);
				
				if(!isStackInf(copy) || removeTotaly)
				{
					int amount = itemM.getPersistenData(shop_stuff_stacks.get(i), pd_count, PersistentDataType.INTEGER);

					int total = amount - add_amount;
					if(total < 1 || removeTotaly)
					{
						add_amount = add_amount + total;
						total = 0;
						remove = true;
					}

					itemM.setPersistenData(shop_stuff_stacks.get(i), pd_count, PersistentDataType.INTEGER, total);	
				}else
				{
					removeInfItemPd(copy);
				}
							
				break;
			}
		}
		
		if(remove)
		{
			shop_stuff_stacks.remove(i);
			shop_stuff_values.remove(i);
		}
		
		if(removeTotaly)
		{			
			RefresAllInvs();
			return;
		}

		if(copy == null)
		{
			System.out.println("COPY was null, line 857:shop.java");
			return;
		}
		int num = add_amount / 64;
		int remainder = add_amount % 64;
				
		copy.setAmount(64);
		for(int j = 0; j < num; ++j)
		{
			ItemStack item_spawn = new ItemStack(copy);
			itemM.moveItemFirstFreeSpaceInv(item_spawn, player, true);			
		}
		
		if(remainder > 0)
		{
			ItemStack item_spawn = new ItemStack(copy);
			item_spawn.setAmount(remainder);
			itemM.moveItemFirstFreeSpaceInv(item_spawn, player, true);
		}
				
		RefresAllInvs();

	}
	
	void RefresAllInvs()
	{
		for(Player p : player_invs.keySet())
		{
			setStuffPlayerSlots(p, player_currentLabel.get(p));
		}
	}
	
	Boolean removeItemPlayerInv(Player player,ItemStack stack, int remove_amount)
	{
		configSelledItemCount(stack, remove_amount);
		configSellLogItem(player, stack, remove_amount);
		HashMap<ItemStack, ArrayList<ItemStack>> refs = player_refs.get(player);
		
		boolean isEmpty = false;
		int whole_remove_amount = remove_amount;
		for(Map.Entry<ItemStack, ArrayList<ItemStack>> entry : refs.entrySet())
		{
			if(entry.getKey().isSimilar(stack))
			{
				int total_amount = itemM.getPersistenData(entry.getKey(), pd_count, PersistentDataType.INTEGER);
				for(ItemStack s : entry.getValue())
				{
					int stack_total = s.getAmount()-remove_amount;
					if(stack_total < 1)
					{
						remove_amount = Math.abs(stack_total);
						s.setAmount(0);  // checkkaa t�� jos myy yli 64

					}else
					{
						s.setAmount(stack_total);
						break;
					}
					
				}

				if((total_amount-whole_remove_amount)  < 1)
				{
					isEmpty = true;
				}
					
				break;
			}
			
		}
		
		//setStuffPlayerSlots(player, player_currentLabel.get(player));
		return isEmpty;
	}
	public int switcher2000(int current)
	{
		int x = 0;
		LABELS[] all_labels = LABELS.values();
		for(LABELS l : all_labels)
		{
			if(l.getType() == current+1)
			{
				x = current+1;
				break;
			}
		}
		
		return x;
	}
	public void setStuffShopSlots(Player player)
	{

		int start =_firstShopSlot + player_currentShopPage.get(player) * _firstMiddleSlot;
		int count = _firstShopSlot;
		
		Inventory inv = player_invs.get(player);
		if(shop_stuff_stacks.size() > 0)
		{
			ItemStack stack;
			ItemStack copy;
			for(int i = start ; i < shop_stuff_stacks.size(); ++i)
			{
				stack = shop_stuff_stacks.get(i);
				if(getShopStackAmount(stack) <= 0)
				{
					shop_stuff_stacks.remove(i);
					shop_stuff_values.remove(i);
				}
			}
			
			for(int i = start ; i < shop_stuff_stacks.size(); ++i)
			{
				stack = shop_stuff_stacks.get(i);
				
				copy = new ItemStack(stack);
				setToolTip(copy,false);
				
				inv.setItem(count, copy);

				count++;
				
				if(count > _firstMiddleSlot-1)
				{
					break;
				}
			}
		}

		for(int i = count; i < _firstMiddleSlot ; ++i)
		{
			inv.setItem(i, null);		
		}
		
	}
	
	public void setStuffPlayerSlots(Player player, int ATW)
	{
		
		
		player_currentLabel.put(player, ATW);
		setMiddleLINE(player_invs.get(player), ATW);
		
		int shopPages = shopPageCount();
		if(player_currentShopPage.get(player) > shopPages)
		{
			player_currentShopPage.put(player, shopPages);
		}
		
		analysePlayerInv(player);
		setStuffShopSlots(player);
		
		int count = _firstPlayerSlot;
		Inventory inv = player_invs.get(player);

		if(!_shopOnlySell)
		{
			HashMap<ItemStack, Integer> same_stacks = player_stuff.get(player);
			List<ItemStack> stacks = new ArrayList<>();
			for(Map.Entry<ItemStack, Integer> entry : same_stacks.entrySet())
			{
				if(itemM.getPersistenData(entry.getKey(), pd_isArmor, PersistentDataType.INTEGER) == ATW)
				{							
					stacks.add(entry.getKey());
				}
				
			}
			
			Collections.sort(stacks, new Comparator<ItemStack>() 
			{
				public int compare(ItemStack stack1, ItemStack stack2)
				{
					int id1 = Item.getId(CraftItemStack.asNMSCopy(stack1).getItem());
					int id2 = Item.getId(CraftItemStack.asNMSCopy(stack2).getItem());
					return id1 < id2 ? -1 : id1 > id2 ? 1 : 0;
				}
				
			});

			int cur_playerPage = player_currentPlayerPage.get(player);
			int pageCount = (int) Math.round(((stacks.size()-1)/(_size-_firstPlayerSlot))+0.5)-1;

			if(cur_playerPage > pageCount)
			{
				cur_playerPage = pageCount;
				player_currentPlayerPage.put(player, pageCount);
			}
			
			int start = 0 + cur_playerPage * (_size - _firstPlayerSlot) ;
			
			for(int i = start; i < stacks.size(); ++i)
			{
				ItemStack stack = stacks.get(i);

				for(; count < _size;)
				{
					ItemStack c_stack = inv.getItem(count);
					if(c_stack != null && getShopItemType(c_stack) == ShopItemType.EMPTY)
					{
						count++;
					}
					else
					{
						setToolTip(stack,true);	
						inv.setItem(count,stack);						
						count++;
						break;
					}						
				}
				
				if(count > _size-1)
					break;			
			}	
		}

		for(int i = count; i < _size ; ++i)
		{			
			if(!_shopOnlySell)
			{
				if(getShopItemType(inv.getItem(i)) != ShopItemType.EMPTY)
				{
					inv.setItem(i, null);	
				}
				
			}else
			{
				inv.setItem(i, label_icons.get(2));	//black staned glass empty
			}
					
		}
	}
	
	void setToolTip(ItemStack stack, boolean sell)
	{
		
		
		stack.setAmount(1);
		int amount = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
		String action_str = "SELL";
		if(!sell)
		{
			action_str ="BUY ";
		}
		
		ItemStack copy = new ItemStack(stack);
		removeToolTip(copy);
		removeAddedShopPDdata(copy);
		int amount_in_shop = 0;
		for(int i = 0; i < shop_stuff_stacks.size(); ++i)
		{
			ItemStack test = new ItemStack(shop_stuff_stacks.get(i));
			removeToolTip(test);
			removeAddedShopPDdata(test);
			if(test.isSimilar(copy))
			{
				amount_in_shop = itemM.getPersistenData(shop_stuff_stacks.get(i), pd_count, PersistentDataType.INTEGER);
				break;
			} 
			
		}
		
		Double[] prices = calculatePriceOfItem(stack, amount_in_shop, sell);
		itemM.setPersistenData(stack, pd_pone, PersistentDataType.DOUBLE, prices[0]);
		itemM.setPersistenData(stack, pd_peight, PersistentDataType.DOUBLE, prices[1]);
		itemM.setPersistenData(stack, pd_pstack, PersistentDataType.DOUBLE, prices[2]);
		itemM.setPersistenData(stack, pd_pall, PersistentDataType.DOUBLE, prices[3]);
		
		itemM.addLore(stack, ChatColor.AQUA+ "===================", false);
		itemM.addLore(stack, str_lores[3]+prices[3], false);
		itemM.addLore(stack, str_lores[2]+prices[2], false);
		itemM.addLore(stack, str_lores[1]+prices[1], false);
		itemM.addLore(stack, str_lores[0]+prices[0], false);
							
		
		itemM.addLore(stack, ChatColor.GREEN+"==:  "+action_str+" : Price :=====", false);
		itemM.addLore(stack, ChatColor.DARK_PURPLE+"Amount  : "+ChatColor.YELLOW+""+amount, false);
		
		itemM.addLore(stack, ChatColor.AQUA+ "===================", false);
	}
	
	void removeToolTip(ItemStack stack)
	{
		itemM.removeLore(stack, "===================");
		itemM.removeLore(stack, ":=====");
		itemM.removeLore(stack, "Amount  : ");
		itemM.removeLore(stack, str_lores[0]);
		itemM.removeLore(stack, str_lores[1]);
		itemM.removeLore(stack, str_lores[2]);
		itemM.removeLore(stack, str_lores[3]);	
		itemM.removeLore(stack, "===================");
	}
	
	void removeAddedShopPDdata(ItemStack stack)
	{
		itemM.removePersistenData(stack, pd_count);
		itemM.removePersistenData(stack, pd_text);
		itemM.removePersistenData(stack, pd_switcher);
		itemM.removePersistenData(stack, pd_isArmor);
		itemM.removePersistenData(stack, pd_pone);
		itemM.removePersistenData(stack, pd_peight);
		itemM.removePersistenData(stack, pd_pstack);
		itemM.removePersistenData(stack, pd_pall);
		itemM.removePersistenData(stack, pd_type);
		//itemM.removePersistenData(stack, pd_infItem);
		
	}
	
	public double priceCalculation(double levelNow, double maxLevel, double minPrice, double maxPrice)
	{
		double price = 0;
		double maxDmin = maxPrice / minPrice;
		double top = minPrice;
		double lower = Math.pow(maxDmin, 1/(maxLevel-1));
		double end = Math.pow(Math.pow(maxDmin, 1/(maxLevel-1)), levelNow);
		price = (top/lower) * end;	
		
		if(maxLevel == 1)
			price = maxPrice;
		return price;
	}
	
	public Double[] getPrices(Material material,boolean defaultPrice, boolean smartPriceEnabled)
	{
		Double[] prices = {0.0,0.0,0.0};
			
		//System.out.println("get price for : "+material);
		if(!_main.materialPrices.containsKey(material))
		{
			if(!_main.materialCatPrices.containsKey(itemM.getMaterialCategory(material)))
			{
				if(smartPriceEnabled && _main.isEnableSmartPrices())
				{
					if(!_main.getShopManager().smart_prices.containsKey(material))
					{
						if(defaultPrice)	
						{
							prices = _main.getDefault_prices().clone();
						}
					}
					else 
					{
						//System.out.println("smart price found");
						prices = _main.getShopManager().smart_prices.get(material);
					}
				}
				else			
				{					
					if(defaultPrice)	
					{
						prices = _main.getDefault_prices().clone();
					}					
				}				
			}
			else
			{
				//System.out.println("CATEGORICES PRICE!");
				prices = _main.materialCatPrices.get(itemM.getMaterialCategory(material));
			}
		}else
		{
			//System.out.println("Material PRICE!");
			prices = _main.materialPrices.get(material);			
		}
		return prices;
	}
	public Double[] getSmartPrice(ItemStack stack, boolean checkShapless, int iterations)
	{
		int maxItes  = 10;
		if(iterations > maxItes)
		{
			Double[] lock = { 0.0, 0.0, 0.0};
			shopManager.addLookedPrices(stack.getType(), lock.clone());
			return lock;
		}
		Double[] last_prices = getPrices(stack.getType(), false, true).clone();
		if(!itemM.isEveryThingThis(last_prices, 0.0)  && !shopManager.looked_prices.containsKey(stack.getType()))
		{
			return last_prices;
			
		}else
		{
			if(checkShapless)
			{
				last_prices = getSmartPrice_part2(stack,++iterations).clone();
				if(iterations > maxItes)
				{
					Double[] lock = { 0.0, 0.0, 0.0};
					shopManager.addLookedPrices(stack.getType(), lock.clone());
					return lock;
				}
				
				if(!itemM.isEveryThingThis(last_prices, 0.0))
				{
					return last_prices;
				}				
			}
		}
		
		ArrayList<Integer> all_i_keys = new ArrayList<>();
        HashMap<String, HashMap<Material, Integer>> hash = new HashMap<>();
        HashMap<Integer, String> all_i = new HashMap<>();

        hash = getShapedRecipeTree(stack, hash, 0, all_i_keys, all_i);
        
        if(hash.isEmpty() && !checkShapless)
        {
        	return getSmartPrice(stack, true,++iterations).clone();
        }
        Collections.sort(all_i_keys);

        for(int i = all_i_keys.size()-1; i > -1; --i)
        {
        	int key = all_i_keys.get(i);
        	String key2 = all_i.get(key);
        	String[] str_list = all_i.get(key).split(":");
        	
        	int recipeAmount = Integer.parseInt(str_list[0]);
        	Material main_material = Material.getMaterial(str_list[1]);

    		Double[] main_material_price = getPrices(main_material, false, true).clone();

    		if(itemM.isEveryThingThis(main_material_price, 0.0) && !shopManager.looked_prices.containsKey(main_material))
    		{
    			if(!shopManager.smart_prices.containsKey(main_material))
        		{
        			for(Entry<Material, Integer> entry : hash.get(key2).entrySet())
            		{
            			Material sub_material = entry.getKey();
            			int sub_count = entry.getValue();
            			Double[] sub_price = {0.0,0.0,0.0};
            			
        				Double[] get_price = getPrices(sub_material, false, true);

            			if(itemM.isEveryThingThis(get_price, 0.0) && !shopManager.looked_prices.containsKey(sub_material))
            			{
            				get_price = getSmartPrice_part2(new ItemStack(sub_material),++iterations).clone();
            			}
            			
            			sub_price[0] = get_price[0] * sub_count;
            			sub_price[1] = get_price[1] * sub_count;
            			sub_price[2] = get_price[2];
            			                   			
            			main_material_price[0] = main_material_price[0] + sub_price[0];
            			main_material_price[1] = main_material_price[1] + sub_price[1];
            			
            			if(main_material_price[2] < sub_price[2])
            			{
            				main_material_price[2] = sub_price[2];
            			}
            			
            		}
        		}else
        		{
        			main_material_price = shopManager.smart_prices.get(main_material).clone();       			
        		}
    		}
    		
    		main_material_price[0] = main_material_price[0] / recipeAmount;
    		main_material_price[1] = main_material_price[1] / recipeAmount;
        	
    		if(!itemM.isEveryThingThis(main_material_price.clone(), 0.0))
    		{
    			shopManager.addSmartPrice(main_material, main_material_price.clone());
    		}else
    		{
    			shopManager.addLookedPrices(main_material, main_material_price.clone());
    		}
    		last_prices = main_material_price.clone();
    		itemM.setAllThisValue(main_material_price, 0.0);
        }    
        return last_prices;
	}
	
	public HashMap<String, HashMap<Material, Integer>> getShapedRecipeTree(ItemStack stack, HashMap<String, HashMap<Material, Integer>> hash, int i, ArrayList<Integer> all_i_keys, HashMap<Integer, String> all_i)
	{
		for(Recipe r : _main.getServer().getRecipesFor(stack))
		{
			if(r instanceof ShapedRecipe)
			{
				ShapedRecipe sr = (ShapedRecipe)r;
				HashMap<Material, Integer> mats = new HashMap<>();
				String str =sr.getResult().getAmount()+":"+stack.getType().name();
				if(!hash.containsKey(str))
				{
					all_i_keys.add(i);
				}					
				all_i.put(i,str);
				for(ItemStack s : sr.getIngredientMap().values())
				{					
					if(s != null && s.getType() != Material.AIR)
					{
						int count = mats.containsKey(s.getType()) ? mats.get(s.getType()) : 0;
						
						mats.put(s.getType(), ++count);
						getShapedRecipeTree(s, hash, ++i, all_i_keys,all_i);
					}					
				}
				
				hash.put(str, mats);
				break;
			}
		}
		return hash;
	}
	
	Double[] getSmartPrice_part2(ItemStack stack,int iterations)
	{
		if(shopManager.looked_prices.containsKey(stack.getType()))
		{
			return shopManager.looked_prices.get(stack.getType()).clone();
		}
		if(iterations > 10)
		{
			Double[] lock = { 0.0, 0.0, 0.0};
			shopManager.addLookedPrices(stack.getType(), lock.clone());
			return lock;
		}
		
		Double[] last_price =  {0.0,0.0,0.0};
		ArrayList<Integer> all_i_keys = new ArrayList<>();
        HashMap<String, HashMap<Material, Integer>> hash = new HashMap<>();
        HashMap<Integer, String> all_i = new HashMap<>();

        hash = getRecipeShaplessTree(stack, hash, 0, all_i_keys, all_i,iterations);
        Collections.sort(all_i_keys);
        
        for(int i = all_i_keys.size()-1; i > -1; --i)
        {
        	int key = all_i_keys.get(i);
        	String key2 = all_i.get(key);
        	String[] str_list = all_i.get(key).split(":");
        	
        	int recipeAmount = Integer.parseInt(str_list[0]);
        	Material main_material = Material.getMaterial(str_list[1]);
        	
    		Double[] main_material_price = new Double[3];
    		itemM.setAllThisValue(main_material_price, 0.0);
    		if(!shopManager.looked_prices.containsKey(main_material))
    		{
    			for(Entry<Material, Integer> entry : hash.get(key2).entrySet())
        		{
        			Material sub_material = entry.getKey();
        			int sub_count = entry.getValue();
        			Double[] sub_price = shopManager.looked_prices.containsKey(sub_material) 
        					? shopManager.looked_prices.get(sub_material)
        					: getSmartPrice(new ItemStack(sub_material),false,++iterations).clone();
        			if(itemM.isEveryThingThis(sub_price, 0.0))
        			{
        				shopManager.addLookedPrices(sub_material, sub_price);
        			}
        			
        			sub_price[0] = sub_price[0]*sub_count;
        			sub_price[1] = sub_price[1]*sub_count;
        			
        			main_material_price[0] = main_material_price[0] + sub_price[0];
        			main_material_price[1] = main_material_price[1] + sub_price[1];
        			
        			if(main_material_price[2] < sub_price[2])
        			{
        				main_material_price[2] = sub_price[2];
        			}
        		}
    		}
			
			main_material_price[0] = main_material_price[0] / recipeAmount;
    		main_material_price[1] = main_material_price[1] / recipeAmount;
        	
    		if(!itemM.isEveryThingThis(main_material_price.clone(), 0.0))
    		{
    			shopManager.addSmartPrice(main_material, main_material_price.clone());
    		}else
    		{
    			shopManager.addLookedPrices(main_material, main_material_price.clone());
    		}
    		last_price = main_material_price.clone();
    		itemM.setAllThisValue(main_material_price, 0.0);
        }
        return last_price;
	}
	
	public HashMap<String, HashMap<Material, Integer>> getRecipeShaplessTree(ItemStack stack, HashMap<String, HashMap<Material, Integer>> hash, int i, ArrayList<Integer> all_i_keys, HashMap<Integer, String> all_i, int iterations)
	{
		if(i > 15 || iterations > 15)
		{
			return hash;
		}
		for(Recipe r : _main.getServer().getRecipesFor(stack))
		{
			if(r instanceof ShapelessRecipe)
			{
				ShapelessRecipe sr = (ShapelessRecipe)r;
				HashMap<Material, Integer> mats = new HashMap<>();
				String str =sr.getResult().getAmount()+":"+stack.getType().name();
				if(!hash.containsKey(str))
				{
					all_i_keys.add(i);
				}					
				all_i.put(i,str);
				for(ItemStack s : sr.getIngredientList())
				{					
					if(s != null && s.getType() != Material.AIR)
					{
						int count = mats.containsKey(s.getType()) ? mats.get(s.getType()) : 0;						
						mats.put(s.getType(), ++count);
						getRecipeShaplessTree(s, hash, ++i, all_i_keys,all_i, ++iterations);						
					}
					
				}
				hash.put(str, mats);
				break;
			}
		}
		return hash;
	}
	public Double[] materialNEWPrices(ItemStack stack, Double[] lastPrice,int recepiesAmount)
	{
		
		System.out.println("seaching for: "+stack.getType());
		System.out.println("HOW many recepeis: "+ _main.getServer().getRecipesFor(stack).size());
		
		if(itemM.getMaterialCategory(stack.getType()).equalsIgnoreCase("planks")
				|| itemM.getMaterialCategory(stack.getType()).equalsIgnoreCase("wood"))
		{
			System.out.println("ITS PLANk");
			Double[] material_values = 	getPrices(Material.OAK_LOG,false,false);
			lastPrice[0] = lastPrice[0]+(material_values[0]/4);
			lastPrice[1] = lastPrice[1]+material_values[1]/4;
			lastPrice[2] = lastPrice[2]+material_values[2]/4;
			return lastPrice;
		}
				
		for(Recipe r : _main.getServer().getRecipesFor(stack))
		{
			System.out.println("r: "+r);
			if(r instanceof ShapedRecipe)
			{
				ShapedRecipe sr = (ShapedRecipe)r;
				System.out.println("RESULT: "+sr.getResult());
				int amount = sr.getResult().getAmount();
				int count = 0;
				for(ItemStack s : sr.getIngredientMap().values())
				{
					if(s != null)
					{
						
						System.out.println("=========== NEW s: " + s);
						Double[] mav = getPrices(s.getType(), false,false);
						if(mav[0] != 0 || mav[1] != 0 || mav[2] != 0)
						{								
							
							lastPrice[0] = lastPrice[0]+mav[0];
							lastPrice[1] = lastPrice[1]+mav[1];
							lastPrice[2] = lastPrice[2]+mav[2];	
							itemM.printArray("kesel",lastPrice);
						}
						
						
						lastPrice = materialNEWPrices(s, lastPrice, amount); // eka birchs
						itemM.printArray("this "+s.getType().name()+" and others "+ count + " price is before dive ", lastPrice);
						
					}
					
					count++;
				}
				System.out.println("========DIVE by: "+amount+" "+sr.getResult().getType().name());
				lastPrice[0] = lastPrice[0]/amount;
				lastPrice[1] = lastPrice[1]/amount;
				lastPrice[2] = lastPrice[2]/amount;	
				
				break;
			}			
		}
		
		if(lastPrice[0] == 0 && lastPrice[1] == 0 && lastPrice[2] == 0)
		{
			lastPrice = getPrices(stack.getType(), true,false);
			itemM.printArray("Last chance",lastPrice);
		}
		
		return lastPrice;
	}
	
	public Double[] materialPricesDecoder(ItemStack stack, ArrayList<Double[]> values)
	{
		Double[] prices = {0.0,0.0,0.0};
		values.remove(0);
		//values.remove(1);
		
		int size = 1; //values.size(); for savety now 1
		double nextM = 1;
		double highestPros = 0;
		for(int i = 0; i < values.size(); ++i)
		{
			Double[] vals = values.get(i);
			double div = vals[3];
			
			for(int j = 0; j < vals.length-1 ; ++j)
			{
				
				double di = div;
				if(di < 0)
				{
					di = 1;
				}			
				double price = (vals[j]/di)/nextM;				
				prices[j] = (prices[j]+price);
				//System.out.println(j+": "+vals[j]+", di: "+di+", price: "+price+ ", nextM: "+nextM+", last: "+prices[j]);
				
			}
			if(vals[2] > highestPros)
			{
				highestPros = vals[2];
			}
			nextM = 1;
			if(div < 0)
			{
				nextM = Math.abs(div);
			}
		}
		
		int amount = 1;
				
		for(Recipe r : _main.getServer().getRecipesFor(stack))
		{
			if(r instanceof ShapedRecipe)
			{
				ShapedRecipe sr = (ShapedRecipe)r;
				amount  = sr.getResult().getAmount();
				break;
			}
		}
						
		if(size % 2 > 0 || (amount > 6 && size % 2 == 0))
		{
			for(int i = 0; i < prices.length; ++i)
			{
				prices[i] = prices[i]/amount;
			}
		}
		
		prices[2]=highestPros;
		
		//itemM.printArray("Last price2", prices);
		return prices;
	}
	
	public ArrayList<Double[]> materialTreePrices(ItemStack stack, ArrayList<Double[]> values)
	{
		
		//System.out.println("seaching for: "+stack.getType());
		
		if(itemM.getMaterialCategory(stack.getType()).equalsIgnoreCase("planks")
				|| itemM.getMaterialCategory(stack.getType()).equalsIgnoreCase("wood"))
		{
			Double[] material_values = 	getPrices(Material.OAK_LOG,false,false);
			Double[] vals = new Double[4];
			vals[0] =  material_values[0];
			vals[1] =  material_values[1];
			vals[2] =  material_values[2];
			vals[3] = 4.0;
			values.add(vals);
		}else
		{
			for(Recipe r : _main.getServer().getRecipesFor(stack))
			{
				if(r instanceof ShapedRecipe)
				{
					ShapedRecipe sr = (ShapedRecipe)r;
					int amount = sr.getResult().getAmount();
					for(ItemStack s : sr.getIngredientMap().values())
					{
						if(s != null)
						{							
							//System.out.println("=========== NEW s: " + s);
							Double[] mav = getPrices(s.getType(), false, true);
							
							Double[] vals = new Double[4];
							vals[0] =  mav[0];
							vals[1] =  mav[1];
							vals[2] =  mav[2];
							vals[3] = -(double)amount;
							values.add(vals);
							//printPriceArrayList("keskel",values);
							
							if(vals[0] != 0 && vals[1] != 0 && vals[1] != 0)
							{
								
								continue;
							}
							
							values = materialTreePrices(s, values); // eka birchs
							//if(!_main.shopManager.smart_prices.containsKey(s.getType()))
							//{
							//	_main.shopManager.smart_prices.put(s.getType(),values.get(values.size()-1));
							//}
						}						
					}					
					break;
				}			
			}
						
		}
				
		Double[] vals = values.get(values.size()-1);
		if(vals[0] == 0 && vals[1] == 0 && vals[2] == 0 && vals[3]==0)
		{
			Double[] mav = getPrices(stack.getType(), false,true);
			Double[] val = new Double[4];
			val[0] =  mav[0];
			val[1] =  mav[1];
			val[2] =  mav[2];
			val[3] = 1.0;
			values.add(val);
		}
		
		//printPriceArrayList("OUT", values);
				
		return values;
	}
	
	
	public Double[] calculatePriceOfItem(ItemStack stack,int amount_inShop, boolean sell)
	{
		Double[] prices = {0.0,0.0,0.0,0.0};
		
		if(stack == null || stack.getType()== Material.AIR)
		{
			return prices;
		}
		if(!sell)
		{
			amount_inShop = 0;
		}
		
		double enchantcost = 0;
		if(stack.hasItemMeta())
		{
			ItemMeta meta = stack.getItemMeta();
			
			for(Map.Entry<Enchantment, Integer> ench : meta.getEnchants().entrySet())
			{
				if(enchantsManager.isEnchPriced(ench.getKey()))
				{
					Double[] values = enchantsManager.getEnchPrice(ench.getKey());
					double calp = 0;
					if( values[2] != 0 ||  values[3] != 0)
					{
						calp = priceCalculation(ench.getValue(), values[1], values[2], values[3]);
					}					
					enchantcost += calp;					
				}
			}
		}
				
		int total_amount = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
		Double[] material_values = {0.0,0.0,0.0};
		
		boolean isUnique = false;
		ItemStack uniqueTest = new ItemStack(stack);
		
		removeAddedShopPDdata(uniqueTest);
		removeInfItemPd(uniqueTest);
		
		Double[] uniquePrice = _main.getShopManager().getUniqueItemPrice(uniqueTest);

		if(uniquePrice == null)
		{
			if(_main.isEnableSmartPrices() && !_main.isLoadSmartPricesUpFront())
			{
				material_values = getSmartPrice(stack, false, 0).clone();
			}else
			{
				material_values = getPrices(stack.getType(), true, true).clone();//min,max,pros
			}
		}else
		{
			isUnique = true;
			material_values = uniquePrice;
			enchantcost = 0;
		}
	
		double material_values2 = material_values[2]/100;
		
		double materialCost_all = 0 + enchantcost;
		double materialCost_one = 0 + enchantcost;
		double materialCost_eight = 0 + enchantcost;
		double materialCost_stack = 0 + enchantcost;
		
		boolean lock = false;
		for(int i = 0 + amount_inShop; i < total_amount+amount_inShop; ++i)
		{
			double cost = material_values[0];
			if(!lock)
			{
				if(sell)
				{			
					cost = material_values[1] * Math.pow((1.0-material_values2),i);
					//System.out.println("cost: "+cost);
				}else
				{
					cost = material_values[1];
				}
				
				if(cost < material_values[0])
				{
					lock=true;
					cost = material_values[0];
				}
					
			}
						
			if(i == 0 + amount_inShop)
			{
				materialCost_one +=cost;
			}
			
			if(i < 8 + amount_inShop)
			{
				materialCost_eight+=cost;
			}
			
			if(i < 64 + amount_inShop)
			{
				materialCost_stack+=cost;
			}
			
			materialCost_all += cost;
		}
		
		double durProsent = itemM.getDurabilityProsent(stack);
		if(durProsent != 1)
		{
			durProsent = durProsent * _main.getDurabilityCostMultiplier();
			if(durProsent <= 0)
			{
				durProsent = 1;
			}
		}
		
		prices[0]=Math.round((materialCost_one   * durProsent)* 100.0) / 100.0;
		prices[1]=Math.round((materialCost_eight   * durProsent)* 100.0) / 100.0;
		prices[2]=Math.round((materialCost_stack * durProsent)* 100.0) / 100.0;
		prices[3]=Math.round((materialCost_all   * durProsent)* 100.0) / 100.0;
		
		
		
		if(!sell)
		{
			for(int i = 0; i < prices.length; ++i)
			{				
				if(!isUnique)
				{
					prices[i]=(double)Math.round(prices[i] *_main.getSellProsent()*100)/100;
				}else
				{
					prices[i]=(double)Math.round(prices[i]*100)/100;
				}
				
			}
		}
		
		return prices;
	}
	
	public void putInfItemToShop(ItemStack stack)
	{
		ItemStack copy = new ItemStack(stack);
		putItemToShop(copy, stack.getAmount(), true);
	}
	
	public void removeInfItemFromShop(ItemStack stack)
	{
		ItemStack copy = new ItemStack(stack);
		setStackInfItem(copy);
		putItemToPlayerInv(null, copy, 1, true);
	}
	
	void putItemToShop(ItemStack stack, int amount,boolean isInfinity)
	{
		boolean found = false;
		stack.setAmount(1);
		removeToolTip(stack);
		removeAddedShopPDdata(stack);
		
		if(isInfinity)
		{
			//itemM.setPersistenData(stack, pd_infItem, PersistentDataType.INTEGER, 1);
			setStackInfItem(stack);
		}
		
		for(int i = 0; i < shop_stuff_stacks.size(); ++i)
		{
			
			ItemStack shop_stack = shop_stuff_stacks.get(i);
			int shop_amount = getShopStackAmount(shop_stack);
			shop_stack.setAmount(1);
			removeAddedShopPDdata(shop_stack);
			removeToolTip(stack);
			if(shop_stack.isSimilar(stack))
			{
				int count = shop_amount+amount;
				
				shop_stuff_stacks.set(i, shop_stack);
				shop_stuff_values.set(i, count);
				
				found = true;
				break;
			}
		}
		if(!found)
		{

			shop_stuff_stacks.add(stack);
			shop_stuff_values.add(amount);
		}
		
		for(int i = 0; i < shop_stuff_stacks.size(); ++i)
		{
			itemM.setPersistenData(shop_stuff_stacks.get(i), pd_count, PersistentDataType.INTEGER, shop_stuff_values.get(i));
		}
		
		for(Player p : player_invs.keySet())
		{
			setStuffPlayerSlots(p, player_currentLabel.get(p));
		}
		
	}
	
	public void analysePlayerInv(Player player)
	{
		player_stuff.remove(player);
		player_refs.remove(player);
		
		HashMap<ItemStack, Integer> same_stacks=new HashMap<>();
		HashMap<Material, Integer> materials = new HashMap<>();
		HashMap<ItemStack, ArrayList<ItemStack>> stack_refs=new HashMap<>();
	
		for(ItemStack s : player.getInventory().getContents())
		{
			if(s != null)
			{
				ItemStack stack = new ItemStack(s);
				boolean found = false;
				if(itemM.findLoreIndex(stack, "Soulbound") > -1)
				{
					continue;
				}
				
				if(itemM.isArmor(stack) || itemM.isTool(stack))
				{
					itemM.setPersistenData(stack, pd_isArmor, PersistentDataType.INTEGER, (int)1);
				}else
				{
					itemM.setPersistenData(stack, pd_isArmor, PersistentDataType.INTEGER, (int)0);
				}
				
				if(materials.containsKey(stack.getType()))
				{
					
					for(Map.Entry<ItemStack, Integer> entry : same_stacks.entrySet())
					{
						ItemStack entry_stack = entry.getKey();
						if(entry_stack.isSimilar(stack))
						{
							int count = entry.getValue()+stack.getAmount();
							
							same_stacks.put(entry_stack, count);

							for(Map.Entry<ItemStack, ArrayList<ItemStack>> entryyy : stack_refs.entrySet())
							{
								if(entryyy.getKey().isSimilar(stack))
								{
									//System.out.println("similar found first");
									entryyy.getValue().add(s);
									break;
								}
							}

							found = true;
							break;
						}else
						{
							found = false;	
						}
												
					}
				}
				if(!found)
				{
					//new item add					
					same_stacks.put(stack, stack.getAmount());
					ArrayList<ItemStack> ref_items = new ArrayList<ItemStack>();
					ref_items.add(s);
					stack_refs.put(stack, ref_items);
					materials.put(stack.getType(), 1);
				}
				
			}			
		}
		
		for(Map.Entry<ItemStack, Integer> entry : same_stacks.entrySet())
		{
			itemM.setPersistenData(entry.getKey(), pd_count, PersistentDataType.INTEGER, entry.getValue());
		}
		
		player_stuff.put(player,same_stacks);
		player_refs.put(player,stack_refs);
		
	}
}

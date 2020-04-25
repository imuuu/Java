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
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
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
import net.minecraft.server.v1_15_R1.Item;


public class Shop implements Listener
{
	String _displayName = "";
	String _name = "";
	String _fileNameShopYML="";
	String _fileNameSelledMaterialCount="";
	int _size=9*6;
	

	HashMap<Player,Inventory> player_invs = new HashMap<>();
	HashMap<Player,Integer> player_currentLabel = new HashMap<>();
	HashMap<Player,Integer> player_currentShopPage = new HashMap<>();
	HashMap<Player,Integer> player_currentPlayerPage = new HashMap<>();
	
	HashMap<Player,Integer> player_clicks = new HashMap<>();
	HashMap<Player,Integer> player_clicks_warnings = new HashMap<>();
	HashMap<Player,BukkitRunnable> player_runnables = new HashMap<>();
	
	
	ItemMetods itemM = new ItemMetods();
	
	Main _main = Main.getInstance();
	Economy _econ = Main.getEconomy();
	ShopManager shopManager=null;
	
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
	String pd_pstack= "gs.priceStack";
	String pd_pall= "gs.priceAll";
	String pd_infItem="gs.infItem";
	
	int _maxClicksInHalfSecond=10/2;
	
	Cooldowns cds = new Cooldowns();
	String cdName = "expireTime";
	
	//DATA Gather
	ArrayList<Pair<Material, Integer> > sellCountValues = new ArrayList<>();
	
	
	public Shop(String shopName,boolean realShop) 
	{
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
		
	void setLabelIcons()
	{
		ItemStack armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack stuff = new ItemStack(Material.STONE);
		
		itemM.setPersistenData(armor, pd_switcher, PersistentDataType.INTEGER, (int)1);
		itemM.setPersistenData(stuff, pd_switcher, PersistentDataType.INTEGER, (int)1);
		
		itemM.setPersistenData(armor, pd_text, PersistentDataType.STRING, ChatColor.AQUA + "Armor, Tools, Weapons");
		itemM.setPersistenData(stuff, pd_text, PersistentDataType.STRING, ChatColor.AQUA + "Other stuff");

		label_icons.put(0,armor);
		label_icons.put(1,stuff);
		
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
				//System.out.println("run");
				
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
			System.out.println("ITS inf stack");
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
			System.out.println("SOME SHOP IS OPEN");
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
		int pages =(int) Math.round((shop_stuff_stacks.size()/(_firstMiddleSlot))+0.5);
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
		
		//player_runnables.remove(player);		
	}
	
	@EventHandler
	public void invOpen(InventoryOpenEvent e)
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
	public void invClose(InventoryCloseEvent e)
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
	public void invClickEvent(InventoryClickEvent e)
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
					int warnings_count = player_clicks_warnings.containsKey(player) ? player_clicks_warnings.get(player) : 0;
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
								
				//shop side
				if(raw_slot > -1 && raw_slot < 27)
				{					
					//BUY FROM SHOP
					int stack_count = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
					
					double price_one = itemM.getPersistenData(stack, pd_pone, PersistentDataType.DOUBLE);
					if(click == ClickType.LEFT && withdrawPlayerHasMoney(player, price_one))
					{
						putItemToPlayerInv(player, stack, 1, false);
						return;
					}
					
					double price_stack = itemM.getPersistenData(stack, pd_pstack, PersistentDataType.DOUBLE);
					if(click == ClickType.RIGHT && withdrawPlayerHasMoney(player, price_stack))
					{
						int c = 64;
						if(stack_count < 64)
							c = stack_count;
						putItemToPlayerInv(player, stack, c,false);
						return;
					}
					
					double price_all = itemM.getPersistenData(stack, pd_pall, PersistentDataType.DOUBLE);
					if(click == ClickType.MIDDLE && withdrawPlayerHasMoney(player, price_all))
					{
						putItemToPlayerInv(player, stack, stack_count,false);
						return;
					}
				}
				
				//player side in shop
				if(raw_slot > _firstPlayerSlot-1 && raw_slot < _size)
				{
					//SELL TO THE SHOP
					int stack_count = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
					double price_one = itemM.getPersistenData(stack, pd_pone, PersistentDataType.DOUBLE);
					if(click == ClickType.LEFT)
					{						
						removeItemPlayerInv(player, stack, 1);
						putItemToShop(stack, 1,false);
						depositMoney(player, price_one);
						return;
						
					}
					
					double price_stack = itemM.getPersistenData(stack, pd_pstack, PersistentDataType.DOUBLE);
					if(click == ClickType.RIGHT)
					{
						int c = 64;
						if(stack_count < 64)
							c = stack_count;
						
						removeItemPlayerInv(player, stack, c);
						putItemToShop(stack, c,false);
						depositMoney(player, price_stack);
						return;
					}
					
					double price_all = itemM.getPersistenData(stack, pd_pall, PersistentDataType.DOUBLE);
					if(click == ClickType.MIDDLE)
					{
						removeItemPlayerInv(player, stack, stack_count);
						putItemToShop(stack, stack_count,false);
						depositMoney(player, price_all);
						return;
					}
					
				}
				if(raw_slot > _firstMiddleSlot-1 && raw_slot < _firstPlayerSlot)
				{
					//mid panel
					Integer data = itemM.getPersistenData(stack, pd_switcher, PersistentDataType.INTEGER);
					if(data != null && data == 1)
					{					
						setStuffPlayerSlots(player, switcher2000(player_currentLabel.get(player)));
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
								setStuffPlayerSlots(player, player_currentLabel.get(player));
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
								setStuffPlayerSlots(player, player_currentLabel.get(player));
							}
						}
					}
					data = itemM.getPersistenData(stack, pd_playerSwitchButton, PersistentDataType.INTEGER);
					if(data != null && data != 0)
					{
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
								setStuffPlayerSlots(player, player_currentLabel.get(player));
							}
						}
						
						if(data == 1)
						{
							int page_next = cur_player_page + 1;							
							player_currentPlayerPage.put(player, page_next);
							setStuffPlayerSlots(player, player_currentLabel.get(player));
						}
					}
				}
			}
		}
		
	}
	public void depositMoney(Player player, double price)
	{
		EconomyResponse res = _econ.depositPlayer(player, price);
		player.sendMessage(ChatColor.DARK_PURPLE+ "You have received: "+ ChatColor.GOLD +""+res.amount+ ""+_econ.currencyNameSingular());
	}
	public boolean withdrawPlayerHasMoney(Player player, double price)
	{

		double balance = _econ.getBalance(player);
		balance = Math.round(balance*100)/100;
		if(balance > price)
		{
			_econ.withdrawPlayer(player, price);
			player.sendMessage(ChatColor.DARK_PURPLE+ "Your balance now: "+ ChatColor.GOLD +""+balance);
			return true;
		}
		player.sendMessage(ChatColor.RED + "You don't have enough money!");
		player.sendMessage(ChatColor.DARK_PURPLE+ "Your balance is: "+ ChatColor.GOLD +""+balance+" "+ChatColor.DARK_PURPLE+"and that cost you: "+ChatColor.GOLD+""+price);
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
		for(; i < shop_stuff_stacks.size() ; ++i)
		{
			ItemStack s = new ItemStack(shop_stuff_stacks.get(i));
			removeAddedShopPDdata(s);
			removeToolTip(s);
			
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
			System.out.println("COPY was null");
			return;
		}
		
		int num = add_amount / 64;
		int remainder = add_amount % 64;
				
		copy.setAmount(64);
		for(int j = 0; j < num; ++j)
		{
			itemM.moveItemFirstFreeSpaceInv(copy, player, true);			
		}
		
		if(remainder > 0)
		{
			copy.setAmount(remainder);
			itemM.moveItemFirstFreeSpaceInv(copy, player, true);
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
	
	void removeItemPlayerInv(Player player,ItemStack stack, int remove_amount)
	{
		configSelledItemCount(stack, remove_amount);
		configSellLogItem(player, stack, remove_amount);
		HashMap<ItemStack, ArrayList<ItemStack>> refs = player_refs.get(player);
		
		for(Map.Entry<ItemStack, ArrayList<ItemStack>> entry : refs.entrySet())
		{
			if(entry.getKey().isSimilar(stack))
			{
				for(ItemStack s : entry.getValue())
				{
					int stack_total = s.getAmount()-remove_amount;
					if(stack_total < 0)
					{
						remove_amount = Math.abs(stack_total);
						s.setAmount(0);
					}else
					{
						s.setAmount(stack_total);
						break;
					}
					
				}
				break;
			}
		}
		
		setStuffPlayerSlots(player, player_currentLabel.get(player));
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
		ItemStack empty = new ItemStack(Material.AIR);
		if(shop_stuff_stacks.isEmpty())
		{
			for(int i = _firstShopSlot; i < _firstMiddleSlot ; ++i)
			{
				for(Inventory inv : player_invs.values())
				{
					inv.setItem(i, empty);
				}
				
			}
			shop_stuff_values.clear();
			return;
		}
		int start =_firstShopSlot + player_currentShopPage.get(player) * _firstMiddleSlot;
		int count = _firstShopSlot;
		
		if(shop_stuff_stacks.size() > 0)
		{
			for(int i = start ; i < shop_stuff_stacks.size(); ++i)
			{
				ItemStack stack = shop_stuff_stacks.get(i);
				if(getShopStackAmount(stack) <= 0)
				{
					shop_stuff_stacks.remove(i);
					shop_stuff_values.remove(i);
				}
			}
			for(int i = start ; i < shop_stuff_stacks.size(); ++i)
			{
				ItemStack stack = shop_stuff_stacks.get(i);
				
				
				ItemStack copy = new ItemStack(stack);
				setToolTip(copy,false);
				
				for(Inventory inv : player_invs.values())
				{
					inv.setItem(count, copy);
				}
				
				count++;
				
				if(count > _firstMiddleSlot-1)
				{
					break;
				}
			}
		}
		
		
		for(int i = count; i < _firstMiddleSlot ; ++i)
		{
			for(Inventory inv : player_invs.values())
			{
				inv.setItem(i, empty);
			}
			
		}
		
	}
	
	public void setStuffPlayerSlots(Player player, int ATW)
	{
		player_currentLabel.put(player, ATW);
		setMiddleLINE(player_invs.get(player), ATW);
		
		analysePlayerInv(player);
		setStuffShopSlots(player);
		
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
		int count = _firstPlayerSlot;
		Inventory inv = player_invs.get(player);
		for(int i = start; i < stacks.size(); ++i)
		{
			ItemStack stack = stacks.get(i);
			
			setToolTip(stack,true);				
			inv.setItem(count,stack);				
			count++;
			
			if(count >_size-1)
				break;			
		}
		
	
		ItemStack empty = new ItemStack(Material.AIR);
		for(int i = count; i < _size ; ++i)
		{			
			inv.setItem(i, empty);			
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
		itemM.setPersistenData(stack, pd_pstack, PersistentDataType.DOUBLE, prices[1]);
		itemM.setPersistenData(stack, pd_pall, PersistentDataType.DOUBLE, prices[2]);
		
		itemM.addLore(stack, ChatColor.AQUA+ "===================", false);
		itemM.addLore(stack, ChatColor.GREEN+ "M3:"+ChatColor.DARK_PURPLE+" ALL   : "+ChatColor.GOLD+" "+prices[2], false);
		itemM.addLore(stack, ChatColor.GREEN+ "M2:"+ChatColor.DARK_PURPLE+" STACK: "+ChatColor.GOLD+" "+prices[1], false);
		itemM.addLore(stack, ChatColor.GREEN+ "M1:"+ChatColor.DARK_PURPLE+" ONE   : "+ChatColor.GOLD+" "+prices[0], false);
							
		
		itemM.addLore(stack, ChatColor.GREEN+"==: "+action_str+" : Price :=====", false);
		itemM.addLore(stack, ChatColor.DARK_PURPLE+"Amount  : "+ChatColor.YELLOW+""+amount, false);
		
		itemM.addLore(stack, ChatColor.AQUA+ "===================", false);
	}
	
	void removeToolTip(ItemStack stack)
	{
		itemM.removeLore(stack, "===================");
		itemM.removeLore(stack, "===================");
		itemM.removeLore(stack, "ALL");
		itemM.removeLore(stack, "STACK");
		itemM.removeLore(stack, "ONE");
		itemM.removeLore(stack, "Amount  : ");
		itemM.removeLore(stack, ":=====");
	}
	
	void removeAddedShopPDdata(ItemStack stack)
	{
		itemM.removePersistenData(stack, pd_count);
		itemM.removePersistenData(stack, pd_text);
		itemM.removePersistenData(stack, pd_switcher);
		itemM.removePersistenData(stack, pd_isArmor);
		itemM.removePersistenData(stack, pd_pone);
		itemM.removePersistenData(stack, pd_pstack);
		itemM.removePersistenData(stack, pd_pall);
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
							System.out.println("Default price2 ");
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
	
	
	void printPriceArrayList(String id,ArrayList<Double[]> values)
	{
		for(Double[] ds : values)
		{
			System.out.println("===== "+id+" =====");
			for(Double d: ds)
			{
				System.out.println("vals: "+d);
			}
			System.out.println("===== "+id+" =====");
		}
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
	
	public void oldtesting()
	{
		Double[] test2 = {0.0,0.0,0.0,0.0};
		ArrayList<Double[]> test = new ArrayList<>();
		test.add(test2);
		
		ItemStack copy = new ItemStack(Material.AIR);
		itemM.setDamage(copy, 0);
		test = materialTreePrices(copy, test);
		//Double[] ds = materialPricesDecoder(copy,test);
	}
	public Double[] calculatePriceOfItem(ItemStack stack,int amount_inShop, boolean sell)
	{
		Double[] prices = {0.0,0.0,0.0};
		
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
				if(_main.enchPrices.containsKey(ench.getKey()))
				{
					Double[] values=_main.enchPrices.get(ench.getKey());
					double calp = priceCalculation(ench.getValue(), values[1], values[2], values[3]);
					enchantcost += calp;					
				}
			}
		}
				
		int total_amount = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
		Double[] material_values = {0.0,0.0,0.0};
		ItemStack uniqueTest = new ItemStack(stack);
		removeAddedShopPDdata(uniqueTest);
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
			System.out.println("UNIQUE PRICE");
			material_values = uniquePrice;
			enchantcost = 0;
		}
	
		double material_values2 = material_values[2]/100;
		
		double materialCost_all = 0 + enchantcost;
		double materialCost_one = 0 + enchantcost;
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
			if(i < 64+amount_inShop)
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
		prices[1]=Math.round((materialCost_stack * durProsent)* 100.0) / 100.0;
		prices[2]=Math.round((materialCost_all   * durProsent)* 100.0) / 100.0;
		
		
		
		if(!sell)
		{
			for(int i = 0; i < prices.length; ++i)
			{						
				prices[i]=(double)Math.round(prices[i] *_main.getSellProsent()*100)/100;
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
			itemM.setPersistenData(stack, pd_infItem, PersistentDataType.INTEGER, 1);
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

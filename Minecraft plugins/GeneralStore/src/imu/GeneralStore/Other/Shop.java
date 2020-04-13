package imu.GeneralStore.Other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.GeneralStore.main.Main;
import net.minecraft.server.v1_15_R1.Item;


public class Shop implements Listener
{
	String _name = "";
	String _displayName ="";
	String _fileNameShop="";
	int _size=9*6;
	

	HashMap<Player,Inventory> player_invs = new HashMap<>();
	
	ItemMetods itemM= new ItemMetods();
	Main _main = Main.getInstance();
	
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
	
	HashMap<Player,Integer> player_currentLabel = new HashMap<>();
	int _currentLbel = LABELS.STUFF.getType();
	
	public Shop(String shopName) 
	{
		_name = ChatColor.stripColor(shopName);
		_displayName=shopName;
		_fileNameShop = "shop_"+ChatColor.stripColor(shopName)+".yml";	
		_main.getServer().getPluginManager().registerEvents(this, _main);
		setupConfig();
		setLabelIcons();
		//makeShop();
	}
	
	@SuppressWarnings("unchecked")
	void setupConfig()
	{
		ConfigMaker cm = new ConfigMaker(_main, _fileNameShop);
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
		ConfigMaker cm = new ConfigMaker(_main, _fileNameShop);
		FileConfiguration config = cm.getConfig();
		if(cm.isExists())
		{
			config.set("shop_stacks", shop_stuff_stacks);
			config.set("shop_values",shop_stuff_values);
			cm.saveConfig();
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
	
	
	public String getName()
	{
		return _name;
	}
	
	void makeShop(Player player)
	{
		Inventory inv = _main.getServer().createInventory(null, _size, _name);
		
		
		ItemStack panel = itemM.setDisplayName(
				new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.GOLD+"LINE");		
				
		for(int i = 28; i < _firstPlayerSlot-1; ++i)
		{
			inv.setItem(i, panel);
		}
		
		player_invs.put(player, inv);
		//0-26 => shop items
		//36-54 => player items
	}
	
	void setMiddleLINE(Inventory inv, int label)
	{
		ItemStack button = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
		ItemStack label_icon = label_icons.get(label);
		inv.setItem((_firstMiddleSlot+35)/2, itemM.setDisplayName(label_icon,itemM.getPersistenData(label_icon, pd_text, PersistentDataType.STRING) ));
		inv.setItem(_firstMiddleSlot, itemM.setDisplayName(button, ChatColor.AQUA + "GO LEFT"));
		inv.setItem(35, itemM.setDisplayName(button, ChatColor.AQUA + "GO RIGHT"));
	}
	
	public void openShopInv(Player player)
	{
		makeShop(player);
		player.openInventory(player_invs.get(player));
	}
	
	@EventHandler
	public void invOpen(InventoryOpenEvent e)
	{
		System.out.println("Inv OPEN"+ e.getInventory());
		if(e.getPlayer() instanceof Player)
		{		
			Player player = (Player) e.getPlayer();
		//	Inventory inv = e.getInventory();
			InventoryView view = e.getView();
			if(view.getTitle().equalsIgnoreCase(_name))
			{
				System.out.println("Inv opened: " + _name);
				player_currentLabel.put(player, LABELS.STUFF.getType());
				//_currentLabel=LABELS.STUFF.getType();
				setStuffPlayerSlots(player, LABELS.STUFF.getType());			
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
			
			if(view.getTitle().equalsIgnoreCase(_name))
			{
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
					int stack_count = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
					
					if(click == ClickType.LEFT)
					{
						putItemToPlayerInv(player, stack, 1);
					}
					
					if(click == ClickType.RIGHT)
					{
						int c = 64;
						if(stack_count < 64)
							c = stack_count;
						putItemToPlayerInv(player, stack, c);
					}
					
					if(click == ClickType.MIDDLE)
					{
						putItemToPlayerInv(player, stack, stack_count);
					}
				}
				
				//player side in shop
				if(raw_slot > _firstPlayerSlot-1 && raw_slot < _size)
				{
					int stack_count = itemM.getPersistenData(stack, pd_count, PersistentDataType.INTEGER);
					
					if(click == ClickType.LEFT)
					{						
						removeItemPlayerInv(player, stack, 1);
						putItemToShop(stack, 1);
						
					}
					
					if(click == ClickType.RIGHT)
					{
						int c = 64;
						if(stack_count < 64)
							c = stack_count;
						
						removeItemPlayerInv(player, stack, c);
						putItemToShop(stack, c);
					}
					
					if(click == ClickType.MIDDLE)
					{
						removeItemPlayerInv(player, stack, stack_count);
						putItemToShop(stack, stack_count);
					}
					
				}
				
				Integer data = itemM.getPersistenData(stack, pd_switcher, PersistentDataType.INTEGER);
				if(data != null && data == 1)
				{					
					setStuffPlayerSlots(player, switcher2000(player_currentLabel.get(player)));
				}
				
				
			}
		}
		
	}
	
	void putItemToPlayerInv(Player player, ItemStack stack, int add_amount)
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
				
				int amount = itemM.getPersistenData(shop_stuff_stacks.get(i), pd_count, PersistentDataType.INTEGER);
				int total = amount - add_amount;
				
				if(total < 1)
				{
					add_amount = add_amount + total;
					total = 0;
					remove = true;
				}

				itemM.setPersistenData(shop_stuff_stacks.get(i), pd_count, PersistentDataType.INTEGER, total);				
				break;
			}
		}
		
		if(remove)
		{
			shop_stuff_stacks.remove(i);
			shop_stuff_values.remove(i);
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
		
		setStuffPlayerSlots(player, player_currentLabel.get(player));

	}
	
	void removeItemPlayerInv(Player player,ItemStack stack, int remove_amount)
	{
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
	public void setStuffShopSlots()
	{
		ItemStack empty = new ItemStack(Material.AIR);
		if(shop_stuff_stacks.isEmpty())
		{
			for(int i = _firstShopSlot; i < _firstMiddleSlot-1 ; ++i)
			{
				for(Inventory inv : player_invs.values())
				{
					inv.setItem(i, empty);
				}
				
			}
			return;
		}
		
		int count = _firstShopSlot;
		if(shop_stuff_stacks.size() > 0)
		{
			for(int i = 0 ; i < shop_stuff_stacks.size(); ++i)
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
		
		
		for(int i = count; i < _firstMiddleSlot-1 ; ++i)
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
		setStuffShopSlots();
				
		int count = _firstPlayerSlot;
		HashMap<ItemStack, Integer> same_stacks = player_stuff.get(player);
		List<ItemStack> stacks = new ArrayList<>();
		for(Map.Entry<ItemStack, Integer> entry : same_stacks.entrySet())
		{
			stacks.add(entry.getKey());
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
		
		for(ItemStack stack : stacks)
		{
			if(itemM.getPersistenData(stack, pd_isArmor, PersistentDataType.INTEGER) == ATW)
			{							
				setToolTip(stack,true);
				
				for(Inventory inv : player_invs.values())
				{
					inv.setItem(count,stack);
				}
				
				
				count++;
			}
			
			if(count >_size)
				break;			
		}
		
		ItemStack empty = new ItemStack(Material.AIR);
		for(int i = count; i < _size ; ++i)
		{
			for(Inventory inv : player_invs.values())
			{
				inv.setItem(i, empty);
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
		
		
		itemM.addLore(stack, ChatColor.AQUA+ "===================", false);
		itemM.addLore(stack, ChatColor.GREEN+ "M3:"+ChatColor.DARK_PURPLE+" ALL   : "+ChatColor.GOLD+" "+0, false);
		itemM.addLore(stack, ChatColor.GREEN+ "M2:"+ChatColor.DARK_PURPLE+" STACK: "+ChatColor.GOLD+" "+0, false);
		itemM.addLore(stack, ChatColor.GREEN+ "M1:"+ChatColor.DARK_PURPLE+" ONE   : "+ChatColor.GOLD+" "+0, false);
							
		
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
		
	}
	
	void putItemToShop(ItemStack stack, int amount)
	{
		boolean found = false;
		stack.setAmount(1);
		removeToolTip(stack);
		removeAddedShopPDdata(stack);
		for(int i = 0; i < shop_stuff_stacks.size(); ++i)
		{
			
			ItemStack shop_stack = shop_stuff_stacks.get(i);
			shop_stack.setAmount(1);
			removeAddedShopPDdata(shop_stack);
			removeToolTip(stack);
			if(shop_stack.isSimilar(stack))
			{
				int count = shop_stuff_values.get(i)+amount;
				
				shop_stuff_stacks.set(i, shop_stack);
				shop_stuff_values.set(i, count);
				
				found=true;
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
		
		setStuffShopSlots();
	}
	
	public void analysePlayerInv(Player player)
	{
		player_stuff.remove(player);
		player_refs.remove(player);
		
		HashMap<ItemStack, Integer> same_stacks=new HashMap<>();
		HashMap<Material, Integer> materials = new HashMap<>();
		HashMap<ItemStack, ArrayList<ItemStack>> stack_refs=new HashMap<>();
		//System.out.println("Analyzing");
		
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

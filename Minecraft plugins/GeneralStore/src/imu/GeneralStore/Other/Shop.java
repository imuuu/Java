package imu.GeneralStore.Other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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


public class Shop implements Listener
{
	String _name = "";
	String _invContentYAML="";
	int _size=9*6;
	
	Inventory _inv;
	ItemMetods itemM= new ItemMetods();
	Main _main = Main.getInstance();
	
	int _firstPlayerSlot=36;
	int _firstShopSlot=0;
	
	
	HashMap<Player,HashMap<ItemStack, Integer>> player_stuff=new HashMap<>();
	HashMap<Player,HashMap<ItemStack, ArrayList<ItemStack>>> player_refs=new HashMap<>();
	
	HashMap<Integer,ItemStack> label_icons = new HashMap<>();
	
	String pd_isArmor = "gs.Isarmor";
	String pd_switcher= "gs.Switch";
	String pd_text = "gs.text";
	
	int _currentLabel = LABELS.STUFF.getType();
	
	public Shop(String shopName) 
	{
		_name = shopName;
		_invContentYAML = _main.playerInvContentYAML;	
		_main.getServer().getPluginManager().registerEvents(this, _main);
		setLabelIcons();
		makeShop();
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
	enum LABELS
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
	
	void makeShop()
	{
		_inv = _main.getServer().createInventory(null, _size, _name);
		ItemStack panel = itemM.setDisplayName(
				new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.GOLD+"LINE");		
		
		
		
		for(int i = 28; i < _firstPlayerSlot-1; ++i)
		{
			_inv.setItem(i, panel);
		}
		
		
		//0-26 => shop items
		//36-54 => player items
	}
	
	void setMiddleLINE(int label)
	{
		ItemStack button = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
		ItemStack label_icon = label_icons.get(label);
		_inv.setItem((27+35)/2, itemM.setDisplayName(label_icon,itemM.getPersistenData(label_icon, pd_text, PersistentDataType.STRING) ));
		_inv.setItem(27, itemM.setDisplayName(button, ChatColor.AQUA + "GO LEFT"));
		_inv.setItem(35, itemM.setDisplayName(button, ChatColor.AQUA + "GO RIGHT"));
	}
	
	public void openShopInv(Player player)
	{
		player.openInventory(_inv);
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
				_currentLabel=LABELS.STUFF.getType();
				setStuffPlayerSlots(player, _currentLabel);
				
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
				
				if(raw_slot > -1 && raw_slot < 27)
				{
					System.out.println("SHOP SLOTS");
				}
				
				if(raw_slot > _firstPlayerSlot-1 && raw_slot < _size)
				{
					System.out.println("Player shop slots");
					if(click == ClickType.LEFT)
					{
						System.out.println("left click");							
						removeItemPlayerInv(player, stack, 1);
					}
					
					if(click == ClickType.RIGHT)
					{
						removeItemPlayerInv(player, stack, 64);
					}
					
				}
				
				Integer data = itemM.getPersistenData(stack, pd_switcher, PersistentDataType.INTEGER);
				if(data != null && data == 1)
				{
					
					setStuffPlayerSlots(player, switcher2000(_currentLabel));
				}
				
				
			}
		}
		
	}
	
	void removeItemPlayerInv(Player player,ItemStack stack, int remove_amount)
	{
		HashMap<ItemStack, ArrayList<ItemStack>> refs = player_refs.get(player);
		
		for(Map.Entry<ItemStack, ArrayList<ItemStack>> entry : refs.entrySet())
		{
			if(entry.getKey().isSimilar(stack))
			{
				System.out.println("similar found");
				for(ItemStack s : entry.getValue())
				{
					System.out.println("s: "+s);
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
		
		setStuffPlayerSlots(player, _currentLabel);
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
	public void setStuffPlayerSlots(Player player, int ATW)
	{
		_currentLabel = ATW;
		setMiddleLINE(ATW);
		
		analysePlayerInv(player);
				
		int count = _firstPlayerSlot;
		HashMap<ItemStack, Integer> same_stacks = player_stuff.get(player);
		
		for(Map.Entry<ItemStack, Integer> entry : same_stacks.entrySet())
		{
			ItemStack stack = entry.getKey();
			int amount = entry.getValue();
			if(itemM.getPersistenData(stack, pd_isArmor, PersistentDataType.INTEGER) == ATW)
			{
				stack.setAmount(1);
				
				itemM.addLore(stack, ChatColor.AQUA+ "===================", false);
				itemM.addLore(stack, ChatColor.GREEN+ "M3:"+ChatColor.DARK_PURPLE+" ALL   : "+ChatColor.GOLD+" "+0, false);
				itemM.addLore(stack, ChatColor.GREEN+ "M2:"+ChatColor.DARK_PURPLE+" STACK: "+ChatColor.GOLD+" "+0, false);
				itemM.addLore(stack, ChatColor.GREEN+ "M1:"+ChatColor.DARK_PURPLE+" ONE   : "+ChatColor.GOLD+" "+0, false);
				
				
								
				itemM.addLore(stack, ChatColor.GREEN+"==: SELL : Price :=====", false);
				itemM.addLore(stack, ChatColor.DARK_PURPLE+"Amount: "+ChatColor.YELLOW+""+amount, false);
				
				itemM.addLore(stack, ChatColor.AQUA+ "===================", false);
				
				_inv.setItem(count, stack);
				
				count++;
			}
			
			if(count >_size)
				break;			
		}
		
		ItemStack empty = new ItemStack(Material.AIR);
		for(int i = count; i < _size ; ++i)
		{
			_inv.setItem(i, empty);
		}
	}
	
	public void analysePlayerInv(Player player)
	{
		player_stuff.remove(player);
		player_refs.remove(player);
		
		HashMap<ItemStack, Integer> same_stacks=new HashMap<>();
		HashMap<Material, Integer> materials = new HashMap<>();
		HashMap<ItemStack, ArrayList<ItemStack>> stack_refs=new HashMap<>();
		System.out.println("Analyzing");
		
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
							int count = entry.getValue();
							same_stacks.put(entry_stack, count+stack.getAmount());
							
							//ArrayList<ItemStack> ref_items = stack_refs.get(stack);
							//System.out.println("ref size: "+ref_items.size());
							//ref_items.add(s);
							//System.out.println("ref size2: "+ref_items.size());
							
							for(Map.Entry<ItemStack, ArrayList<ItemStack>> entryyy : stack_refs.entrySet())
							{
								if(entryyy.getKey().isSimilar(stack))
								{
									System.out.println("similar found first");
									entryyy.getValue().add(s);
									break;
								}
							}
							
							
							
							
							//stack_refs.put(stack, ref_items);
							
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
					System.out.println("new item");
					same_stacks.put(stack, stack.getAmount());
					ArrayList<ItemStack> ref_items = new ArrayList<ItemStack>();
					ref_items.add(s);
					stack_refs.put(stack, ref_items);
					materials.put(stack.getType(), 1);
				}
				
			}			
		}
		
		player_stuff.put(player,same_stacks);
		player_refs.put(player,stack_refs);
		
	}
}

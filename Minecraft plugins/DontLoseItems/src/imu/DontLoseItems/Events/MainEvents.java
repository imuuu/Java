package imu.DontLoseItems.Events;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import imu.DontLoseItems.Other.ConfigMaker;
import imu.DontLoseItems.Other.ItemMetods;

public class MainEvents implements Listener
{
		
	HashMap<UUID, ItemStack[]> saved_items = new HashMap<UUID, ItemStack[]>();
	HashMap<UUID, Boolean> died_pvp = new HashMap<UUID,Boolean>();

	String[] tools = {"PICKAXE", "AXE", "HOE", "SHOVEL","ROD"};
	String[] weapons = {"SWORD","BOW"};
	
	boolean saveArmor = true;
	boolean saveHotBar = true;
	boolean saveTools = false;
	boolean saveWeapons = false;
	
	double durability_penalty_pve = 0.1;
	double durability_penalty_pvp = 0.2;
	
	Plugin _plugin;
	ItemMetods _itemM = null;
	public MainEvents(Plugin plugin, ItemMetods itemM)
	{
		_plugin = plugin;
		_itemM = itemM;
		getSettings();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		PlayerInventory inv = player.getInventory();

		ItemStack[] content = inv.getContents();
		ItemStack[] save_items = new ItemStack[content.length];

		if(player.getKiller() instanceof Player)
		{
			died_pvp.put(player.getUniqueId(), true);
		}else
		{
			died_pvp.put(player.getUniqueId(), false);
		}
		
		for(int l = 0 ; l < content.length; ++l)
		{
			ItemStack stack = content[l];
			if(stack != null)
			{
				ItemStack copy = new ItemStack(stack);
							
				if(saveHotBar && l > -1 && l < 9)
				{
					save_items[l] = copy;
					stack.setAmount(0);
				}
				else if(saveTools || saveWeapons)
				{
					if(saveTools & stringEndsWith(copy.getType().toString(), tools)) //Its a tool!
					{
						save_items[l]= copy;
						stack.setAmount(0);
					}
					
					if(saveWeapons & stringEndsWith(copy.getType().toString(), weapons)) // its a weapon
					{
						save_items[l]= copy;
						stack.setAmount(0);
					}
				}
				
				if(saveArmor && l > content.length-6) 
				{
					save_items[l] = copy;
					stack.setAmount(0);
				}
			}
			
		}
		saved_items.put(player.getUniqueId(), save_items);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player player = e.getPlayer();

		PlayerInventory inv = player.getInventory();
		
		ItemStack[] s_items = saved_items.get(player.getUniqueId());
		int prosent = 0;
		
		if(s_items == null)
		{
			return;
		}
			
		
		for(int i = 0; i < inv.getContents().length; ++i)
		{
			ItemStack item = s_items[i];
			if(item != null)
			{
				
				if(died_pvp.get(player.getUniqueId()))
				{
					_itemM.giveDamage(item, (int)(item.getType().getMaxDurability() * durability_penalty_pvp), false);
					prosent = (int)(durability_penalty_pvp * 100);
				}else
				{
					_itemM.giveDamage(item, (int)(item.getType().getMaxDurability() * durability_penalty_pve), false);
					prosent = (int)(durability_penalty_pve * 100);
				}
			
			inv.setItem(i, item);
			}
		}
		
		if(prosent > 0)
		{
			player.sendMessage(ChatColor.GRAY + "All your items has lost durability: " + ChatColor.RED+ prosent +"%");
		}
		saved_items.remove(player.getUniqueId());
		
	}
	
	
	void getSettings()
	{
		ConfigMaker cm = new ConfigMaker(_plugin, "settings.yml");
		FileConfiguration config=cm.getConfig();
		
		String a_path ="settings.saveArmor";
		String t_path = "settings.saveTools";
		String w_path = "settings.saveWeapons";
		String h_path = "settings.saveHotbar";
		String dpvp_path = "settings.pvp_damage_penalty";
		String dpve_path = "settings.pve_damage_penalty";
		
		if(!config.contains("settings.")) 
		{
			//default values
			_plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA +"DontLoseItems : Default config made!");		
			config.set(a_path,saveArmor);
			config.set(t_path,saveTools);
			config.set(w_path,saveWeapons);		
			config.set(h_path,saveHotBar);		
			config.set(dpvp_path,durability_penalty_pvp);		
			config.set(dpve_path,durability_penalty_pve);		
			cm.saveConfig();
			return;
		}
		
		saveArmor = config.getBoolean(a_path);
		saveTools = config.getBoolean(t_path);
		saveWeapons = config.getBoolean(w_path);
		saveHotBar = config.getBoolean(h_path);
		durability_penalty_pvp = config.getDouble(dpvp_path);
		durability_penalty_pve = config.getDouble(dpve_path);
				
	}
	boolean stringEndsWith(String target, String[] array)
	{
		for(String str : array)
		{
			if(target.endsWith(str))
				return true;
		}
		
		return false;
	}
	void printItemStacks(ItemStack[] stacks)
	{
		System.out.println("===========================");
		for (ItemStack itemStack : stacks)
		{
			if(itemStack != null)
			{
				System.out.println("Item: " + itemStack + "Material:" +itemStack.getType());
			}else
			{
				System.out.println("Item: " + itemStack);
			}
				
		}
	}
	
	ItemStack[] copyItemStackArray(ItemStack[] itemstacks)
	{
		ItemStack[] clone = new ItemStack[itemstacks.length];
		for(int i = 0 ; i < itemstacks.length; ++i)
		{
			ItemStack org = itemstacks[i];
			ItemStack copy = new ItemStack(Material.AIR);
			
			if(org != null)
				copy = new ItemStack(org);
			
			clone[i] = copy;
		}
		return clone;
	}
	
	public void dropItem(ItemStack stack, Player player)
	{
		ItemStack copy = new ItemStack(stack);
		stack.setAmount(0);
		
		player.sendMessage(ChatColor.RED + "You have dropped your: "+ ChatColor.AQUA +copy.getType().toString());
		Item dropped = player.getWorld().dropItemNaturally(player.getLocation(), copy);
		PlayerDropItemEvent event = new PlayerDropItemEvent(player, dropped);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	/**
	 * 
	 * Put item to player inventory if there is free space.. if not drop it on ground.. can include hotbar
	 * @return
	 */
	public void moveItemFirstFreeSpaceInv(ItemStack stack, Player player)
	{
		if(stack == null || stack.getType() == Material.AIR)
		{
			return;
		}
		ItemStack copy = new ItemStack(stack);
		stack.setAmount(0);
		
		PlayerInventory inv = player.getInventory();
		
		int invSlot = inv.firstEmpty();
		
		if( invSlot != -1)
		{
			player.sendMessage(ChatColor.RED + "You got your item back: "+ ChatColor.AQUA + copy.getType().toString());
			inv.setItem(invSlot, copy);
		}else
		{
			player.sendMessage(ChatColor.RED + "You don't have space!");
			dropItem(copy,player);
			
			
		}
		
	}
}

package imu.DontLoseItems.Events;

import java.util.ArrayList;
import java.util.HashMap;

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
import org.bukkit.scheduler.BukkitRunnable;

import imu.DontLoseItems.Other.ConfigMaker;

public class MainEvents implements Listener
{
		
	HashMap<Player, ItemStack[]> armorItems = new HashMap<Player, ItemStack[]>();
	HashMap<Player, ArrayList<ItemStack>> toolItems = new HashMap<Player, ArrayList<ItemStack>>();
	HashMap<Player, ArrayList<ItemStack>> weaponItems = new HashMap<Player, ArrayList<ItemStack>>();
	
	String[] tools = {"PICKAXE", "AXE", "HOE", "SHOVEL","ROD"};
	String[] weapons = {"SWORD","BOW"};
	
	boolean saveArmor = true;
	boolean saveTools = false;
	boolean saveWeapons = false;
	
	Plugin _plugin;
	
	public MainEvents(Plugin plugin)
	{
		_plugin = plugin;
		getSettings();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		PlayerInventory inv = player.getInventory();

		ItemStack[] armors = copyItemStackArray(inv.getArmorContents());
				
		ItemStack[] content = inv.getContents();
		
		if(saveTools || saveWeapons)
		{
			
			for(int i = 0; i < content.length; ++i)
			{
				ItemStack stack = content[i];
				
				if(stack != null)
				{
					ItemStack copy = new ItemStack(stack);
					
					if(saveTools & stringEndsWith(copy.getType().toString(), tools)) //Its a tool!
					{
						if(!toolItems.containsKey(player))
						{
							toolItems.put(player, new ArrayList<ItemStack>());
						}
							

						toolItems.get(player).add(copy);
						
						stack.setAmount(0);
					}
					
					if(saveWeapons & stringEndsWith(copy.getType().toString(), weapons)) // its a weapon
					{
						if(!weaponItems.containsKey(player))
						{
							weaponItems.put(player, new ArrayList<ItemStack>());
						}
							
										
						weaponItems.get(player).add(copy);
						
						stack.setAmount(0);
					}
				}
			}
		}
		
		
		
		if(player.hasPermission("dontloseitems.dontlose") && saveArmor)
		{
			armorItems.put(player,armors);
			
			for (ItemStack itemStack : inv.getArmorContents())
			{
				if(itemStack != null)
				{
					itemStack.setAmount(0);
				}		
			}
		}
		

	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player player = e.getPlayer();

		ItemStack[] armors = armorItems.get(player);
		
		if(armors != null)
		{
			player.getInventory().setArmorContents(armors);
		}
		
		if(toolItems.containsKey(player))
		{
			new BukkitRunnable() {
				
				@Override
				public void run() 
				{
					for(ItemStack t_stack : toolItems.get(player))
					{
						//inv.addItem(t_stack);
						moveItemFirstFreeSpaceInv(t_stack, player);
					}
					toolItems.remove(player);
					
				}
			}.runTask(_plugin);
			
		}
		
		if(weaponItems.containsKey(player))
		{
			System.out.println("size: "+ weaponItems.get(player).size());
			new BukkitRunnable() 
			{
				
				@Override
				public void run() 
				{
					for(ItemStack w_stack : weaponItems.get(player))
					{
						//inv.addItem(w_stack);
						moveItemFirstFreeSpaceInv(w_stack, player);
					}
					weaponItems.remove(player);
				}
			}.runTask(_plugin);
			
		}
		
		
	}
	
	
	void getSettings()
	{
		ConfigMaker cm = new ConfigMaker(_plugin, "settings.yml");
		FileConfiguration config=cm.getConfig();
		
		String a_path ="settings.saveArmor";
		String t_path = "settings.saveTools";
		String w_path = "settings.saveWeapons";
		
		if(!config.contains("settings.")) 
		{
			//default values
			_plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA +"DontLoseItems : Default config made!");		
			config.set(a_path,saveArmor);
			config.set(t_path,saveTools);
			config.set(w_path,saveWeapons);		
			cm.saveConfig();
			return;
		}
		
		saveArmor = config.getBoolean(a_path);
		saveTools = config.getBoolean(t_path);
		saveWeapons = config.getBoolean(w_path);
				
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

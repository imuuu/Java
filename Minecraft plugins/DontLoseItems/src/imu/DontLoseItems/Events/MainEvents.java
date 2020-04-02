package imu.DontLoseItems.Events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

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
		
		printItemStacks(inv.getContents());
		
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
		System.out.println("RESPAWN");
		Player player = e.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		ItemStack[] armors = armorItems.get(player);
		
		if(armors != null)
		{
			printItemStacks(armors);
			player.getInventory().setArmorContents(armors);
		}
		
		if(toolItems.containsKey(player))
		{
			for(ItemStack t_stack : toolItems.get(player))
			{
				System.out.println("tool give: " + t_stack);
				inv.addItem(t_stack);
			}
			toolItems.remove(player);
		}
		
		if(weaponItems.containsKey(player))
		{
			System.out.println("size: "+ weaponItems.get(player).size());
			for(ItemStack w_stack : weaponItems.get(player))
			{
				System.out.println("weapon give: " + w_stack);
				inv.addItem(w_stack);
			}
			weaponItems.remove(player);
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
	
	
}

package imu.DontLoseItems.Events;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import imu.DontLoseItems.CustomItems.Manager_HellTools;
import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.main.DontLoseItems;
import imu.DontLoseItems.other.Manager_HellArmor;
import imu.iAPI.LootTables.ImusLootTable;
import imu.iAPI.Other.ConfigMaker;
import imu.iAPI.Other.Metods;

public class ChestLootEvents implements Listener
{
	public static ChestLootEvents Instance;
	private Random _rand;

	private World _nether;

	
	//private final String META_OPENED_CHEST = "chestOpened";
	

	private ImusLootTable<ItemStack> _lootTable_hellArmor;
	private ImusLootTable<ItemStack> _lootTable_hellArrows;
	//private ImusLootTable<ItemStack> _lootTable_hellShields;
	private ImusLootTable<ItemStack> _lootTable_hellTools;
	private ImusLootTable<ItemStack> _lootTable_blocks;
	private ImusLootTable<ItemStack> _lootTable_valuables;
	private ImusLootTable<ItemStack> _lootTable_food;
	
	
	private ImusLootTable<Enchantment> _lootTable_enchants_armor;
	private ImusLootTable<Enchantment> _lootTable_enchants_tool;
	
	private ImusLootTable<Integer> _lootTable_stackMaxAmounts;
	
	private int _chestRollMaxAmount = 10;
	
	private boolean _chestDEBUG = false;
	private boolean _lootBoost = false;
	
	
	///setblock ~ ~ ~ minecraft:chest{LootTable:"chests/bastion_bridge"}
	public ChestLootEvents()
	{
		Instance = this;
		_rand = new Random();
		
		// GetSettings();

		_nether = Bukkit.getWorld("world_nether");

		// GetSettings();

		InitLootTable();
	}

	private void InitLootTable()
	{
		_lootTable_hellArmor = new ImusLootTable<>();
		_lootTable_hellArrows = new ImusLootTable<>();
		//_lootTable_hellShields = new ImusLootTable<>();
		_lootTable_blocks = new ImusLootTable<>();
		_lootTable_hellTools = new ImusLootTable<>();
		_lootTable_stackMaxAmounts = new ImusLootTable<>();
		_lootTable_valuables = new ImusLootTable<>();
		_lootTable_food = new ImusLootTable<>();
		_lootTable_enchants_armor = new ImusLootTable<>();
		_lootTable_enchants_tool = new ImusLootTable<>();
		
		_lootTable_stackMaxAmounts.Add(5, 90);
		_lootTable_stackMaxAmounts.Add(10, 70);
		_lootTable_stackMaxAmounts.Add(13, 50);
		_lootTable_stackMaxAmounts.Add(30, 15);
		_lootTable_stackMaxAmounts.Add(40, 7);
		_lootTable_stackMaxAmounts.Add(64, 5);
		
		
		
		int common = 90;
		int unCommon = 90;
		int rare = 80;
		int epic = 73;
		int mythic = 34;
		int lege = 20;
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellBoots(ITEM_RARITY.Common), common);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellBoots(ITEM_RARITY.Uncommon), unCommon);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellBoots(ITEM_RARITY.Rare), rare);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellBoots(ITEM_RARITY.Epic), epic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellBoots(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellBoots(ITEM_RARITY.Legendary), lege);
		
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellLeggins(ITEM_RARITY.Common), common);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellLeggins(ITEM_RARITY.Uncommon), unCommon);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellLeggins(ITEM_RARITY.Rare), rare);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellLeggins(ITEM_RARITY.Epic), epic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellLeggins(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellLeggins(ITEM_RARITY.Legendary), lege);
		
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellChestplate(ITEM_RARITY.Common), common);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellChestplate(ITEM_RARITY.Uncommon), unCommon);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellChestplate(ITEM_RARITY.Rare), rare);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellChestplate(ITEM_RARITY.Epic), epic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellChestplate(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellChestplate(ITEM_RARITY.Legendary), lege);
		
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellHelmet(ITEM_RARITY.Common), common);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellHelmet(ITEM_RARITY.Uncommon), unCommon);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellHelmet(ITEM_RARITY.Rare), rare);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellHelmet(ITEM_RARITY.Epic), epic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellHelmet(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellArmor.Add(Manager_HellArmor.Instance.CreateHellHelmet(ITEM_RARITY.Legendary), lege);
		
		
		_lootTable_hellArrows.Add(Manager_HellArmor.Instance.CreateHellTorch(), 85);
		_lootTable_hellArrows.Add(new ItemStack(Material.ARROW), 80);
		_lootTable_hellArrows.Add(Manager_HellArmor.Instance.CreateHellArrow(ITEM_RARITY.Common), 60);
		_lootTable_hellArrows.Add(Manager_HellArmor.Instance.CreateHellArrow(ITEM_RARITY.Uncommon), 60);
		_lootTable_hellArrows.Add(Manager_HellArmor.Instance.CreateHellArrow(ITEM_RARITY.Rare), 40);
		_lootTable_hellArrows.Add(Manager_HellArmor.Instance.CreateHellArrow(ITEM_RARITY.Epic), 28);
		_lootTable_hellArrows.Add(Manager_HellArmor.Instance.CreateHellArrow(ITEM_RARITY.Mythic), 18);
		_lootTable_hellArrows.Add(Manager_HellArmor.Instance.CreateHellArrow(ITEM_RARITY.Legendary), 8);
		
		
		//_lootTable_hellTools.Add(new ItemStack(Material.SHIELD), rare);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellReflectShield(ITEM_RARITY.Epic), epic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellReflectShield(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellReflectShield(ITEM_RARITY.Legendary), lege);
		
		//_lootTable_hellTools.Add(new ItemStack(Material.GOLDEN_PICKAXE), rare);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellPickaxe(ITEM_RARITY.Epic), epic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellPickaxe(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellPickaxe(ITEM_RARITY.Legendary), lege);
		
		//_lootTable_hellTools.Add(new ItemStack(Material.GOLDEN_SWORD), rare);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellTripleSword(ITEM_RARITY.Epic), epic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellTripleSword(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellTripleSword(ITEM_RARITY.Legendary), lege);
		
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellDoubleAxe(ITEM_RARITY.Epic), epic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellDoubleAxe(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellDoubleAxe(ITEM_RARITY.Legendary), lege);
		
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellHoe(ITEM_RARITY.Epic), epic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellHoe(ITEM_RARITY.Mythic), mythic);
		_lootTable_hellTools.Add(Manager_HellTools.Instance.CreateHellHoe(ITEM_RARITY.Legendary), lege);
		
		
		//_lootTable_hellTools.Add(null, lege);
		// _lootTable.Add(new ItemStack(Material.DIAMOND), 1);
		// _lootTable.Add(new ItemStack(Material.GOLD_INGOT), 10);
		// _lootTable.Add(new ItemStack(Material.IRON_INGOT), 20);
		
		
		_lootTable_blocks.Add(new ItemStack(Material.STONE), 160);
		_lootTable_blocks.Add(new ItemStack(Material.NETHER_BRICK), 160);
		_lootTable_blocks.Add(new ItemStack(Material.GRAVEL), 130);
		_lootTable_blocks.Add(new ItemStack(Material.GLOWSTONE), 80);
		_lootTable_blocks.Add(new ItemStack(Material.SHROOMLIGHT), 60);
		_lootTable_blocks.Add(new ItemStack(Material.BASALT), 160);
		_lootTable_blocks.Add(new ItemStack(Material.BLACK_STAINED_GLASS), 150);
		_lootTable_blocks.Add(new ItemStack(Material.SOUL_SAND), 150);
		_lootTable_blocks.Add(new ItemStack(Material.MAGMA_BLOCK), 120);
		_lootTable_blocks.Add(new ItemStack(Material.OBSIDIAN), 110);
		_lootTable_blocks.Add(new ItemStack(Material.CRYING_OBSIDIAN), 40);
		_lootTable_blocks.Add(new ItemStack(Material.GILDED_BLACKSTONE), 40);
		_lootTable_blocks.Add(new ItemStack(Material.CRIMSON_STEM), 130);
		_lootTable_blocks.Add(new ItemStack(Material.WARPED_STEM), 130);
		//_lootTable_blocks.Add(new ItemStack(Material.), 120);
		
		
		_lootTable_valuables.Add(new ItemStack(Material.DIAMOND), 33);
		_lootTable_valuables.Add(new ItemStack(Material.GOLD_INGOT), 80);
		_lootTable_valuables.Add(new ItemStack(Material.IRON_INGOT), 90);
		//_lootTable_valuables.Add(new ItemStack(Material.EMERALD), 10);
		_lootTable_valuables.Add(new ItemStack(Material.LAPIS_LAZULI), 100);
		_lootTable_valuables.Add(new ItemStack(Material.MAGMA_CREAM), 90);
		_lootTable_valuables.Add(new ItemStack(Material.GUNPOWDER), 70);
		_lootTable_valuables.Add(new ItemStack(Material.REDSTONE), 110);
		_lootTable_valuables.Add(new ItemStack(Material.IRON_BLOCK), 45);
		_lootTable_valuables.Add(new ItemStack(Material.GOLD_BLOCK), 45);
		_lootTable_valuables.Add(new ItemStack(Material.EMERALD_BLOCK), 45);
		_lootTable_valuables.Add(new ItemStack(Material.LAPIS_BLOCK), 40);
		_lootTable_valuables.Add(new ItemStack(Material.DIAMOND_BLOCK), 4);
		_lootTable_valuables.Add(new ItemStack(Material.ENDER_PEARL), 60);
		_lootTable_valuables.Add(new ItemStack(Material.BLAZE_ROD), 40);
		_lootTable_valuables.Add(new ItemStack(Material.GHAST_TEAR), 14);
		_lootTable_valuables.Add(new ItemStack(Material.NETHER_STAR), 2);
		_lootTable_valuables.Add(new ItemStack(Material.NETHERITE_INGOT), 1);
		_lootTable_valuables.Add(new ItemStack(Material.NETHERITE_SCRAP), 18);
		
		//_lootTable_valuables.Add(new ItemStack(Material.TNT), 120);
		
		
		_lootTable_food.Add(new ItemStack(Material.APPLE), 20);
		_lootTable_food.Add(new ItemStack(Material.COOKED_PORKCHOP), 13);
		_lootTable_food.Add(new ItemStack(Material.BAKED_POTATO), 8);
		_lootTable_food.Add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), 1);
		_lootTable_food.Add(new ItemStack(Material.GOLDEN_APPLE), 3);
		//_lootTable_food.Add(Manager_HellArmor.Instance.CreateHellReflectShield(), 100);
		
		
		
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("protection")), 8);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("fire_protection")), 9);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("blast_protection")), 11);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("projectile_protection")), 10);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("respiration")), 8);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("aqua_affinity")), 8);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("thorns")), 5);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("unbreaking")), 7);
		_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("mending")), 3);
		//_lootTable_enchants_armor.Add(Enchantment.getByKey(NamespacedKey.minecraft("binding_curse")), 1);
		
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("efficiency")), 10);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("unbreaking")), 10);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("silk_touch")), 2);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("fortune")), 3);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("power")), 4);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("punch")), 7);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("flame")), 5);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("looting")), 3);
		_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("mending")), 1);
		//_lootTable_enchants_tool.Add(Enchantment.getByKey(NamespacedKey.minecraft("vanishing_curse")), 1);

	}

	@SuppressWarnings("unused")
	private boolean IsNether(World world)
	{
		return world == _nether;
	}

	@SuppressWarnings("unused")
	private boolean IsNether(Entity entity)
	{
		return entity.getWorld() == _nether;
	}

	@SuppressWarnings("unused")
	private boolean IsNether(Block block)
	{
		return block.getWorld() == _nether;
	}

	@SuppressWarnings("unused")
	private boolean IsNether(Location loc)
	{
		if(loc == null) return false;
		
		return loc.getWorld() == _nether;
	}
	
	
	
	public LinkedList<ItemStack> GenerateNetherLoot(int rollIncrease, int rollChances)
	{
		LinkedList<ItemStack> stacks = new LinkedList<>();

		int totalRolls = 0;
		int startChance = 92;
		int reduceChance = 8;
		for(int i = 0; i < rollChances; i++)
		{
			if(_rand.nextInt(100) >= startChance) break;
			
			startChance -= reduceChance;
			totalRolls += rollIncrease;
		}	
		if(totalRolls <= 0) totalRolls = 1;
		
		int blockLootChance = 75;
		int valuableChance = 36;
		int hellArrowChance = 11; //11
		int foodChance = 2; //2
		int hellArmorChance = 4;
		int enchantedBook = 3;
		int toolGear = 5; //4
		
		if(_chestDEBUG && _lootBoost)
		{
			blockLootChance = 75;
			valuableChance = 36;
			hellArrowChance = 80; //11
			foodChance = 2; //2
			hellArmorChance = 80;
			enchantedBook = 80;
			toolGear = 80; //4
		}
		for(int i = 0; i < totalRolls; i++)
		{
			ItemStack stack;
			if(ThreadLocalRandom.current().nextInt(100) < blockLootChance)
			{
				stack = GetValidAmountStack(_lootTable_blocks.GetLoot().clone());
				stacks.add(stack);				
			}
			if(ThreadLocalRandom.current().nextInt(100) < valuableChance)
			{
				stack = GetValidAmountStack(_lootTable_valuables.GetLoot().clone());
				stacks.add(stack);				
			}
			
			if(ThreadLocalRandom.current().nextInt(100) < hellArrowChance)
			{
				stack = GetValidAmountStack(_lootTable_hellArrows.GetLoot().clone());
				stacks.add(stack);
			}
			
			if(ThreadLocalRandom.current().nextInt(100) < foodChance)
			{
				stack = GetValidAmountStack(_lootTable_food.GetLoot().clone());
				stacks.add(stack);
			}
			
			if(ThreadLocalRandom.current().nextInt(100) < hellArmorChance)
			{
				stack = GetValidAmountStack(_lootTable_hellArmor.GetLoot().clone());
				stacks.add(stack);
			}
			if(ThreadLocalRandom.current().nextInt(100) < enchantedBook)
			{
				stack = EnchantBook(2);
				stacks.add(stack);
			}
			
			if(ThreadLocalRandom.current().nextInt(100) < toolGear)
			{
				stack = GetValidAmountStack(_lootTable_hellTools.GetLoot().clone());
				stacks.add(stack);
			}
		}
		return stacks;
	}
	
	private ItemStack GetValidAmountStack(ItemStack stack)
	{
		stack.setAmount(ThreadLocalRandom.current().nextInt((_lootTable_stackMaxAmounts.GetLoot())+1));
		
		if(stack.getType() == Material.NETHER_STAR) 
		{
			stack.setAmount(1);
		}
		
		if(stack.getType() == Material.NETHERITE_INGOT) 
		{
			if(stack.getAmount() > 32) stack.setAmount(16);
			else if(stack.getAmount() > 16)stack.setAmount(4);
			else stack.setAmount(1);
		}
		
		if(stack.getType() == Material.ENDER_PEARL) 
		{
			if(stack.getAmount() > 16) stack.setAmount(16);
		}
		
		if(Metods._ins.isArmor(stack))
		{
			stack.setAmount(1);
			EnchantArmor(stack,5,2);
			
			if(Metods._ins.HasEnchant(stack, Enchantment.DURABILITY) && ThreadLocalRandom.current().nextInt(100) < 20)
			{
				stack.removeEnchantment(Enchantment.DURABILITY);
				stack.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
			}
		}
		
		if(Metods._ins.isTool(stack))
		{
			stack.setAmount(1);
			EnchantTool(stack,5,2);
			
			if(Metods._ins.HasEnchant(stack, Enchantment.DURABILITY) && ThreadLocalRandom.current().nextInt(100) < 20)
			{
				stack.removeEnchantment(Enchantment.DURABILITY);
				stack.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
			}
		}
		
		if(Manager_HellArmor.Instance.IsHellArrow(stack))
		{
			//System.out.println("it was hell arrow");
			if(stack.getAmount() > 5) stack.setAmount(5);
			
			Metods._ins.AddGlow(stack);
		}
		
		if(stack.getType() == Material.ENCHANTED_GOLDEN_APPLE)
		{
			if(stack.getAmount() > 3) stack.setAmount(3);
			else stack.setAmount(1);
			 
		}
			
		
		return stack;
	}
	
	private ItemStack EnchantBook(int miniumLevel)
	{
		ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
		
		if(ThreadLocalRandom.current().nextInt(100) > 50) EnchantTool(stack, 3, miniumLevel);
		else EnchantArmor(stack, 3, miniumLevel);
			
		
		return stack;
	}
	private void EnchantTool(ItemStack stack, int rolls,int miniumLevel)
	{
		for(int i = 0; i < rolls; i++)
		{
			if(ThreadLocalRandom.current().nextInt(100) > 50) break;
			
			Enchantment ench = _lootTable_enchants_tool.GetLoot();
			int level = ThreadLocalRandom.current().nextInt(ench.getMaxLevel())+miniumLevel;
			
			if(level == 0) level = 1;
			
			if(level > ench.getMaxLevel()) level = ench.getMaxLevel();
			
			if(stack.getType() != Material.ENCHANTED_BOOK && !ench.canEnchantItem(stack) ) continue;
			
			if(stack.getType() == Material.ENCHANTED_BOOK)
			{

				EnchantmentStorageMeta meta = (EnchantmentStorageMeta)stack.getItemMeta();
				meta.addStoredEnchant(ench, level, true);
				stack.setItemMeta(meta);
				return;
			}
			stack.addEnchantment(ench, level);
			
		}
		
		if(stack.getType() == Material.ENCHANTED_BOOK && stack.getEnchantments().size() == 0)
		{
			EnchantTool(stack, rolls, miniumLevel);
		}
	}
	
	private void EnchantArmor(ItemStack stack,  int rolls,int miniumLevel)
	{

		for(int i = 0; i < rolls; i++)
		{
			boolean done = false;
			
			if(ThreadLocalRandom.current().nextInt(100) > 55) break;
			
			Enchantment ench = _lootTable_enchants_armor.GetLoot();
			int level = ThreadLocalRandom.current().nextInt(ench.getMaxLevel())+miniumLevel;
			
			if(level == 0) level = 1;
			
			if(level > ench.getMaxLevel()) level = ench.getMaxLevel();
			
			if(stack.getType() != Material.ENCHANTED_BOOK && !ench.canEnchantItem(stack) ) continue;
			
			if(stack.getType() == Material.ENCHANTED_BOOK)
			{
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta)stack.getItemMeta();
				meta.addStoredEnchant(ench, level, true);
				stack.setItemMeta(meta);
				return;
			}
			
			int counter = 0;
			
			for(Enchantment en : stack.getEnchantments().keySet())
			{
				if(en.conflictsWith(ench))
				{
					counter++;
				}
				
				if(counter >= 2) {done = true; break;}
				
			}
			
			
			
			if(done) continue;
			
			stack.addEnchantment(ench, level);
			
			
		}
		
		if(stack.getType() == Material.ENCHANTED_BOOK && stack.getEnchantments().size() == 0)
		{
			EnchantArmor(stack, rolls, miniumLevel);
		}
		
	}
	
	
	@EventHandler
	public void OnInventoryOpen(LootGenerateEvent e)
	{
		
		
		if (!(IsNether(e.getWorld())))
			return;
		
		if(e.isCancelled()) return;
		
		
		
		Inventory inv = e.getInventoryHolder().getInventory();
		
//		if(!isDouble) System.out.println("Player: "+e.getPlayer().getName()+ " Generated loot by opened chest");
//		else System.out.println("Player: "+e.getPlayer().getName()+ " Generated loot by opened DOUBLE chest");
		
		System.out.println("Player: "+e.getEntity()+ " Generated loot by opened  chest");
		
		for(ItemStack stack : GenerateNetherLoot(2,_chestRollMaxAmount)) { inv.addItem(stack); }
		
		//if(isDouble) for(ItemStack stack : GenerateNetherLoot(2,_chestRollMaxAmount+2)) { inv.addItem(stack); }
		//GenerateNetherLoot(chestInventory);

		
	}
	@EventHandler
	public void OnInventoryOpen(InventoryOpenEvent e)
	{

		if(!_chestDEBUG) return;
		
		Block[] block = {null,null};
		Inventory inv = null;

		if (e.getInventory().getHolder() instanceof Chest)	
		{
			Chest chest = (Chest) e.getInventory().getHolder();
			block[0] = chest.getBlock();
			inv = chest.getInventory();
		}		
//		
		if(block[0] == null) return;
//		 
		inv.clear();

		for(ItemStack stack : GenerateNetherLoot(2,_chestRollMaxAmount)) { inv.addItem(stack); }

	}
	

	void GetSettings()
	{
		final String netherSettings = "NetherSettings";
		ConfigMaker cm = new ConfigMaker(DontLoseItems.Instance, netherSettings + ".yml");
		FileConfiguration config = cm.getConfig();

		// String dot_dmg = netherSettings+".durabilityDamageFromDot";

		if (!config.contains(netherSettings + "."))
		{
			// default values
			DontLoseItems.Instance.getServer().getConsoleSender()
					.sendMessage(ChatColor.AQUA + "DontLoseItems : Default config made!");

			cm.saveConfig();
			// return;
		}

	}

}

package imu.DontLoseItems.Events;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import imu.DontLoseItems.main.DontLoseItems;
import imu.iAPI.LootTables.ImusLootTable;
import imu.iAPI.Other.ConfigMaker;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;

public class ChestLootEvents implements Listener
{

	private Random _rand;

	private World _nether;

	private Cooldowns _cds;
	private final String META_OPENED_CHEST = "chestOpened";

	private ImusLootTable<ItemStack> _lootTable;

	private HashMap<UUID, Location> _lastLocations;

	private final String _PD_LAVA_BOOTS = "HELL_BOOTS";
	
	private final String _META_HELL_BOOTS_STONE = "HB_Stone";

	private final String _CD_ON_LAVA = "onLava";
	private final String _CD_IN_LAVA = "inLava";
	private final String _CD_CREATED_SAFE_PLAT = "safe_plat";
	
	private final int _hellBootsRadius = 3;
	public ChestLootEvents()
	{
		_rand = new Random();
		_lastLocations = new HashMap<>();
		_cds = new Cooldowns();
		// GetSettings();

		_nether = Bukkit.getWorld("world_nether");

		// GetSettings();

		InitLootTable();
	}

	private void InitLootTable()
	{
		_lootTable = new ImusLootTable<>();

		_lootTable.Add(CreateHellBoots(ITEM_RARITY.Common), 100);
		_lootTable.Add(CreateHellBoots(ITEM_RARITY.Uncommon), 90);
		_lootTable.Add(CreateHellBoots(ITEM_RARITY.Rare), 80);
		_lootTable.Add(CreateHellBoots(ITEM_RARITY.Epic), 70);
		_lootTable.Add(CreateHellBoots(ITEM_RARITY.Mythic), 30);
		_lootTable.Add(CreateHellBoots(ITEM_RARITY.Legendary), 10);
		// _lootTable.Add(new ItemStack(Material.DIAMOND), 1);
		// _lootTable.Add(new ItemStack(Material.GOLD_INGOT), 10);
		// _lootTable.Add(new ItemStack(Material.IRON_INGOT), 20);

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
		return loc.getWorld() == _nether;
	}
	
	public class RarityItem
	{
		public ItemStack Stack;
		public String Name;
		public ITEM_RARITY Rarity;
		
		public double[] Values;
		
		public RarityItem(ItemStack stack, String name, ITEM_RARITY rarity, double[] values)
		{
			this.Stack = stack;
			this.Name = name;
			this.Rarity = rarity;
			this.Values = values;
		}
		
		public ItemStack GetItemStack()
		{
			ItemStack stack = Stack.clone();
			Metods.setDisplayName(stack, GetColor(Rarity)+" "+Name);
			
			return stack;
		}
		
		private String GetColor(ITEM_RARITY rarity) 
		{
		    switch (rarity) {
		        case Common:
		            return ChatColor.GRAY + rarity.toString();
		        case Uncommon:
		            return ChatColor.GREEN + rarity.toString();
		        case Rare:
		            return ChatColor.YELLOW + rarity.toString();
		        case Epic:
		            return ChatColor.LIGHT_PURPLE + rarity.toString();
		        case Mythic:
		            return ChatColor.AQUA + rarity.toString();
		        case Legendary:
		            return ChatColor.GOLD + rarity.toString();
		        default:
		            return ChatColor.WHITE + rarity.toString();
		    }
		}
		
		
	}
	
	public enum ITEM_RARITY
	{
	    Common,
	    Uncommon,
	    Rare,
	    Epic,
	    Mythic,
	    Legendary
	}
	
	private RarityItem[] _hellBoots = 
		{
			new RarityItem(new ItemStack(Material.LEATHER_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Common, 		new double[] {0.0, -14,  1, 0}),	
			new RarityItem(new ItemStack(Material.GOLDEN_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Uncommon, 	new double[] {0.00, -12, 1, 0}),	
			new RarityItem(new ItemStack(Material.CHAINMAIL_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Rare, 		new double[] {0.01, -10, 1, 1}),	
			new RarityItem(new ItemStack(Material.IRON_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Epic, 			new double[] {0.01, -8,  1, 1}),	
			new RarityItem(new ItemStack(Material.DIAMOND_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Mythic, 		new double[] {0.03, -6,  1, 1}),	
			new RarityItem(new ItemStack(Material.NETHERITE_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Legendary, new double[] {0.08, -4,  3, 4}),	
		};
	private ItemStack CreateHellBoots(ITEM_RARITY rarity)
	{
		
		RarityItem rarityItem = null;
		
		for(RarityItem boots : _hellBoots)
		{
			if(boots.Rarity == rarity) {rarityItem = boots;}
		}
		
		ItemStack stack = rarityItem.GetItemStack();
		
		String[] lores = 
			{ 
				" ",
				"&9Able to walk on &cLava",
				" ",
				"&7'Some say these boots were created by",
				"&7a mad wizard in an attempt to control the power",
				"&7of the volcanoes. Others say he just wanted",
				"&7a comfy pair of slippers'",
				
				};

		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
	
		if(rarityItem.Values[0] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", rarityItem.Values[0],AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
		if(rarityItem.Values[1] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.health", rarityItem.Values[1], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
		if(rarityItem.Values[2] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", rarityItem.Values[2], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
		if(rarityItem.Values[3] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", rarityItem.Values[3], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));

		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_LAVA_BOOTS, PersistentDataType.INTEGER, 1);

		return stack;
	}
	
	
	
	private boolean IsHellBoots(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_LAVA_BOOTS, PersistentDataType.INTEGER) != null;
	}

	@EventHandler
	public void OnInventoryOpen(InventoryOpenEvent e)
	{
		if (!(IsNether(e.getInventory().getLocation())))
			return;

		if (!(e.getInventory().getHolder() instanceof Chest))
			return;

		Chest chest = (Chest) e.getInventory().getHolder();
		Inventory chestInventory = chest.getInventory();

		// if (chest.hasMetadata(META_OPENED_CHEST)) return;

		chest.getInventory().clear();
		_lootTable.AddLootAsItemStack(chestInventory, 27);

		chest.setMetadata(META_OPENED_CHEST, new FixedMetadataValue(DontLoseItems.Instance, true));
	}

	@EventHandler
	public void OnBlockBreak(BlockBreakEvent e)
	{
		if (!(IsNether(e.getBlock())))
			return;

		// if (!(e.getInventory().getHolder() instanceof Chest)) return;

		Block block = e.getBlock();

		if (block == null || block.getType() != Material.CHEST)
			return;

		if (!block.hasMetadata(META_OPENED_CHEST))
			return;

		block.removeMetadata(META_OPENED_CHEST, DontLoseItems.Instance);
		System.out.println("Metadata removed");

	}

	public void onPlayerQuit(PlayerQuitEvent e)
	{
		RemoveLastLocation(e.getPlayer());

		_cds.removeCooldown(e.getPlayer().getUniqueId().toString() + _CD_IN_LAVA);
		_cds.removeCooldown(e.getPlayer().getUniqueId().toString() + _CD_ON_LAVA);
	}

	private void RemoveLastLocation(Player player)
	{
		if (_lastLocations.containsKey(player.getUniqueId()))
			_lastLocations.remove(player.getUniqueId());
	}

//	@EventHandler
//	public void onPlayerMove(BlockPlaceEvent event)
//	{
//		event.setCancelled(true);
//	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{

		// if(!_lastLocations.containsKey(event.getPlayer().getUniqueId())) return;

		Player player = event.getPlayer();

		Location lastLocation = _lastLocations.get(player.getUniqueId());
		Location currentLocation = player.getLocation();


		if (!(lastLocation == null || lastLocation.getWorld() != currentLocation.getWorld()
				|| currentLocation.distance(lastLocation) >= 1))
		{
			return;
		}
			

		if (!IsHellBoots(player.getInventory().getBoots()))
		{
			RemoveLastLocation(player);
			return;
		}

		Block block = currentLocation.getBlock().getRelative(BlockFace.DOWN);
		
		if(block.getType() != Material.LAVA && block.getType() != Material.STONE && block.getType() != Material.AIR)
		{
			//System.out.println("noni");
			_cds.removeCooldown(player.getUniqueId().toString() + _CD_ON_LAVA);
		}
		
		if (currentLocation.getBlock().getType() == Material.LAVA)
		{
			return;
		}
		
		//CHECKING FALLING 
		if(player.getVelocity().getY() < -0.5 && _cds.isCooldownReady(player.getUniqueId().toString()+_CD_CREATED_SAFE_PLAT))
		{

			for(int i = 0; i < 6; i++)
			{
				Block b = currentLocation.clone().add(0,-1*i,0).getBlock();
				
				if(b.getType() != Material.LAVA) continue;
				
				if(!CreateStonePlatform(player, b,_hellBootsRadius)) continue;
				
				_cds.setCooldownInSeconds(player.getUniqueId().toString()+_CD_CREATED_SAFE_PLAT, 2);
				return;
			}
		}
		
		if(CreateStonePlatform(player, block,_hellBootsRadius))
		{
			_lastLocations.put(player.getUniqueId(), currentLocation);
		}
	}
	
	private Block SetBlockType(Player player,Block block, Material mat)
	{

		
	    BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(mat), player, true, EquipmentSlot.HAND);
	    //new BlockPl
	    Bukkit.getServer().getPluginManager().callEvent(event);
	    
	    if(event.isCancelled()) return block;
	    
	    block.setType(mat);
	    
		return block;
	}
	
	
	
//	private boolean CreateStonePlatform_radius3(Player player, Block block)
//	{
//		if (block.getType() == Material.LAVA || !_cds.isCooldownReady(player.getUniqueId().toString() + _CD_ON_LAVA))
//		{
//			System.out.println("set lava");
//			
//			Block center = block.getRelative(player.getFacing(), 1);
//			_cds.setCooldownInSeconds(player.getUniqueId().toString() + _CD_ON_LAVA, 3);
//			int x = center.getX();
//			int y = center.getY();
//			int z = center.getZ();
//			int radius = 3;
//
//			Block[] blocks = new Block[] {
//				    center.getWorld().getBlockAt(x, y, z),
//				    center.getWorld().getBlockAt(x + radius, y, z),
//				    center.getWorld().getBlockAt(x + (int)(radius * Math.cos(Math.toRadians(45))), y, z + (int)(radius * Math.sin(Math.toRadians(45)))),
//				    center.getWorld().getBlockAt(x, y, z + radius),
//				    center.getWorld().getBlockAt(x - (int)(radius * Math.cos(Math.toRadians(45))), y, z + (int)(radius * Math.sin(Math.toRadians(45)))),
//				    center.getWorld().getBlockAt(x - radius, y, z),
//				    center.getWorld().getBlockAt(x - (int)(radius * Math.cos(Math.toRadians(45))), y, z - (int)(radius * Math.sin(Math.toRadians(45)))),
//				    center.getWorld().getBlockAt(x, y, z - radius),
//				    center.getWorld().getBlockAt(x + (int)(radius * Math.cos(Math.toRadians(45))), y, z - (int)(radius * Math.sin(Math.toRadians(45))))
//				};
//
//			for (Block b : blocks) 
//			{
//			    if (b.getType() != Material.LAVA) 
//			    {
//			        continue;
//			    }
//			    SetBlockType(b, Material.STONE);
//			}
//			return true;
//		}
//		
//		return false;
//	}
	
	private boolean CreateStonePlatform(Player player,Block block, int radius)
	{
		if (block.getType() == Material.LAVA || !_cds.isCooldownReady(player.getUniqueId().toString() + _CD_ON_LAVA))
		{
			//System.out.println("set lava");
			
			Block center = block.getRelative(player.getFacing(), 1);

			_cds.setCooldownInSeconds(player.getUniqueId().toString() + _CD_ON_LAVA, 3);
			int x = center.getX();
			int y = center.getY();
			int z = center.getZ();
			int r = radius;
			
			for (int i = x - r; i <= x + r; i++)
			{
				for (int k = z - r; k <= z + r; k++)
				{
					Block b = center.getWorld().getBlockAt(i, y, k);
					
					if (b.getType() != Material.LAVA) continue;
	
					if(!IsInCircle(i, y, k, x, y, z, r)) continue;
					
					SetBlockType(player, b,Material.STONE);
				}

			}
			return true;
		}
		
		return false;
	}
	private boolean IsInCircle(int x, int y, int z, int cx, int cy, int cz, int r)
	{
		return (x - cx) * (x - cx) + (y - cy) * (y - cy) + (z - cz) * (z - cz) <= r * r;
	}

//	@EventHandler
//    public void onPlayerMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
//
//        if (block.getType() == Material.LAVA) {
//            int x = block.getX();
//            int y = block.getY();
//            int z = block.getZ();
//            int r = 2;
//
//            for (int i = x - r; i <= x + r; i++) {
//                for (int j = y - r; j <= y + r; j++) {
//                    for (int k = z - r; k <= z + r; k++) {
//                        Block b = block.getWorld().getBlockAt(i, j, k);
//                        if (b.getType() == Material.LAVA && isInCircle(i, j, k, x, y, z, r)) {
//                            b.setType(Material.STONE);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean isInCircle(int x, int y, int z, int cx, int cy, int cz, int r) {
//        return (x - cx) * (x - cx) + (y - cy) * (y - cy) + (z - cz) * (z - cz) <= r * r;
//    }

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

package imu.DontLoseItems.CustomItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.other.RarityItem;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;

public final class Hell_Pickaxe_Controller
{

	private final String _PD_HELL_PICKAXE = "HELL_PICKAXE";
	private HashMap<UUID, MINE_DIRECTION> _playerMineDir;
	private Cooldowns _cds;
	private RarityItem[] _hellPickaxes = {
			new RarityItem(new ItemStack(Material.NETHERITE_PICKAXE), ChatColor.DARK_RED + "Hell Pickaxe", ITEM_RARITY.Common, new double[] { 0 }), // not used
			new RarityItem(new ItemStack(Material.NETHERITE_PICKAXE), ChatColor.DARK_RED + "Hell Pickaxe", ITEM_RARITY.Uncommon, new double[] { 0 }), // not used
			new RarityItem(new ItemStack(Material.NETHERITE_PICKAXE), ChatColor.DARK_RED + "Hell Pickaxe", ITEM_RARITY.Rare, new double[] { 0 }), // not used
			new RarityItem(new ItemStack(Material.IRON_PICKAXE), ChatColor.DARK_RED + "Hell Pickaxe", ITEM_RARITY.Epic, new double[] { 0 }),
			new RarityItem(new ItemStack(Material.DIAMOND_PICKAXE), ChatColor.DARK_RED + "Hell Pickaxe", ITEM_RARITY.Mythic, new double[] { 0 }),
			new RarityItem(new ItemStack(Material.NETHERITE_PICKAXE), ChatColor.DARK_RED + "Hell Pickaxe",ITEM_RARITY.Legendary, new double[] { 0 }), };
	
	private int _durabilityLost = 2;
	private int _durabilityHealByLava = 4;
	public Hell_Pickaxe_Controller()
	{
		_playerMineDir = new HashMap<>();
		_cds = new Cooldowns();
	}

	private enum MINE_DIRECTION
	{
		NONE, HORIZONTA, DOWN, UP,
	}

	public ItemStack CreateHellPickaxe(ITEM_RARITY rarity)
	{
		RarityItem rarityItem = null;

		for (RarityItem arrow : _hellPickaxes)
		{
			if (arrow.Rarity == rarity)
			{
				rarityItem = arrow;
			}
		}

		ItemStack stack = rarityItem.GetItemStack();

		ArrayList<String> lores = new ArrayList<>();
		lores.add(" ");
		lores.add("&cLava &9is turned cobblestone ");
		lores.add("&9around mined block");
		lores.add(" ");
		lores.add("&9Changed &clava &9restores durability");
		lores.add(" ");

		// if(rarity == ITEM_RARITY.Epic) lores.add("&9Mines one random direction");
		if (rarity == ITEM_RARITY.Mythic)
		{
			lores.add("&9Mines &3two &9blocks vertically");
			lores.add(" ");
		}
			
		if (rarity == ITEM_RARITY.Legendary)
		{
			lores.add("&9Mines &3three &9blocks vertically");
			lores.add(" ");
		}
			

		// if(rarity == ITEM_RARITY.Legendary) lores.add(" ");
		// if(rarity == ITEM_RARITY.Legendary) lores.add("&9Able to mine through lava");
		
		lores.add("&9Silk Touch doesn't work on this");
		lores.add(" ");
		lores.add("&7'One ancient tome claims that");
		lores.add("&7the Hell Picaxe was crafted by the");
		lores.add("&7devil himself, as a tool to reshape");
		lores.add("&7the underworld to his will'");

		Metods._ins.SetLores(stack, lores.toArray(new String[lores.size()]), false);

		ItemMeta meta = stack.getItemMeta();

		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_PICKAXE, PersistentDataType.INTEGER, 1);

		return stack;
	}

	public boolean IsHellPickaxe(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_PICKAXE, PersistentDataType.INTEGER) != null;
	}
	
	public ItemStack RemoveHellPickaxe(ItemStack stack)
	{
		return Metods._ins.removePersistenData(stack, _PD_HELL_PICKAXE);
	}

	private boolean BreakTheBlock(Player player, Block b, int fortune)
	{
		if (b == null || b.getType().isAir())
			return false;

		if (b.getType().getBlastResistance() > 1500 || !b.getType().isBlock() || b.isPassable())
			return false;

		BlockBreakEvent event = new BlockBreakEvent(b, player);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return false;

		// System.out.println("event: "+event.getBlock().getDrops());
		if (b.getType().isInteractable())
		{
			System.out.println("is interactable");
			b.breakNaturally();
			return true;
		}

		if (fortune > 0)
		{
			for (ItemStack stack : b.getDrops())
			{
				ItemStack newStack = Metods._ins.FortuneSimulation(stack, fortune);
				b.getWorld().dropItemNaturally(b.getLocation(), newStack);
			}

			b.setType(Material.AIR);

			b.getDrops().clear();
			return true;
		}

		System.out.println("break naturally");
		b.breakNaturally();
		return true;

	}

	private void PlaceLavaBlocks(Player player, ItemStack pick,Block b)
	{
		ReplaceLava(player, pick, b.getRelative(BlockFace.UP));
		ReplaceLava(player, pick, b.getRelative(BlockFace.DOWN));
		ReplaceLava(player, pick, b.getRelative(BlockFace.EAST));
		ReplaceLava(player, pick, b.getRelative(BlockFace.WEST));
		ReplaceLava(player, pick, b.getRelative(BlockFace.NORTH));
		ReplaceLava(player, pick, b.getRelative(BlockFace.SOUTH));
	}

	private Block ReplaceLava(Player player, ItemStack pick, Block b)
	{
		if (b == null || b.getType() != Material.LAVA)
			return b;

		Block now = b;
		b.setType(Material.COBBLESTONE);

		BlockPlaceEvent event = new BlockPlaceEvent(b, now.getState(), b, new ItemStack(Material.LAVA), player, false,
				EquipmentSlot.HAND);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
		{
			b.setType(Material.LAVA);
			return b;
		}

		b.setType(Material.COBBLESTONE);
		Metods._ins.giveDamage(pick, -_durabilityHealByLava, false);
		
		if (!_cds.isCooldownReady("LavaSound"))
			return b;

		_cds.setCooldownInSeconds("LavaSound", 0.1);
		player.playSound(player, Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
		
		return b;

	}

	public void OnBlockInteract(PlayerInteractEvent e)
	{
		if (e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		BlockFace face = e.getBlockFace();

		switch (face)
		{
		case DOWN:
		{
			_playerMineDir.put(e.getPlayer().getUniqueId(), MINE_DIRECTION.UP);
			break;
		}
		case UP:
		{
			_playerMineDir.put(e.getPlayer().getUniqueId(), MINE_DIRECTION.DOWN);
			break;
		}
		case NORTH:
		{
			_playerMineDir.put(e.getPlayer().getUniqueId(), MINE_DIRECTION.HORIZONTA);
			break;
		}
		case SOUTH:
		{
			_playerMineDir.put(e.getPlayer().getUniqueId(), MINE_DIRECTION.HORIZONTA);
			break;
		}
		case WEST:
		{
			_playerMineDir.put(e.getPlayer().getUniqueId(), MINE_DIRECTION.HORIZONTA);
			break;
		}
		case EAST:
		{
			_playerMineDir.put(e.getPlayer().getUniqueId(), MINE_DIRECTION.HORIZONTA);
			break;
		}

		default:
			_playerMineDir.put(e.getPlayer().getUniqueId(), MINE_DIRECTION.HORIZONTA);
			break;

		}

	}

	@SuppressWarnings("incomplete-switch")
	public void OnHellPickBlockBreak(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		ItemStack stack = player.getInventory().getItemInMainHand();
		if (!IsHellPickaxe(stack))
			return;

		if (_playerMineDir.containsKey(player.getUniqueId())
				&& _playerMineDir.get(player.getUniqueId()) == MINE_DIRECTION.NONE)
		{
			//System.out.println("it was none");
			return;
		}

		Block minedBlock = e.getBlock();
		MINE_DIRECTION dir = _playerMineDir.get(player.getUniqueId());
		_playerMineDir.put(player.getUniqueId(), MINE_DIRECTION.NONE);

		if (stack.containsEnchantment(Enchantment.SILK_TOUCH))
			stack.removeEnchantment(Enchantment.SILK_TOUCH);

		int fortuneLevel = Metods._ins.GetEnchantLevel(stack, Enchantment.LOOT_BONUS_BLOCKS);

		PlaceLavaBlocks(player, stack, minedBlock);
		//System.out.println("dir: " + dir);
		if (dir != MINE_DIRECTION.HORIZONTA)
		{
			// SetMinedBlocks(player, minedBlock, null, null);

			_playerMineDir.put(player.getUniqueId(), dir);
			return;
		}
		Metods._ins.giveDamage(stack, _durabilityLost, false);
		switch (RarityItem.GetRarity(stack))
		{
		case Legendary:
		{
			Block upper = minedBlock.getRelative(BlockFace.UP);
			Block lower = minedBlock.getRelative(BlockFace.DOWN);
			if (BreakTheBlock(player, upper, fortuneLevel))
			{
				PlaceLavaBlocks(player, stack, upper);
			}

			if (lower.getLocation().getBlockY() >= player.getLocation().getBlockY())
			{
				if (BreakTheBlock(player, lower, fortuneLevel))
				{
					PlaceLavaBlocks(player, stack, lower);
				}
			}

			_playerMineDir.put(player.getUniqueId(), dir);
			break;
		}

		case Mythic:
		{

			Block lower = minedBlock.getRelative(BlockFace.DOWN);

			if (lower.getLocation().getBlockY() < player.getLocation().getBlockY())
				break;

			if (BreakTheBlock(player, lower, fortuneLevel))
			{
				PlaceLavaBlocks(player, stack, lower);
			}
			_playerMineDir.put(player.getUniqueId(), dir);
			break;
		}

		case Epic:
		{
			break;
		}
		}
	}
}

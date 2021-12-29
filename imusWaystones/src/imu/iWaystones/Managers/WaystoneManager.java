package imu.iWaystones.Managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Other.Metods;
import imu.iWaystone.Upgrades.BaseUpgrade;
import imu.iWaystone.Upgrades.UpgradeCastTime;
import imu.iWaystone.Upgrades.UpgradeCooldown;
import imu.iWaystone.Upgrades.UpgradeDimension;
import imu.iWaystone.Upgrades.UpgradeXPusage;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Enums.UpgradeType;
import imu.iWaystones.Invs.WaystoneMenuInv;
import imu.iWaystones.Main.ImusWaystones;

public class WaystoneManager 
{
	ImusWaystones _main = ImusWaystones._instance;
	private Set<Material> _valid_mats = new HashSet<>();
	
	private Set<Material> _valid_top_mats = new HashSet<>();
	private Set<Material> _valid_mid_mats = new HashSet<>();
	private Set<Material> _valid_low_mats = new HashSet<>();
	
	private HashMap<UUID, Waystone> _waitingPlayerConfirm = new HashMap<>();
	
	private HashMap<UUID, Waystone> _waystones = new HashMap<>();
	private HashSet<UUID> _IsTeleporting = new HashSet<>();
	private HashMap<UUID, HashSet<UUID>> _discoveredWaystones = new HashMap<>();
	//private HashMap<UUID, PlayerUpgradePanel> _playerUpgradePanel = new HashMap<>();
	private HashMap<Location, UUID> _location_of_waystones = new HashMap<>();
	
	public final String pd_waystoneUUID = "iw.waystoneUUID";
	public final String pd_waystoneHolo = "iw.holo";
	private WaystoneManagerSQL _waystoneManagersSQL;
	
	public WaystoneManager()
	{
		_waystoneManagersSQL = new WaystoneManagerSQL(this);
		
	}
	
	public void OnDisable() 
	{
		ClearHolograms();
	}
	
	public void Init()
	{
		ClearHolograms();
		
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				SetupValidBlocks();
				_waystoneManagersSQL.CreateTables();
				_waystoneManagersSQL.LoadWaystones();
				_waystoneManagersSQL.LoadDiscoveredWaystones();
				
			}
		}.runTaskAsynchronously(_main);
	}
	public void ClearHolograms()
	{
		for(World world : Bukkit.getWorlds())
		{
			for(Entity entity : world.getEntities())
			{
				if(Metods._ins.getPersistenData(entity, pd_waystoneHolo, PersistentDataType.STRING) != null) entity.remove();
			}
		}
	}
	public WaystoneManagerSQL GetWaystoneManagerSQL()
	{
		return _waystoneManagersSQL;
	}
	public Waystone GetWaystone(UUID uuid_ws)
	{
		return _waystones.get(uuid_ws);
	}
	
	public HashMap<UUID, Waystone> GetWaystones()
	{
		return _waystones;
	}
	
	public boolean IsTeleporting(Player player)
	{
		return _IsTeleporting.contains(player.getUniqueId());
	}
	
	public void SetTeleporting(Player player)
	{
		_IsTeleporting.add(player.getUniqueId());
	}
	
	public void RemoveTeleportin(UUID uuid_player)
	{
		_IsTeleporting.remove(uuid_player);
	}
	

	public BaseUpgrade GetNewUpgrade(UpgradeType type) 
	{
		switch(type)
		{
		case CAST_TIME:
			return new UpgradeCastTime();
		case COOLDOWN:
			return new UpgradeCooldown();
		case DIMENSION:
			return new UpgradeDimension();
		case XP_USAGE:
			return new UpgradeXPusage();

		}
		return null;
	}
	void SetupValidBlocks()
	{
		for(Material mat : Material.values())
		{
			if(Tag.WALLS.getValues().contains(mat) )
			{
				AddValidMid(mat);
			}
			if(Tag.SLABS.getValues().contains(mat))
			{
				AddValidTop(mat);
			}

		}
		
		AddValidLow(Material.IRON_BLOCK);
		AddValidLow(Material.GOLD_BLOCK);
		AddValidLow(Material.DIAMOND_BLOCK);
		AddValidLow(Material.NETHERITE_BLOCK);
	}
	void AddValidTop(Material mat)
	{
		_valid_mats.add(mat);
		_valid_top_mats.add(mat);
	}
	
	void AddValidMid(Material mat)
	{
		_valid_mats.add(mat);		
		_valid_mid_mats.add(mat);
	}
	
	void AddValidLow(Material mat)
	{
		_valid_mats.add(mat);
		_valid_low_mats.add(mat);
	}
	
	boolean IsValidMaterial(Block block)
	{
		return _valid_mats.contains(block.getType());
	}
	Waystone CreateWayStone(Block top,Block mid,Block low)
	{
		if(_valid_top_mats.contains(top.getType()) && _valid_mid_mats.contains(mid.getType()) && _valid_low_mats.contains(low.getType())) return new Waystone(new Location(low.getWorld(), low.getX(), low.getY(), low.getZ()));
		return null;
	}
	public Waystone TryToCreateWaystone(Block block)
	{
		if(!IsValidMaterial(block)) return null;
		if(_valid_top_mats.contains(block.getType())) return CreateWayStone(block, block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0)), block.getWorld().getBlockAt(block.getLocation().add(0, -2, 0)));
		if(_valid_mid_mats.contains(block.getType())) return CreateWayStone(block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)), block, block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0)));
		if(_valid_low_mats.contains(block.getType())) return CreateWayStone( block.getWorld().getBlockAt(block.getLocation().add(0, 2, 0)), block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)), block);
		
		
		return null;
		
	}
	
	public HashMap<UUID, HashSet<UUID>> GetDiscovered()
	{
		return _discoveredWaystones;
	}
	
	public boolean HasDiscovered(Player player, Waystone waystone)
	{
		if(!_discoveredWaystones.containsKey(player.getUniqueId())) return false;
		
		if(_discoveredWaystones.get(player.getUniqueId()).contains(waystone.GetUUID())) return true;
		return false;
	}
	
	public void AddDiscovered(UUID player_uuid, UUID uuid_ws, boolean saveDatabase)
	{
		if(player_uuid == null || uuid_ws == null) return;
		
		if(!_discoveredWaystones.containsKey(player_uuid)) _discoveredWaystones.put(player_uuid, new HashSet<>());
		_discoveredWaystones.get(player_uuid).add(uuid_ws);
		if(saveDatabase) _waystoneManagersSQL.SaveDiscovered(player_uuid, uuid_ws);
	}
	
	public void SetPlayerConfirmation(UUID uuid, Waystone waystone)
	{
		if(waystone == null) _waitingPlayerConfirm.remove(uuid);
		_waitingPlayerConfirm.put(uuid, waystone);
	}
	
	public void ConfirmWaystoneCreation(UUID uuid)
	{
		Waystone wStone = _waitingPlayerConfirm.get(uuid);
		_waitingPlayerConfirm.remove(uuid);
		
		SaveWaystone(wStone, true);
	}
	
	public void RemoveWaystone(UUID uuid)
	{
		Waystone waystone = _waystones.get(uuid);
		if(waystone == null) return;
		//Metods._ins.printHashMap(_location_of_waystones);
		_location_of_waystones.remove(waystone.GetLowBlock().getLocation());
		_location_of_waystones.remove(waystone.GetMidBlock().getLocation());
		_location_of_waystones.remove(waystone.GetTopBlock().getLocation());
		waystone.GetHologram().remove();
		
		_waystones.remove(uuid);		
		_waystoneManagersSQL.RemoveWaystoneAsync(waystone);
		_waystoneManagersSQL.RemoveDiscoveredAsync(waystone.GetUUID());
		for(HashSet<UUID> set : _discoveredWaystones.values()) {set.remove(waystone.GetUUID());}
	}
	
	public void RemoveWaystone(Waystone waystone)
	{
		RemoveWaystone(waystone.GetUUID());
	}
	void RegisterWaystoneLocation(Waystone waystone)
	{
		_location_of_waystones.put(waystone.GetLowBlock().getLocation(), waystone.GetUUID());
		_location_of_waystones.put(waystone.GetMidBlock().getLocation(), waystone.GetUUID());
		_location_of_waystones.put(waystone.GetTopBlock().getLocation(), waystone.GetUUID());
	}
	public void SaveWaystone(Waystone waystone, boolean saveDatabase)
	{
		RegisterWaystoneLocation(waystone);
		_waystones.put(waystone.GetUUID(), waystone);
		AddDiscovered(waystone.GetOwnerUUID(), waystone.GetUUID(), saveDatabase);
		waystone.CreateHologram();
		if(saveDatabase)
		{
			_waystoneManagersSQL.SaveWaystoneAsync(waystone);
			
		}

	}

	public boolean IsWaystone(Block block)
	{
		if(block == null) return false;
		
		return _location_of_waystones.containsKey(block.getLocation());
	}
	
	public boolean IsValid(Waystone waystone)
	{
		if(waystone == null) return false;
		
		if(_valid_top_mats.contains(waystone.GetTopBlock().getType()) && _valid_mid_mats.contains(waystone.GetMidBlock().getType()) && _valid_low_mats.contains(waystone.GetLowBlock().getType())) return true;
		
		return false;
	}
	
	public Waystone GetWaystone(Block block)
	{
		return _waystones.get(_location_of_waystones.get(block.getLocation()));
	}

	
	public void OpenWaystone(Player player, Waystone waystone)
	{
		new WaystoneMenuInv(waystone, player).openThis();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

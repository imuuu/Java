package imu.iWaystones.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Main.ImusAPI;
import imu.iWaystone.Waystones.Waystone;
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
	private HashMap<UUID, HashSet<UUID>> _discoveredWaystones = new HashMap<>();
	private HashMap<Location, UUID> _location_of_waystones = new HashMap<>();
	
	public final String pd_waystoneUUID = "iw.waystoneUUID";
	private WaystoneManagerSQL _waystoneManagersSQL;
	
	public WaystoneManager()
	{
		_waystoneManagersSQL = new WaystoneManagerSQL();
		
	}
	
	public void Init()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				SetupValidBlocks();
				_waystoneManagersSQL.LoadTables();
				
			}
		}.runTaskAsynchronously(_main);
	}
	
	public WaystoneManagerSQL GetWaystoneManagerSQL()
	{
		return _waystoneManagersSQL;
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
		if(_valid_top_mats.contains(top.getType()) && _valid_mid_mats.contains(mid.getType()) && _valid_low_mats.contains(low.getType())) return new Waystone(top, mid, low);
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
	
	public boolean HasDiscovered(Player player, Waystone waystone)
	{
		if(!_discoveredWaystones.containsKey(player.getUniqueId())) return false;
		
		if(_discoveredWaystones.get(player.getUniqueId()).contains(waystone.GetUUID())) return true;
		return false;
	}
	
	public void AddDiscovered(UUID player_uuid, Waystone waystone)
	{
		if(!_discoveredWaystones.containsKey(player_uuid)) _discoveredWaystones.put(player_uuid, new HashSet<>());
		_discoveredWaystones.get(player_uuid).add(waystone.GetUUID());
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
		
		SaveWaystone(wStone);
	}
	
	public void RemoveWaystone(UUID uuid)
	{
		Waystone waystone = _waystones.get(uuid);
		if(waystone == null) return;
		_location_of_waystones.remove(waystone.GetLowBlock().getLocation());
		_location_of_waystones.remove(waystone.GetMidBlock().getLocation());
		_location_of_waystones.remove(waystone.GetTopBlock().getLocation());
		_waystones.remove(uuid);
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
	public void SaveWaystone(Waystone waystone)
	{
		RegisterWaystoneLocation(waystone);
		_waystones.put(waystone.GetUUID(), waystone);
		AddDiscovered(waystone.GetOwnerUUID(), waystone);
	}
	
//	public void SetWaystoneUUIDtoBlock(Block block, UUID uuid)
//	{
//		ImusAPI._metods.setPersistenData(block, ImusWaystones._instance.GetWaystoneManager().pd_waystoneUUID, PersistentDataType.STRING, uuid.toString());
//	}
	
	public boolean IsWaystone(Block block)
	{
		if(block == null) return false;
		
		return _location_of_waystones.containsKey(block.getLocation());
	}
	
	public Waystone GetWaystone(Block block)
	{
		return _waystones.get(_location_of_waystones.get(block.getLocation()));
	}
	
//	public UUID GetWaystoneUUIDFromBlock(Block block)
//	{
//		String uuid_str = ImusAPI._metods.getPersistenData(block, pd_waystoneUUID, PersistentDataType.STRING);
//		if(uuid_str == null) return null;
//		return UUID.fromString(uuid_str);
//	}
	
	public void OpenWaystone(Player player, Waystone waystone)
	{
		new WaystoneMenuInv(waystone, player).openThis();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

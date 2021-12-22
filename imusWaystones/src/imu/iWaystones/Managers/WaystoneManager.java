package imu.iWaystones.Managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Main.ImusWaystones;

public class WaystoneManager 
{
	private Set<Material> _valid_mats = new HashSet<>();
	
	private Set<Material> _valid_top_mats = new HashSet<>();
	private Set<Material> _valid_mid_mats = new HashSet<>();
	private Set<Material> _valid_low_mats = new HashSet<>();
	
	private HashMap<UUID, Waystone> _waitingPlayerConfirm = new HashMap<>();
	
	private HashMap<UUID, Waystone> _waystones = new HashMap<>();
	public WaystoneManager()
	{
		SetupValidBlocks();
	}
	
	void SetupValidBlocks()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
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
		}.runTaskAsynchronously(ImusWaystones._instance);

		
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
	public Waystone GetWaystone(Block block)
	{
		if(!IsValidMaterial(block)) return null;
		if(_valid_top_mats.contains(block.getType())) return CreateWayStone(block, block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0)), block.getWorld().getBlockAt(block.getLocation().add(0, -2, 0)));
		if(_valid_mid_mats.contains(block.getType())) return CreateWayStone(block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)), block, block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0)));
		if(_valid_low_mats.contains(block.getType())) return CreateWayStone( block.getWorld().getBlockAt(block.getLocation().add(0, 2, 0)), block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)), block);
		
		
		return null;
		
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
	
	public void SaveWaystone(Waystone waystone)
	{
		_waystones.put(waystone.GetUUID(), waystone);
		System.out.println("Waystone saved");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

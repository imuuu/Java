package imu.iWaystone.Waystones;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Other.Metods;
import imu.iWaystone.Upgrades.BaseUpgrade;
import imu.iWaystone.Upgrades.PlayerUpgradePanel;
import imu.iWaystone.Upgrades.UpgradeCastTime;
import imu.iWaystone.Upgrades.UpgradeCooldown;
import imu.iWaystone.Upgrades.UpgradeDimension;
import imu.iWaystone.Upgrades.UpgradeXPusage;
import imu.iWaystones.Main.ImusWaystones;

public class Waystone 
{
	private String _name="&6New Waystone";
	//private Block _top,_mid,_low;
	private UUID _owner_uuid;
	private String _owner_name;
	private UUID _uuid;
	private Location _loc;
	private ItemStack _displayItem = new ItemStack(Material.BLACKSTONE_WALL);
	private ArmorStand _hologram;
	private HashMap<UUID, PlayerUpgradePanel> _playerUpgradePanel = new HashMap<>();
	public Waystone(Location loc) 
	{
		_uuid = UUID.randomUUID();		
		_loc = loc;
		//System.out.println("waystone location created to: "+_loc.toVector());
		//_top = top; _mid = mid; _low = low;
	}
	
	
	public void CreateHologram()
	{
		new BukkitRunnable() {
			@Override
			public void run() 
			{
				ArmorStand hologram = _hologram == null ? Metods._ins.CreateHologram(_name, _loc.clone().add(0.5, 0.5, 0.5)) : _hologram;
				hologram.setCustomName(Metods.msgC(_name));
				Metods._ins.setPersistenData(hologram, ImusWaystones._instance.GetWaystoneManager().pd_waystoneHolo, PersistentDataType.STRING, _uuid.toString());
				_hologram = hologram;
				//System.out.println("holo created: "+_hologram.getLocation().toVector());
			}
		}.runTask(ImusWaystones._instance);
		
		
	}
	
	public PlayerUpgradePanel GetPlayerUpgradePanel(UUID uuid_player)
	{
		if(!_playerUpgradePanel.containsKey(uuid_player)) _playerUpgradePanel.put(uuid_player, new PlayerUpgradePanel(new UpgradeCastTime(), new UpgradeCooldown(), new UpgradeDimension(), new UpgradeXPusage()));
		return _playerUpgradePanel.get(uuid_player);
	}
	
	public void SetPlayerUpgrade(UUID uuid_player, BaseUpgrade upgrade)
	{
		if(!_playerUpgradePanel.containsKey(uuid_player)) _playerUpgradePanel.put(uuid_player, new PlayerUpgradePanel(new UpgradeCastTime(), new UpgradeCooldown(), new UpgradeDimension(), new UpgradeXPusage()));
		
		_playerUpgradePanel.get(uuid_player).SetUpgrade(upgrade);
	}
	
	public HashMap<UUID, PlayerUpgradePanel> GetPlayerUpgrades()
	{
		return _playerUpgradePanel;
	}
	
	public ArmorStand GetHologram()
	{
		return _hologram;
	}
	
	public Location GetLoc()
	{
		return _loc;
	}
	
	public String GetName()
	{
		return _name;
	}
	
	public void SetName(String name)
	{
		_name = name;
		CreateHologram();
	}
	
	public void SetUUID(UUID uuid)
	{
		_uuid = uuid;
	}
	
	public ItemStack GetDisplayItem()
	{
		return _displayItem;
	}
	
	public void SetDisplayitem(ItemStack stack)
	{
		_displayItem = stack;
	}
	
	public UUID GetUUID()
	{
		return _uuid;
	}
	
	public void SetOwner(Player player)
	{
		_owner_uuid = player.getUniqueId();
		_owner_name = player.getName();
	}
	
	public void SetOwner(String name, UUID uuid)
	{
		_owner_name = name;
		_owner_uuid = uuid;
	}
	
	public UUID GetOwnerUUID()
	{
		return _owner_uuid;
	}
	
	public String GetOwnerName()
	{
		return _owner_name;
	}
	
	public void SendMessageToOwner(String str)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() {
				try {
					Bukkit.getServer().getPlayer(_owner_uuid).sendMessage(Metods.msgC(str));
				} 
				catch (Exception e) 
				{
					//Bukkit.getLogger().info("Couldnt inform owner!");
				}
			}
		}.runTaskAsynchronously(ImusWaystones._instance);
	}
	
	public Block GetTopBlock()
	{
		
		return _loc.getWorld().getBlockAt(_loc.getBlockX(), _loc.getBlockY()+2, _loc.getBlockZ());
	}
	
	public Block GetMidBlock()
	{
		return _loc.getWorld().getBlockAt(_loc.getBlockX(), _loc.getBlockY()+1, _loc.getBlockZ());
	}
	
	public Block GetLowBlock()
	{
		return _loc.getWorld().getBlockAt(_loc.getBlockX(), _loc.getBlockY(), _loc.getBlockZ());
	}
}

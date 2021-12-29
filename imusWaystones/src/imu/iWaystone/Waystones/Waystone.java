package imu.iWaystone.Waystones;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;
import imu.iAPI.Other.XpUtil;
import imu.iWaystone.Upgrades.BaseUpgrade;
import imu.iWaystone.Upgrades.BuildUpgrade;
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
	
	private double base_cooldown = 600;
	private double base_casttime = 16;
	private double base_xpUsage = 2.5;
	private ImusWaystones _main = ImusWaystones._instance;
	
	private final double _move_telecancel_dis = 0.2;
	private Cooldowns _cds = new Cooldowns();
	private BuildUpgrade _buildUpgrade;
	public Waystone(Location loc) 
	{
		_uuid = UUID.randomUUID();		
		_loc = loc;
		//System.out.println("waystone location created to: "+_loc.toVector());
		//_top = top; _mid = mid; _low = low;
	}
	
	public void SetBuildUpgrade(BuildUpgrade buildUpgrade)
	{
		_buildUpgrade = buildUpgrade;
	}
	
	public BuildUpgrade GetBuildUpgrade()
	{
		return _buildUpgrade;
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
		//System.out.println("Setting upgrade to player: "+uuid_player + " upgrade: "+upgrade + " tier: "+upgrade.GetCurrentTier());
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


	public double getBase_cooldown() {
		return base_cooldown;
	}


	public void setBase_cooldown(int base_cooldown) {
		this.base_cooldown = base_cooldown;
	}


	public double getBase_casttime() {
		return base_casttime;
	}


	public void setBase_casttime(int base_casttime) {
		this.base_casttime = base_casttime;
	}


	public double getBase_xpUsage() {
		return base_xpUsage;
	}


	public void setBase_xpUsage(double base_xpUsage) {
		this.base_xpUsage = base_xpUsage;
	}
	
	public void SetCooldownPlayer(Player player)
	{
		_cds.setCooldownInSeconds(player.getUniqueId().toString(), GetValue(GetPlayerUpgrades().get(player.getUniqueId()).get_cooldown()));
	}
	
	public boolean IsCooldown(Player player)
	{
		return !_cds.isCooldownReady(player.getUniqueId().toString());
	}
	
	public String GetCooldown(Player player)
	{
		return _cds.GetCdInReadableTime(player.getUniqueId().toString());
	}
	
	public Double GetValue(BaseUpgrade upgrade)
	{
		if(upgrade instanceof UpgradeCastTime)
		{		
			return upgrade.GetCombinedValue(base_casttime);
		}

		if(upgrade instanceof UpgradeXPusage)
		{		
			return upgrade.GetCombinedValue(base_xpUsage);
		}
		
		if(upgrade instanceof UpgradeCooldown)
		{		
			return upgrade.GetCombinedValue(base_cooldown);
		}
		return null;
	}
	
	public boolean HasEnoughExpToTeleport(Player player)
	{
		double neededXP = _playerUpgradePanel.get(player.getUniqueId()).get_xpUsage().GetCombinedValue(base_xpUsage);

		if((XpUtil.GetPlayerLevel(player) - neededXP) >= 0)
		{
			return true;
		}
		return false;
	}
	
	public void ReduceExp(Player player)
	{
		double neededXP = _playerUpgradePanel.get(player.getUniqueId()).get_xpUsage().GetCombinedValue(base_xpUsage);
		XpUtil.SetPlayerLevel(player, XpUtil.GetPlayerLevel(player) - neededXP);
	}
	
	public void StartTeleporting(Player player, Waystone target_waystone)
	{
		if(_main.GetWaystoneManager().IsTeleporting(player)) return;
		
		PlayerUpgradePanel panel = _playerUpgradePanel.get(player.getUniqueId());
		Waystone thiss = this;
		UUID uuid_player = player.getUniqueId();
		Location startLoc = player.getLocation().clone();
		_main.GetWaystoneManager().SetTeleporting(player);
		new BukkitRunnable() 
		{
			
			int seconds = (int)panel.get_castTime().GetCombinedValue(base_casttime);
			void TeleCancel()
			{
				ImusWaystones._instance.GetWaystoneManager().RemoveTeleportin(uuid_player);
				if(player != null)
				{
					player.sendTitle(Metods.msgC("&4Teleporting Canceled!"), "", 1, 15, 1);
				}
			}
			
			void TeleConfirm()
			{
				ImusWaystones._instance.GetWaystoneManager().RemoveTeleportin(uuid_player);
				
				if(!_main.GetWaystoneManager().IsValid(target_waystone) || !_main.GetWaystoneManager().IsValid(thiss))
				{
					player.sendMessage(Metods.msgC("&cDestination waystone might be broken!"));
					TeleCancel();
					return;
				}
				if(!HasEnoughExpToTeleport(player)) 
				{
					player.sendMessage(Metods.msgC("&cYou don't have enough xp to teleport!"));
					TeleCancel();
					return;
				}
				
				
				new BukkitRunnable() {
					
					@Override
					public void run() 
					{
						int x = ThreadLocalRandom.current().nextInt(-2,2);
						int z = ThreadLocalRandom.current().nextInt(-2,2);
						
						x = x == 0 ? 1 : x;
						z = z == 0 ? 1 : z;
						
						player.teleport(target_waystone.GetLoc().clone().add(x, 1, z));
						ReduceExp(player);
						SetCooldownPlayer(player);
					}
				}.runTask(_main);
				
			}
			
			@Override
			public void run() 
			{

				if(startLoc.getWorld() != player.getWorld() || startLoc.distance(player.getLocation()) > _move_telecancel_dis || player == null)
				{
					TeleCancel();					
					this.cancel();
					return;
				}
				
				if(seconds <= 0)
				{
					TeleConfirm();
					this.cancel();
					return;
				}
				
				player.sendTitle(Metods.msgC("&2Teleporting in &5"+seconds+" &2s"), Metods.msgC("&4Don't move"), 1, 21, 1);
				player.playSound(_loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.1f);
				seconds--;
			}
			
		}.runTaskTimerAsynchronously(ImusWaystones._instance, 0, 20);
	}
	
	
}
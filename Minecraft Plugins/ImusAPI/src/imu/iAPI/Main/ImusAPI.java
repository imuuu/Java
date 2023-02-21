package imu.iAPI.Main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;
import imu.iAPI.Other.MySQLHelper;
import imu.iAPI.Other.ProtocolLibUtil;


public class ImusAPI extends JavaPlugin
{
	public static ImusAPI _instance;
	public static Metods _metods;
	public static MySQLHelper _sqlHelper;
	private HashMap<UUID, CustomInvLayout> _openedInvs = new HashMap<>();
	private ProtocolManager _protocolManager;
	private ProtocolLibUtil _protocolLibUtil;
	
	public static HashSet<Material> AirHashSet;
	public static HashSet<Material> Ores;
	public static HashSet<EntityType> EntitiesNoBosses;
	private List<EntityType> _excludeEntityTypes = Arrays.asList(
			EntityType.WANDERING_TRADER, 
			EntityType.WARDEN, 
			EntityType.ARMOR_STAND, 
			EntityType.GIANT, 
			EntityType.ENDER_DRAGON, 
			EntityType.WITHER,
			EntityType.ELDER_GUARDIAN

	);
	
	@Override
	public void onEnable() 
	{
		_instance = this;
		_metods = new Metods(this);
		_sqlHelper = new MySQLHelper();
		
		_protocolManager = ProtocolLibrary.getProtocolManager();
		//setup();
		_protocolLibUtil = new ProtocolLibUtil();
		
		AirHashSet = new HashSet<>();
		AirHashSet.add(Material.AIR);
		InitOres();
		InitEntities();
		
		
	}
	
	@Override
	public void onDisable() 
	{
		for(CustomInvLayout inv : _openedInvs.values())
		{
			inv.GetPlayer().closeInventory();
		}
		_openedInvs.clear();
	}
	
	public void RegisterInvOpen(CustomInvLayout inv)
	{
		_openedInvs.put(inv.GetPlayer().getUniqueId(),inv);
	}
	
	public void UnregisterInv(CustomInvLayout inv)
	{
		_openedInvs.remove(inv.GetPlayer().getUniqueId());
	}
	
	public CustomInvLayout GetOpenedInv(Player player)
	{
		return _openedInvs.get(player.getUniqueId());
	}
	
	public ProtocolManager GetProtocolManager()
	{
		return _protocolManager;
	}
	
	public ProtocolLibUtil GetProtocolLibUtil()
	{
		return _protocolLibUtil;
	}
	
	private void InitEntities()
	{
		EntitiesNoBosses = new HashSet<>();
		
		for (EntityType entityType : EntityType.values())
		{
			if (entityType.isAlive() && entityType.isSpawnable() && !_excludeEntityTypes.contains(entityType))
			{
				EntitiesNoBosses.add(entityType);
			}
		}
	}
	private void InitOres()
	{
		Ores = new HashSet<>();
		
		Ores.add(Material.COAL_ORE);
		Ores.add(Material.DIAMOND_ORE);
		Ores.add(Material.EMERALD_ORE);
		Ores.add(Material.GOLD_ORE);
		Ores.add(Material.IRON_ORE);
		Ores.add(Material.LAPIS_ORE);
        Ores.add(Material.NETHER_QUARTZ_ORE);
        Ores.add(Material.REDSTONE_ORE);
        Ores.add(Material.ANCIENT_DEBRIS);
        Ores.add(Material.GLOWSTONE);
        Ores.add(Material.NETHER_GOLD_ORE);
        Ores.add(Material.COPPER_ORE);
        
        Ores.add(Material.DEEPSLATE_COAL_ORE);
        Ores.add(Material.DEEPSLATE_DIAMOND_ORE);
        Ores.add(Material.DEEPSLATE_EMERALD_ORE);
        Ores.add(Material.DEEPSLATE_GOLD_ORE);
        Ores.add(Material.DEEPSLATE_IRON_ORE);
        Ores.add(Material.DEEPSLATE_LAPIS_ORE);
        Ores.add(Material.DEEPSLATE_REDSTONE_ORE);
	}
//	boolean setup()
//	{
//		if(Bukkit.getPluginManager().getPlugin("imusAPI") != null)
//		{
//			ChestCleaner cc = (ChestCleaner) Bukkit.getPluginManager().getPlugin("ChestCleaner");
//			System.out.println("LOADING CHESTCLEANER");
//			
//			
//			return true;
//		}
//		return false;
//	}
	
//	boolean setupImusApi()
//	{
//		if(Bukkit.getPluginManager().getPlugin("imusAPI") != null)
//		{
//			_imusAPI = (ImusAPI) Bukkit.getPluginManager().getPlugin("imusAPI");
//			return true;
//		}
//		return false;
//	}
	
}

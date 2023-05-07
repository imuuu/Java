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

import imu.iAPI.CmdUtil.CmdHelper;
import imu.iAPI.Commands.ExampleCmd;
import imu.iAPI.FastInventory.Manager_FastInventories;
import imu.iAPI.Handelers.CommandHandler;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.ImusTabCompleter;
import imu.iAPI.Other.Metods;
import imu.iAPI.Other.MySQLHelper;
import imu.iAPI.Other.ProtocolLibUtil;
import imu.iAPI.SubCommands.Sub_Cmd_OpenNamedInvs;

public class ImusAPI extends JavaPlugin
{
	public static ImusAPI _instance;
	public static Metods _metods;
	public static MySQLHelper _sqlHelper;
	private Manager_FastInventories _fastInvs;
	private HashMap<UUID, CustomInvLayout> _openedInvs = new HashMap<>();
	private ProtocolManager _protocolManager;
	private ProtocolLibUtil _protocolLibUtil;

	public static HashSet<Material> AirHashSet;
	public static HashSet<Material> Ores;
	public static HashSet<EntityType> EntitiesNoBosses;
	private List<EntityType> _excludeEntityTypes = Arrays.asList(EntityType.WANDERING_TRADER, EntityType.WARDEN,
			EntityType.ARMOR_STAND, EntityType.GIANT, EntityType.ENDER_DRAGON, EntityType.WITHER,
			EntityType.ELDER_GUARDIAN

	);

	final private String _pluginName = "[imusAPI]";
	private CmdHelper _cmdHelper;
	private ImusTabCompleter _tab_cmd1;
	
	@Override
	public void onEnable()
	{
		_instance = this;
		_metods = new Metods(this);
		_sqlHelper = new MySQLHelper();
		_fastInvs = new Manager_FastInventories();

		_protocolManager = ProtocolLibrary.getProtocolManager();
		// setup();
		_protocolLibUtil = new ProtocolLibUtil();
		
		RegisterCommands();
		AirHashSet = new HashSet<>();
		AirHashSet.add(Material.AIR);
		AirHashSet.add(Material.VOID_AIR);
		AirHashSet.add(Material.CAVE_AIR);

		InitOres();
		InitEntities();
		

	}

	@Override
	public void onDisable()
	{
		for (CustomInvLayout inv : _openedInvs.values())
		{
			inv.GetPlayer().closeInventory();
		}
		_openedInvs.clear();
	}

	public void RegisterCommands()
	{
		_cmdHelper = new CmdHelper(_pluginName);

		HashMap<String, String[]> cmd1AndArguments = new HashMap<>();
		CommandHandler handler = new CommandHandler(this);
		String cmd1 = "ia";
		handler.registerCmd(cmd1, new ExampleCmd());
		
//		String cmd1_sub2 = "follow"; 
//		String full_sub2 = cmd1 + " " + cmd1_sub2;
//		_cmdHelper.setCmd(full_sub2, "Follow the player", full_sub2);
//		handler.registerSubCmd(cmd1, cmd1_sub2, new Sub_Cmd_FollowPlayer(_cmdHelper.getCmdData(full_sub2)));
//		handler.setPermissionOnLastCmd("ia.follow");
		
		String cmd1_sub1 = "inv"; 
		String full_sub1 = cmd1 + " " + cmd1_sub1;
		_cmdHelper.setCmd(full_sub1, "Open Custom Invs", full_sub1);
		handler.registerSubCmd(cmd1, cmd1_sub1, new Sub_Cmd_OpenNamedInvs(_cmdHelper.getCmdData(full_sub1)));
		handler.setPermissionOnLastCmd("ia.inv");
		_fastInvs.SetBaseID(cmd1_sub1); // this needs bc it adds tabcomplete things for it
		
		
		
		//cmd1AndArguments.put(cmd1, new String[] { "inv", "follow" });
		cmd1AndArguments.put(cmd1, new String[] { "inv"});
		//cmd1AndArguments.put("inv", new String[] {"test123"});

		// register cmds
		getCommand(cmd1).setExecutor(handler);

		// register tabcompleters
		_tab_cmd1 = new ImusTabCompleter(cmd1, cmd1AndArguments, "ia.tabcompleter");

		getCommand(cmd1).setTabCompleter(_tab_cmd1);
		
		//_fastInvs.AddFastInventory(new Fast_Inventory("tamaPerkl"));

	}
	
	public ImusTabCompleter GetCMD1_TabCompleter()
	{
		return _tab_cmd1;
	}
	public void RegisterInvOpen(CustomInvLayout inv)
	{
		_openedInvs.put(inv.GetPlayer().getUniqueId(), inv);
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

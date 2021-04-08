package imu.iMiniGames.Main;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Commands.ImgCreateCmd;
import imu.iMiniGames.Commands.ImgMgCmd;
import imu.iMiniGames.Handlers.CombatGameHandler;
import imu.iMiniGames.Handlers.CommandHandler;
import imu.iMiniGames.Handlers.SpleefGameHandler;
import imu.iMiniGames.Managers.CombatManager;
import imu.iMiniGames.Managers.PlanerManager;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.ConfigMaker;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.SubCommands.subBlockMECmd;
import imu.iMiniGames.SubCommands.subCombatArenaCmd;
import imu.iMiniGames.SubCommands.subCombatGamePlanerCmd;
import imu.iMiniGames.SubCommands.subKitCmd;
import imu.iMiniGames.SubCommands.subKitGetCmd;
import imu.iMiniGames.SubCommands.subKitListCmd;
import imu.iMiniGames.SubCommands.subSpectateCmd;
import imu.iMiniGames.SubCommands.subAcceptCmd;
import imu.iMiniGames.SubCommands.subSpleefArenaCmd;
import imu.iMiniGames.SubCommands.subSpleefGamePlanerCmd;
import imu.iMiniGames.TabCompletes.cmd2_tab;
import imu.iMiniGames.TabCompletes.cmd3_tab;
import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin
{
	SpleefManager _spleefManager;
	CombatManager _combatManager;
	
	SpleefGameHandler _spleefGameHandler;
	CombatGameHandler _combatGameHandler;
	
	
	Economy _econ = null;
	
	
	PlanerManager _planerManager;
	
	ItemMetods _itemM;
	
	boolean _enable_broadcast_spleef = true;
	
	HashMap<UUID, Boolean> _block_me = new HashMap<>();
	
	@Override
	public void onEnable() 
	{
		setupEconomy();
		_itemM = new ItemMetods(this);
		_spleefManager = new SpleefManager(this);
		_combatManager = new CombatManager(this);
		_planerManager = new PlanerManager(this);
		_spleefGameHandler = new SpleefGameHandler(this);
		_combatGameHandler = new CombatGameHandler(this);
		
		 
		 
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" imusMiniGames has been activated!");
		registerCommands();
		
		//getServer().getPluginManager().registerEvents(new EventClass(), this);
		_spleefManager.onEnable();
		
		makeSpleef_SettingsConfig(false);
		makeSpleef_BlockedPotionEffectsConfig();
		make_BlockMeConfig(false);
		
		_combatManager.loadPotionsConfig();
		_combatManager.loadArenas();
		_combatGameHandler.loadSettingConfig(false);
		_combatManager.loadKits();
	}
	
	@Override
	 public void onDisable()
	{
		_spleefManager.onDisable();
		_spleefGameHandler.onnDisable();
		make_BlockMeConfig(true);
	}
	
	public void registerCommands() 
	{
		 
		 CommandHandler handler = new CommandHandler(this);

//	     String cmd1 = "mg";      
//	     handler.registerCmd(cmd1, new ImgCmd(this));
//	     
	     
	     String cmd2 ="img";
	     handler.registerCmd(cmd2, new ImgCreateCmd(this));
	     handler.setPermissionOnLastCmd("img");
	     
//	     String cmd2_sub1 ="create spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub1, new subCreateSlpeefArenaCmd(this,cmd2_sub1));
//	     
//	     String cmd2_sub2 ="pos spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub2, new subSpleefCornerPosCmd(this, cmd2_sub2));
//	     
//	     String cmd2_sub3 ="spawn spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub3, new subSpleefSpawnPositionCmd(this, cmd2_sub3));
//	     
//	     String cmd2_sub4 ="save spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub4, new subSpleefSaveCmd2(this, cmd2_sub4));
//	     
//	     String cmd2_sub5 ="clear spawn spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub5, new subSpleefClearSpawnPositionsCmd(this, cmd2_sub5));
//	     
//	     String cmd2_sub6 ="remove spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub6, new subSpleefRemoveCmd(this, cmd2_sub6));
//	     
//	     String cmd2_sub7 ="lobby spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub7, new subSpleefSpecLobbyPosCmd(this, cmd2_sub7));
//	     
//	     String cmd2_sub8 ="reload";
//	     handler.registerSubCmd(cmd2, cmd2_sub8, new subReloadCmd(this, cmd2_sub8));
//	     
//	     String cmd2_sub9 ="desc spleef";
//	     handler.registerSubCmd(cmd2, cmd2_sub9, new subSpleefSetDescriptionCmd(this, cmd2_sub9));
	     
	     HashMap<String, String[]> cmd2AndArguments = new HashMap<>();
	     HashMap<String, String[]> cmd3AndArguments = new HashMap<>();
	     
	     String cmd2_sub11 ="spleef";
	     String[] cmd2_sub11_sub = {"create", "spawn", "pos", "save", "remove", "lobby","desc"};
	     handler.registerSubCmd(cmd2, cmd2_sub11, new subSpleefArenaCmd(this, cmd2_sub11_sub));
	     
	     String cmd2_sub10 ="combat";
	     String[] cmd2_sub10_sub = {"create", "spawn", "middle", "save", "remove", "lobby","desc","kit"};	     
	     handler.registerSubCmd(cmd2, cmd2_sub10, new subCombatArenaCmd(this, cmd2_sub10_sub));
	     cmd2AndArguments.put(cmd2_sub10, cmd2_sub10_sub);
	     	     
	     String cmd2_sub12 ="combat kit";
	     String[] cmd2_sub12_sub = {"create","list"};	     
	     handler.registerSubCmd(cmd2, cmd2_sub12, new subKitCmd(this, cmd2_sub12_sub));
	     cmd2AndArguments.put("kit", cmd2_sub12_sub);
	     
	     String cmd2_sub13 ="combat kit list";    
	     handler.registerSubCmd(cmd2, cmd2_sub13, new subKitListCmd(this, cmd2_sub13));
	     
	     String cmd2_sub14 ="combat kit get";    
	     handler.registerSubCmd(cmd2, cmd2_sub14, new subKitGetCmd(this));
	     
	     
	     cmd2AndArguments.put(cmd2_sub11, cmd2_sub11_sub);
	     cmd2AndArguments.put(cmd2, new String[] {cmd2_sub11,cmd2_sub10,"reload"});
	     
	    
	     
	     
	     String cmd3 ="mg";
	     handler.registerCmd(cmd3, new ImgMgCmd(this));
	     
	     String cmd3_sub1 = "spleef";
	     handler.registerSubCmd(cmd3, cmd3_sub1, new subSpleefGamePlanerCmd(this, cmd3_sub1));
	     
	     String cmd3_sub2 = "spleef accept";
	     handler.registerSubCmd(cmd3, cmd3_sub2, new subAcceptCmd(this, cmd3_sub2));
	     
	     String cmd3_sub3 = "block";
	     handler.registerSubCmd(cmd3, cmd3_sub3, new subBlockMECmd(this, cmd3_sub3));
	     
	     String cmd3_sub4 = "combat";
	     handler.registerSubCmd(cmd3, cmd3_sub4, new subCombatGamePlanerCmd(this, cmd3_sub4));
	     
	     String cmd3_sub5 = "combat accept";
	     handler.registerSubCmd(cmd3, cmd3_sub5, new subAcceptCmd(this, cmd3_sub5));
	     
	     String cmd3_sub6 = "spectate";
	     handler.registerSubCmd(cmd3, cmd3_sub6, new subSpectateCmd(this, cmd3_sub6));
	     
	     
	     cmd3AndArguments.put(cmd3, new String[] {cmd3_sub1,cmd3_sub3,cmd3_sub4});
	     
	     getCommand(cmd3).setExecutor(handler);
	     getCommand(cmd2).setExecutor(handler);
	     
	     getCommand(cmd2).setTabCompleter(new cmd2_tab(this,cmd2,cmd2AndArguments));
	     getCommand(cmd3).setTabCompleter(new cmd3_tab(this, cmd3, cmd3AndArguments));
	 }
	 
	
	public CombatGameHandler get_combatGameHandler() {
		return _combatGameHandler;
	}

	
	public CombatManager get_combatManager() {
		return _combatManager;
	}

	public void sendBlockedmsg(Player p)
	{
		p.sendMessage(ChatColor.GOLD + "You have block yourself from minigames. You can open this lock by writing /mg block");
	}
	
	public boolean isPlayerBlocked(Player p)
	{
		if(_block_me.containsKey(p.getUniqueId()))
		{
			if(_block_me.get(p.getUniqueId()) == true)
			{
				return true;
			}
		}
		return false;
	}
	
	public void putPlayerBlockMe(Player p, boolean blockme)
	{
		_block_me.put(p.getUniqueId(), blockme);
	}
	public void removePlayerBlockMe(Player p)
	{
		_block_me.remove(p.getUniqueId());
	}
	
	public boolean isEnable_broadcast_spleef() {
		return _enable_broadcast_spleef;
	}

	public Economy get_econ() {
			return _econ;
		}

	public SpleefGameHandler get_spleefGameHandler() {
			return _spleefGameHandler;
		}

	public SpleefManager get_spleefManager() 
	{
		return _spleefManager;
	}
	 
	public PlanerManager get_planerManager() 
	{
		return _planerManager;
	}

	public ItemMetods get_itemM() 
	{
		return _itemM;
	}
	
	public void makeSpleef_SettingsConfig(boolean refresh)
	{
		Main main = this;
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				ConfigMaker cm = new ConfigMaker(main, "Spleef_settings.yml");
				FileConfiguration config = cm.getConfig();
				if(refresh)
				{
					if(cm.isExists())
					{
						_spleefGameHandler.setCd_invite_time(config.getInt("Cd_for_invite_acceptTime(Integer)"));
						_spleefGameHandler.setSpleef_roundTime(config.getInt("Spleef_roundTime(Integer)"));
						_spleefGameHandler.setBet_fee_percent(config.getDouble("Spleef_bet_fee(Double)"));
						_enable_broadcast_spleef = config.getBoolean("Enable_spleef_broadCast(Boolean)");
						_spleefGameHandler.set_anti_block_time(config.getInt("Spleef_antiBlock_time(Integer)"));
					}
					cm.clearConfig();
				}
				try 
				{
					_spleefGameHandler.setCd_invite_time(cm.addDefault("Cd_for_invite_acceptTime", _spleefGameHandler.getCd_invite_time(),"Cd_for_invite_acceptTime: how long invite stays before expires"));
					_spleefGameHandler.setSpleef_roundTime(cm.addDefault("Spleef_roundTime", _spleefGameHandler.getSpleef_roundTime(),"Spleef_roundTime: Round time for spleef"));
					_spleefGameHandler.setBet_fee_percent(cm.addDefault("Spleef_bet_fee", _spleefGameHandler.getBet_fee_percent(),"Spleef_bet_fee: How much fee is. Between 0.00 - 1.00 (0.05 = 5%)"));
					_enable_broadcast_spleef = (cm.addDefault("Enable_spleef_broadCast", _enable_broadcast_spleef,"Enable_spleef_broadCast: If true everybody see in server who startet game and result"));
					_spleefGameHandler.set_anti_block_time(cm.addDefault("Spleef_antiBlock_time", _spleefGameHandler.get_anti_block_time(),"Spleef_antiBlock_time: How many seconds before anti_block shows. If 0 => disabled"));
					
					cm.addComments();
					
				} catch (Exception e) 
				{
					getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Something got wrong imusMiniGame fileNamed: "+cm.getFileName());
					getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Maybe you casted some value as Integer When it should be Double?");
				}		
			}
		}.runTaskAsynchronously(this);
		
	}
	public void makeSpleef_BlockedPotionEffectsConfig()
	{
		Main main = this;
		new BukkitRunnable() 
		{		
			@Override
			public void run() 
			{
				ConfigMaker cm = new ConfigMaker(main, "Spleef/Enabled_PotionEffects.yml");
				FileConfiguration config = cm.getConfig();
				
				if(!cm.isExists())
				{
					for(Entry<PotionEffectType, Boolean> entry : _spleefManager.getPotionEffects().entrySet())
					{
						config.set(entry.getKey().getName(), entry.getValue());
					}
				}
				else
				{
					_spleefManager.getPotionEffects().clear();
					for(PotionEffectType t : PotionEffectType.values())
					{
						Boolean value = config.getBoolean(t.getName());
						_spleefManager.getPotionEffects().put(t, value);
					}
				}
				
				cm.saveConfig();
			}
			
		}.runTaskAsynchronously(this);
	}
	
	public void make_BlockMeConfig(boolean saveData)
	{		
		if(saveData)
		{
			ConfigMaker cm = new ConfigMaker(this, "Block_Me.yml");
			FileConfiguration config = cm.getConfig();
			if(_block_me.isEmpty())
			{
				cm.clearConfig();
			}else
			{
				for(Entry<UUID, Boolean> entry : _block_me.entrySet())
				{
					config.set(entry.getKey().toString(), entry.getValue());
				}
			}
			
			cm.saveConfig();
		}
		else
		{
			Main main = this;			
			new BukkitRunnable() 
			{				
				@Override
				public void run() 
				{
					ConfigMaker cm = new ConfigMaker(main, "Block_Me.yml");
					FileConfiguration config = cm.getConfig();
					_block_me.clear();
					for(String key : config.getConfigurationSection("").getKeys(false))					 
					{
						UUID uuid = UUID.fromString(key);
						Boolean b = config.getBoolean(key);
						_block_me.put(uuid, b);
					}
					cm.saveConfig();
				}
			}.runTaskAsynchronously(this);
			
		}
		
	
	}
	
	boolean setupEconomy() 
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
        	System.out.println("Vault not found");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        
        if (rsp == null) {
            return false;
        }
        _econ = rsp.getProvider();
        return _econ != null;
    }
}

package imu.iMiniGames.Main;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Commands.ImgCreateCmd;
import imu.iMiniGames.Commands.ImgMgCmd;
import imu.iMiniGames.Handlers.CommandHandler;
import imu.iMiniGames.Handlers.SpleefGameHandler;
import imu.iMiniGames.Managers.PlanerManager;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.ConfigMaker;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.SubCommands.subCreateSlpeefArenaCmd;
import imu.iMiniGames.SubCommands.subReloadCmd;
import imu.iMiniGames.SubCommands.subSpleefAcceptCmd;
import imu.iMiniGames.SubCommands.subSpleefClearSpawnPositionsCmd;
import imu.iMiniGames.SubCommands.subSpleefCornerPosCmd;
import imu.iMiniGames.SubCommands.subSpleefGamePlanerCmd;
import imu.iMiniGames.SubCommands.subSpleefRemoveCmd;
import imu.iMiniGames.SubCommands.subSpleefSaveCmd2;
import imu.iMiniGames.SubCommands.subSpleefSetDescriptionCmd;
import imu.iMiniGames.SubCommands.subSpleefSpawnPositionCmd;
import imu.iMiniGames.SubCommands.subSpleefSpecLobbyPosCmd;
import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin
{
	SpleefManager _spleefManager;
	SpleefGameHandler _spleefGameHandler;
	Economy _econ = null;
	
	
	PlanerManager _planerManager;
	
	ItemMetods _itemM;
	
	boolean _enable_broadcast_spleef = true;
	
	
	@Override
	public void onEnable() 
	{
		setupEconomy();
		_itemM = new ItemMetods(this);
		_spleefManager = new SpleefManager(this);
		_planerManager = new PlanerManager(this);
		_spleefGameHandler = new SpleefGameHandler(this);
		
		 
		 
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" imusMiniGames has been activated!");
		registerCommands();
		
		//getServer().getPluginManager().registerEvents(new EventClass(), this);
		_spleefManager.onEnable();
		
		makeSpleef_SettingsConfig(false);
		makeSpleef_BlockedPotionEffectsConfig(false);
	}
	
	@Override
	 public void onDisable()
	{
		_spleefManager.onDisable();
		_spleefGameHandler.onnDisable();
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
	     
	     String cmd2_sub1 ="create spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub1, new subCreateSlpeefArenaCmd(this,cmd2_sub1));
	     
	     String cmd2_sub2 ="pos spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub2, new subSpleefCornerPosCmd(this, cmd2_sub2));
	     
	     String cmd2_sub3 ="spawn spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub3, new subSpleefSpawnPositionCmd(this, cmd2_sub3));
	     
	     String cmd2_sub4 ="save spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub4, new subSpleefSaveCmd2(this, cmd2_sub4));
	     
	     String cmd2_sub5 ="clear spawn spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub5, new subSpleefClearSpawnPositionsCmd(this, cmd2_sub5));
	     
	     String cmd2_sub6 ="remove spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub6, new subSpleefRemoveCmd(this, cmd2_sub6));
	     
	     String cmd2_sub7 ="lobby spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub7, new subSpleefSpecLobbyPosCmd(this, cmd2_sub7));
	     
	     String cmd2_sub8 ="reload";
	     handler.registerSubCmd(cmd2, cmd2_sub8, new subReloadCmd(this, cmd2_sub8));
	     
	     String cmd2_sub9 ="desc spleef";
	     handler.registerSubCmd(cmd2, cmd2_sub9, new subSpleefSetDescriptionCmd(this, cmd2_sub9));
	     
	     
	     
	     String cmd3 ="mg";
	     handler.registerCmd(cmd3, new ImgMgCmd(this));
	     
	     String cmd3_sub1 = "spleef";
	     handler.registerSubCmd(cmd3, cmd3_sub1, new subSpleefGamePlanerCmd(this, cmd3_sub1));
	     
	     String cmd3_sub2 = "spleef accept";
	     handler.registerSubCmd(cmd3, cmd3_sub2, new subSpleefAcceptCmd(this, cmd3_sub2));
	     
	     getCommand(cmd3).setExecutor(handler);
	     getCommand(cmd2).setExecutor(handler);
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
	public void makeSpleef_BlockedPotionEffectsConfig(boolean refresh)
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

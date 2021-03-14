package imu.iMiniGames.Main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import imu.iMiniGames.Commands.ImgCreateCmd;
import imu.iMiniGames.Commands.ImgMgCmd;
import imu.iMiniGames.Handlers.CommandHandler;
import imu.iMiniGames.Handlers.SpleefGameHandler;
import imu.iMiniGames.Managers.PlanerManager;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.ItemMetods;
import imu.iMiniGames.SubCommands.subCreateSlpeefArenaCmd;
import imu.iMiniGames.SubCommands.subSpleefAcceptCmd;
import imu.iMiniGames.SubCommands.subSpleefClearSpawnPositionsCmd;
import imu.iMiniGames.SubCommands.subSpleefCornerPosCmd;
import imu.iMiniGames.SubCommands.subSpleefGamePlanerCmd;
import imu.iMiniGames.SubCommands.subSpleefRemoveCmd;
import imu.iMiniGames.SubCommands.subSpleefSaveCmd2;
import imu.iMiniGames.SubCommands.subSpleefSpawnPositionCmd;


public class Main extends JavaPlugin
{
	SpleefManager _spleefManager;
	SpleefGameHandler _spleefGameHandler;
	public SpleefGameHandler get_spleefGameHandler() {
		return _spleefGameHandler;
	}

	PlanerManager _planerManager;
	
	ItemMetods _itemM;
	
	@Override
	public void onEnable() 
	{
		_itemM = new ItemMetods(this);
		_spleefManager = new SpleefManager(this);
		_planerManager = new PlanerManager(this);
		_spleefGameHandler = new SpleefGameHandler(this);
		
		 
		 
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" imusMiniGames has been activated!");
		registerCommands();
		
		//getServer().getPluginManager().registerEvents(new EventClass(), this);
		_spleefManager.onEnable();
	}
	
	@Override
	 public void onDisable()
	{
		_spleefManager.onDisable();
	}
	
	 public void registerCommands() 
	 {
		 
		 CommandHandler handler = new CommandHandler(this);

//	     String cmd1 = "mg";      
//	     handler.registerCmd(cmd1, new ImgCmd(this));
//	     
	     
	     String cmd2 ="img";
	     handler.registerCmd(cmd2, new ImgCreateCmd(this));
	     
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
	     
	     String cmd3 ="mg";
	     handler.registerCmd(cmd3, new ImgMgCmd(this));
	     
	     String cmd3_sub1 = "spleef";
	     handler.registerSubCmd(cmd3, cmd3_sub1, new subSpleefGamePlanerCmd(this, cmd3_sub1));
	     
	     String cmd3_sub2 = "spleef accept";
	     handler.registerSubCmd(cmd3, cmd3_sub2, new subSpleefAcceptCmd(this, cmd3_sub2));
	     
	     getCommand(cmd3).setExecutor(handler);
	     getCommand(cmd2).setExecutor(handler);
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
}

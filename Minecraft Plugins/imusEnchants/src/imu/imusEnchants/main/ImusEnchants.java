package imu.imusEnchants.main;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import imu.iAPI.CmdUtil.CmdHelper;
import imu.iAPI.Commands.ExampleCmd;
import imu.iAPI.Handelers.CommandHandler;
import imu.iAPI.Other.ImusTabCompleter;
import imu.imusEnchants.Events.Events;
import imu.imusEnchants.Managers.ManagerEnchants;
import imu.imusEnchants.SubCommands.SubOpenEnchant_InvCmd;

public class ImusEnchants extends JavaPlugin
{
	public static ImusEnchants Instance;
	
	public ManagerEnchants _managerEnchants;
	
	final private String _pluginName = "[imusEnchants]";

	private CmdHelper _cmdHelper;
	private ImusTabCompleter _tab_cmd1;
	@Override
	public void onEnable()
	{
		Instance = this;
		_managerEnchants = new ManagerEnchants();
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + _pluginName+" is Activated");
		
		getServer().getPluginManager().registerEvents(new Events(), this);
		RegisterCommands();
	}

	public void RegisterCommands()
	{
		_cmdHelper = new CmdHelper(_pluginName);
		
		HashMap<String, String[]> cmd1AndArguments = new HashMap<>();
		CommandHandler handler = new CommandHandler(this);
		String cmd1 = "ie";
		handler.registerCmd(cmd1, new ExampleCmd());

		String cmd1_sub1 = "inv";
		String full_sub1 = cmd1 + " " + cmd1_sub1;
		_cmdHelper.setCmd(full_sub1, "Open Enchant Inv", full_sub1);
		handler.registerSubCmd(cmd1, cmd1_sub1, new SubOpenEnchant_InvCmd(_cmdHelper.getCmdData(full_sub1)));
		handler.setPermissionOnLastCmd("ie.inv");

		cmd1AndArguments.put(cmd1, new String[] { "inv" });
		// cmd1AndArguments.put("create", new String[] {"card"});

		// register cmds
		getCommand(cmd1).setExecutor(handler);

		// register tabcompleters
		_tab_cmd1 = new ImusTabCompleter(cmd1, cmd1AndArguments, "it.tabcompleter");
		getCommand(cmd1).setTabCompleter(_tab_cmd1);

	}

}

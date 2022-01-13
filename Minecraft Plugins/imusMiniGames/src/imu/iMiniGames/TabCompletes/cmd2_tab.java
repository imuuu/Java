package imu.iMiniGames.TabCompletes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import imu.iMiniGames.Main.Main;

public class cmd2_tab implements TabCompleter
{

	HashMap<String, String[]> _cmdAndArguments = new HashMap<>();
	Main _main;
	String _mainCmd;
	public cmd2_tab(Main main,String mainCmd,HashMap<String, String[]> cmdAndArguments) 
	{
		_cmdAndArguments = cmdAndArguments;
		_main = main;
		_mainCmd = mainCmd;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) 
	{
		
		String search_cmd = "";
		if(args.length > 0)
		{
			if(args.length == 1)
			{
				search_cmd = _mainCmd.toLowerCase();
			}
			if(args.length > 1)
			{
				search_cmd = args[args.length-2].toLowerCase();
			}
		}
		
		
		String[] arguments = _cmdAndArguments.containsKey(search_cmd) == true ? _cmdAndArguments.get(search_cmd) : null;


		if(arguments != null)
		{
			List<String> result = new ArrayList<>();
			for(String a : arguments)
			{
				if(a.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
				{
					result.add(a);
				}				
			}
			return result;
		}
		return null;
		
	}

}

package imu.iAPI.Other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import imu.iAPI.Main.ImusAPI;

public class ImusTabCompleter implements TabCompleter
{
	class Rule
	{
		public String _cmd_rule;
		public List<String> _arguments;
		Rule(String cmd, List<String> arguments)
		{
			_cmd_rule = cmd;
			_arguments = arguments;
		}
	}
	private HashMap<String, String[]> _cmdAndArguments = new HashMap<>();
	private String _mainCmd;
	private HashMap<Integer, ArrayList<Rule>> _rules = new HashMap<>();
	private String _permission = null;
	
	public ImusTabCompleter(String mainCmd, HashMap<String, String[]> cmdAndArguments, String permission) 
	{
		_cmdAndArguments = cmdAndArguments;
		_mainCmd = mainCmd;
		_permission = permission;
	}
	
	public void setArgumenrs(String argumentName, String[] argumnets)
	{
		_cmdAndArguments.put(argumentName, argumnets);
	}
	
	public void RemoveArgument(String argumentName)
	{
		_cmdAndArguments.remove(argumentName);
	}
	
	
 /*
  * example cmdRule = gs assign, args_lenght_to_enable = 3
  * 
  */
	
	public void SetRule(String cmdRule, int args_lenght_to_enable, List<String> arguments_result)
	{
		if(!_rules.containsKey(args_lenght_to_enable)) _rules.put(args_lenght_to_enable, new ArrayList<Rule>());
		//_rules.put(args_lenght_to_enable, new Tuple<String, List<String>>(cmdRule.toLowerCase(), arguments_result));
		
		if(_rules.containsKey(args_lenght_to_enable))
		{
			for(Rule r : _rules.get(args_lenght_to_enable))
			{
				if(r._cmd_rule.equalsIgnoreCase(cmdRule)) {r._arguments = arguments_result;return;}
			}
		}
		Rule rule = new Rule(cmdRule, arguments_result);
		_rules.get(args_lenght_to_enable).add(rule);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) 
	{		
		if(_permission != null && !sender.hasPermission(_permission)) return null;
		
		if(!_rules.isEmpty() );
		{		
			if(_rules.containsKey(args.length))
			{
				//System.out.println("rule found at index: "+args.length);
				String test_rule = ("/"+label+" "+ImusAPI._metods.CombineArrayToOneString(args, " ")).toLowerCase();
				
				for(Rule rule : _rules.get(args.length))
				{
					//System.out.println("rule: "+rule._cmd_rule);
					if(StringUtils.contains(test_rule, rule._cmd_rule)) return rule._arguments;
				}
			}
			
		}
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

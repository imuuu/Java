package imu.GS.SubCmds;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.ENUMs.Cmd_add_options;
import imu.GS.ENUMs.TagSubCmds;
import imu.GS.Main.Main;
import imu.GS.Other.CmdData;
import imu.iAPI.Interfaces.CommandInterface;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;
import net.md_5.bungee.api.ChatColor;

public class SubTagMaterialCMD implements CommandInterface
{
	Main _main = null;

	CmdData _data;
	public SubTagMaterialCMD(Main main, CmdData data) 
	{
		_main = main;
		_data = data;
		//_chosenValues = new HashMap<>();
		_data.set_syntaxText("/"+_data.get_cmd_name() + "{"+ImusAPI._metods.CombineArrayToOneString(TagSubCmds.values(), "/")+"} {(!setPrice,!increasePrice) hand/hotbar/inv} {tag}");
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
    	Player player = (Player) sender;
    	ImusAPI._metods.printArray("tag", args);
    	if(args.length < 3)
    	{  		
    		if(!(args.length > 1 && args[1].equalsIgnoreCase("remove_all_tags")))
    		{
    			player.sendMessage(_data.get_syntaxText());
        		return false;
    		}
    		
    	}  
    	
    	Cmd_add_options option = null;
    	TagSubCmds subCMD = null;
    	try 
    	{
			option = Cmd_add_options.valueOf(args[2].toLowerCase());
			
		} 
    	catch (Exception e){}
    	
    	ItemStack[] stacks = null;
    	if(option != null)
    	{
    		switch (option) 
        	{
    		case hand:
    			stacks = Hand(player);
    			break;
    		case hotbar:
    			stacks = Hotbar(player);
    			break;
    		case inventory:
    			stacks = Inv(player);
    			break;		
    		}
        	   	
    	}
    	
    	double number = -1;
    	try 
    	{
    		subCMD = TagSubCmds.valueOf(args[1].toLowerCase());
    		if(ImusAPI._metods.isDigit(args[3]))
    		{
    			System.out.println("it is digit");
    			number = Double.valueOf(args[3]);
    		}else
    		{
    			player.sendMessage(ChatColor.RED + "Last number should be digit");
    			return false;
    		}
    		
		} 
    	catch (Exception e) {}
    	
    	switch (subCMD) 
    	{
		case add:
			AddTags(player, stacks, args[3]);
			return false;		
		case remove:
			RemoveTag(player, stacks, args[3]);
    		return false;
		case remove_all_tags:
			RemoveAll(player, stacks);
    		return false;
		case set_price:
			SetPriceAsync(player,number, args[2]);
			break;
		case increase_price:
			SetInceasePriceAsync(player, number, args[2]);
			break;
		
		}
    	

        return false;
    }
    
    
    void SetPriceAsync(Player player, double price, String tag)
    {
    	if( price < 0) 
    	{
    		player.sendMessage(Metods.msgC("&c Couldnt set price with &2"+price));
    		return;
    	}
    	new BukkitRunnable() 
    	{
			@Override
			public void run() 
			{
				for(Material mat : _main.GetTagManager().GetAllMaterialsWithTag(tag))
				{
					//double price = _main.get_shopManager().GetPriceMaterial(mat).GetPrice();
					_main.get_shopManager().SaveMaterialPrice(mat, price);
				}
				
				player.sendMessage(Metods.msgC("&2All with tag '&b"+tag.toLowerCase()+"&2' the price is set to: &a"+price));
				
				
			}
		}.runTaskAsynchronously(_main);
    }
    
    void SetInceasePriceAsync(Player player,double multiplier, String tag)
    {
    	if( multiplier < 0) 
    	{
    		player.sendMessage(Metods.msgC("&c Couldnt increase with negative multiplier => &2"+multiplier));
    		return;
    	}
    	new BukkitRunnable() 
    	{
			@Override
			public void run() 
			{
				for(Material mat : _main.GetTagManager().GetAllMaterialsWithTag(tag))
				{
					double price = _main.get_shopManager().GetPriceMaterial(mat).GetPrice();
					_main.get_shopManager().SaveMaterialPrice(mat, price * multiplier);
				}
				
				player.sendMessage(Metods.msgC("&2All with tag '&b"+tag.toLowerCase()+"&2' the price is increased by: &b"+multiplier));
				
				
			}
		}.runTaskAsynchronously(_main);
    }
    
    void AddTags(Player player, ItemStack[] stacks, String tag)
    {
    	for(ItemStack stack : stacks)
    	{
    		if(stack == null) continue;
    		_main.GetTagManager().SaveTagAsync(stack.getType(), tag);
    		player.sendMessage(Metods.msgC("&2Tag named &3"+tag.toLowerCase()+" &2has been &eadded &2to material &b"+stack.getType()));
    	}
    	
    }
    
    void RemoveTag(Player player,ItemStack[] stacks, String tag)
    {
    	for(ItemStack stack : stacks)
    	{
    		if(stack == null) continue;
    		_main.GetTagManager().RemoveTagAsync(stack.getType(), tag);
    		player.sendMessage(Metods.msgC("&2Tag named &3"+tag.toLowerCase()+" &2has been &cremoved &2to material &b"+stack.getType()));
    	}
    }
    
    void RemoveAll(Player player,ItemStack[] stacks)
    {
    	for(ItemStack stack : stacks)
    	{
    		if(stack == null) continue;
    		_main.GetTagManager().RemoveAllAsync(stack.getType());
    		player.sendMessage(Metods.msgC("&2All tags from material &b"+stack.getType()+" &2has been &cremoved"));
    	}
    }
    
    ItemStack[] Hand(Player player){return new ItemStack[] {player.getInventory().getItemInMainHand()};}

    	
	
    ItemStack[] Hotbar(Player player)
	{
		ItemStack[] stacks = new ItemStack[9];
		for(int i = 0; i < stacks.length; i++) {stacks[i] = player.getInventory().getItem(i);}
		return stacks;
	}
	
    ItemStack[] Inv(Player player)
	{
    	ItemStack[] stacks = new ItemStack[36];
		for(int i = 0; i < stacks.length; i++) {stacks[i] = player.getInventory().getItem(i);}
		return stacks;
	}
	
	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		
	}
    
   
   
}
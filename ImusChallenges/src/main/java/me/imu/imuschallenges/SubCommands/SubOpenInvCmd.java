package me.imu.imuschallenges.SubCommands;


import imu.iAPI.CmdUtil.CmdData;
import imu.iAPI.Interfaces.CommandInterface;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ImusUtilities;
import me.imu.imuschallenges.Inventories.TestInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SubOpenInvCmd implements CommandInterface
{
	CmdData _data;

	public SubOpenInvCmd(CmdData data)
	{
		_data = data;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
    	//CardManager.Instance.OpenCreateCardInv((Player)sender);

    	//ManagerEnchants.Instance.OpenEnchantingInventory((Player)sender);
    	
    	//new TestINv().Open((Player)sender);
    	
    	Player player = ((Player)sender);
    	
    	//ImusUtilities.SetFakeBlock(player, Material.BEDROCK, ((Player)sender).getLocation().add(0, 3, 0));
		sender.sendMessage("Opening INV");
		//new TestInventory().open(player);

		ImusUtilities.SendCenteredMessage(player, "======== HERE IS NICE DAY ========");

		Location location = player.getLocation();

		HashMap<Location, Material> waterSources = new HashMap<>();
		waterSources = findWaterSources(location, waterSources, 200);

		Metods._ins.printHashMap(waterSources);

		for(Location loc : waterSources.keySet())
		{
			Block block = loc.getBlock();
			if(isSourceBlock(block))
			{
				//ImusUtilities.SetFakeBlock(player, Material.BEDROCK, loc);
				System.out.println("FOUND SOURCE BLOCK");
				block.setType(Material.BEDROCK);
			}
		}
		
        return false;
    }
    
   
	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		
	}

	private HashMap<Location, Material> findWaterSources(Location location, HashMap<Location, Material> waterSources, int depth)
	{
		depth--;

		if(depth <= 0)
			return waterSources;

		Block block = location.getBlock();

		// Check if the block is a water block
		if (block.getType() != Material.WATER) {
			return waterSources;
		}

		// Check if this location is already processed
		if (waterSources.containsKey(location)) {
			return waterSources;
		}

		waterSources.put(location, Material.WATER);

		// Recursively check adjacent blocks
		findWaterSources(location.clone().add(1, 0, 0), waterSources, depth); // East
		findWaterSources(location.clone().add(-1, 0, 0), waterSources, depth); // West
		findWaterSources(location.clone().add(0, 1, 0), waterSources, depth); // Up
		findWaterSources(location.clone().add(0, -1, 0), waterSources, depth); // Down
		findWaterSources(location.clone().add(0, 0, 1), waterSources, depth); // South
		findWaterSources(location.clone().add(0, 0, -1), waterSources, depth); // North

		return waterSources;
	}

	private boolean isSourceBlock(Block block) {
		BlockData blockData = block.getBlockData();

		if (blockData instanceof Levelled)
		{
			Levelled levelled = (Levelled) blockData;
			return levelled.getLevel() == 0;
		}

		return false;
	}
    
   
   
}
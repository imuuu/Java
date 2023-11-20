package imu.imusEnchants.Enchants;

import org.bukkit.inventory.ItemStack;

import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.main.CONSTANTS;

public class NodeBooster extends Node
{
	public int Power = 1;
	
	public NodeBooster()
	{
		SetLock(false);
	}
	@Override
    public String Serialize() 
 	{
		return this.getClass().getSimpleName() + 
				":" + GetX() + 
				":" + GetY() + 
				":" + Power;
    }

    @Override
    public void Deserialize(String data) 
    {
        String[] parts = data.split(":");
        _x = Integer.parseInt(parts[1]);
        _y = Integer.parseInt(parts[2]);
        Power = Integer.parseInt(parts[3]);
    }
    
    @Override
    public ItemStack GetItemStack()
    {
    	ItemStack itemStack = new ItemStack(CONSTANTS.BOOSTER_MATERIAL);
    	ItemUtils.SetDisplayName(itemStack, "&eBOOSTER");
    	ItemUtils.AddLore(itemStack, "&5Power: &6"+Power, false);
    	
    	return itemStack;
    }
}

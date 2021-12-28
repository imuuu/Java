package imu.iWaystone.Upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.iWaystones.Enums.UpgradeType;

public class UpgradeCastTime extends BaseUpgrade
{

	public UpgradeCastTime() 
	{
		super();
	}

	@Override
	Tier[] SetTiers() 
	{
		Tier[] tiers = new Tier[4];
		ItemStack stack = new ItemStack(Material.LAPIS_BLOCK,5);
		tiers[0] = new Tier(new ItemStack[] {stack});
		tiers[1] = new Tier(new ItemStack[] {stack});
		tiers[2] = new Tier(new ItemStack[] {stack});
		tiers[3] = new Tier(new ItemStack[] {stack});
		return tiers;
	}

	@Override
	public String[] SetDescription() 
	{
		return new String[] {"&eEvery &6&ltier &ereduces cast time &25&es"};
	}

	@Override
	public double SetTierReduceValue() {
		return 5.0;
	}

	@Override
	public String SetDisplayName() {
		return  "&2Reduce Cast time";
	}

	@Override
	public Material SetMaterial() {
		return Material.CLOCK;
	}

	@Override
	public UpgradeType SetType() {
		return UpgradeType.CAST_TIME;
	}	
	

	
}

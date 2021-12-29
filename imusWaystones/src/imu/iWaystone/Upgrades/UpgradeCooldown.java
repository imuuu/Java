package imu.iWaystone.Upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.iWaystones.Enums.UpgradeType;

public class UpgradeCooldown extends BaseUpgrade
{

	@Override
	Tier[] SetTiers() 
	{
		Tier[] tiers = new Tier[10];
		for(int i = 0; i < tiers.length; i++)
		{
			tiers[i] = new Tier(new ItemStack[] {new ItemStack(Material.DIAMOND,5)});
		}
		return tiers;
	}

	@Override
	public String[] SetDescription() 
	{
		return new String[]{"&eEvery &6Tier&e reduce cooldown by &25%"};
	}

	@Override
	public double SetTierReduceValue() 
	{
		return 0.05;
	}

	@Override
	public String SetDisplayName() 
	{
		return "&5Reduce Teleport Cooldown";
	}

	@Override
	public Material SetMaterial() 
	{
		return Material.COMPASS;
	}

	@Override
	public UpgradeType SetType() 
	{
		return UpgradeType.COOLDOWN;
	}

	@Override
	public double GetCombinedValue(double value) 
	{
		return value * (1.0 - _tierReduceValue * GetCurrentTier());
	}

}

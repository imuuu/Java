package imu.iWaystone.Upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.iWaystones.Enums.UpgradeType;

public class UpgradeXPusage extends BaseUpgrade
{
	@Override
	Tier[] SetTiers() 
	{
		Tier[] tiers = new Tier[4];
		tiers[0] = new Tier(new ItemStack[] {new ItemStack(Material.IRON_BLOCK,3)});
		tiers[1] = new Tier(new ItemStack[] {new ItemStack(Material.IRON_BLOCK,1),new ItemStack(Material.GOLD_BLOCK,4)});
		tiers[2] = new Tier(new ItemStack[] {new ItemStack(Material.IRON_BLOCK,1),new ItemStack(Material.GOLD_BLOCK,3),new ItemStack(Material.DIAMOND_BLOCK,3)});
		tiers[3] = new Tier(new ItemStack[] {new ItemStack(Material.NETHERITE_BLOCK,1)});
		return tiers;
	}

	@Override
	public String[] SetDescription() {
		return new String[]{"&eEvery &6&lTier &eReduces xp level use by &20.5 &aL"};
	}

	@Override
	public double SetTierReduceValue() 
	{
		return 0.5;
	}

	@Override
	public String SetDisplayName() 
	{
		return "&6Reduce xp usage";
	}

	@Override
	public Material SetMaterial() 
	{
		return Material.EXPERIENCE_BOTTLE;
	}

	@Override
	public UpgradeType SetType() 
	{
		return UpgradeType.XP_USAGE;
	}

}

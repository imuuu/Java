package imu.iWaystone.Upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.iWaystones.Enums.UpgradeType;

public class UpgradeDimension extends BaseUpgrade
{

	@Override
	Tier[] SetTiers() 
	{
		Tier[] tiers = new Tier[4];
		tiers[0] = new Tier(new ItemStack[] {new ItemStack(Material.ENDER_PEARL,5)});
		tiers[1] = new Tier(new ItemStack[] {new ItemStack(Material.ENDER_PEARL,16)});
		return tiers;
	}

	@Override
	public String[] SetDescription() {

		return new String[]{"&eUprade makes posible to see Waystones from other dimension"," ","&3first Tier: &bNether-dimension","&3Second: &bEnd-dimension"};
	}

	@Override
	public double SetTierReduceValue() 
	{
		return 1;
	}

	@Override
	public String SetDisplayName() {
		return "&3Transfer Upgrade";
	}

	@Override
	public Material SetMaterial() 
	{
		return Material.ENDER_PEARL;
	}

	@Override
	public UpgradeType SetType() 
	{
		return UpgradeType.DIMENSION;
	}
	
}

package imu.iWaystone.Upgrades;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Enums.UpgradeType;

public class UpgradeCooldown extends BaseUpgrade
{
	public  UpgradeCooldown() 
	{
		_refreshDescWithToolTip = true;
	}
	@Override
	Tier[] SetTiers() 
	{
		Tier[] tiers = new Tier[11];
		for(int i = 0; i < tiers.length; i++)
		{
			tiers[i] = new Tier(new ItemStack[] {new ItemStack(Material.DIAMOND,10)});
		}
		return tiers;
	}

	@Override
	public String[] SetDescription() 
	{
		return new String[]{
				"&eEvery &6Tier&e reduce cooldown by &25%",
				"&eCurrent bonus: &9"+(GetCurrentTier()*5)+"&2%"
				
		};
	}

	@Override
	public double SetTierReduceValue() 
	{
		return 0.05;
	}

	@Override
	public String SetDisplayName() 
	{
		return "&5Minor &bTeleport &5Cooldown Reduction";
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

	@Override
	public void ButtonPressUpgradeTier(Player player, Waystone ws, int tierBeforeUpgrade) {

	}

	
}

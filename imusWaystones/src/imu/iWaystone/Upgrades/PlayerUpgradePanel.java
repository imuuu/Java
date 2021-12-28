package imu.iWaystone.Upgrades;

import imu.iWaystones.Enums.UpgradeType;

public class PlayerUpgradePanel 
{
	private UpgradeCastTime _castTime;
	private UpgradeCooldown _cooldown;
	private UpgradeDimension _dimension;
	private UpgradeXPusage _xpUsage;
	
	public PlayerUpgradePanel(UpgradeCastTime castime, UpgradeCooldown cooldown, UpgradeDimension dimension, UpgradeXPusage xpUsage)
	{
		_castTime = castime;
		_cooldown = cooldown;
		_dimension = dimension;
		_xpUsage = xpUsage;
	}

	public UpgradeCooldown get_cooldown() {
		return _cooldown;
	}

	public UpgradeDimension get_dimension() {
		return _dimension;
	}

	public UpgradeCastTime get_castTime() {
		return _castTime;
	}

	public UpgradeXPusage get_xpUsage() {
		return _xpUsage;
	}
	
	public BaseUpgrade[] GetUpgrades()
	{
		return new BaseUpgrade[] {_castTime,_cooldown, _dimension, _xpUsage};	
	}
	
	public void SetUpgrade(BaseUpgrade upgrade)
	{
		if(upgrade instanceof UpgradeCastTime)
		{
			_castTime = (UpgradeCastTime)upgrade;
			return;
		}
		
		if(upgrade instanceof UpgradeCooldown)
		{
			_cooldown = (UpgradeCooldown)upgrade;
			return;
		}
		
		if(upgrade instanceof UpgradeXPusage)
		{
			_xpUsage = (UpgradeXPusage)upgrade;
			return;
		}
		
		if(upgrade instanceof UpgradeDimension)
		{
			_dimension = (UpgradeDimension)upgrade;
			return;
		}
	}
	public BaseUpgrade GetUpgrade(UpgradeType type)
	{
		switch (type) 
		{
		case CAST_TIME:
			return get_castTime();
		case COOLDOWN:
			return get_cooldown();
		case DIMENSION:
			return get_dimension();
		case XP_USAGE:
			return get_xpUsage();

		}
		return null;
	}
	
}

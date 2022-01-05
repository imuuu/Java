package imu.iWaystone.Upgrades;

import java.util.UUID;

import imu.iWaystones.Enums.UpgradeType;
import imu.iWaystones.Main.ImusWaystones;

public class PlayerUpgradePanel 
{
	private UpgradeCastTime _castTime = new UpgradeCastTime();
	private UpgradeCooldown _cooldown = new UpgradeCooldown();
	private UpgradeDimension _dimension = new UpgradeDimension();
	private UpgradeXPusage _xpUsage = new UpgradeXPusage();
	private UpgradeFoundation _foundation = new UpgradeFoundation();
	private UpgradeNameChange _nameChange = new UpgradeNameChange();
	private UUID _uuid_ws;
	
	public PlayerUpgradePanel(UUID uuid_ws)
	{
		_uuid_ws = uuid_ws;

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
	
	public UpgradeFoundation get_foundation() {
		return _foundation;
	}

	public UpgradeNameChange get_nameChange() {
		return _nameChange;
	}
	
	public BaseUpgrade[] GetUpgrades()
	{
		return new BaseUpgrade[] {_castTime,_cooldown, _dimension, _xpUsage, _foundation, _nameChange};	
	}
	
	public void LoadToolTips()
	{
		for(BaseUpgrade upgrade : GetUpgrades()) {upgrade.Tooltip();}
	}
	
	public int GetCooldown()
	{
		return (int)_cooldown.GetCombinedValue(_foundation.GetTier()._value);
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
		
		if(upgrade instanceof UpgradeFoundation)
		{
			_foundation = (UpgradeFoundation)upgrade;
			return;
		}
		
		if(upgrade instanceof UpgradeNameChange)
		{
			_nameChange =(UpgradeNameChange)upgrade;
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
		case BUILD:
			return ImusWaystones._instance.GetWaystoneManager().GetWaystone(_uuid_ws).GetUpgradeBottomUpgrade();
		case FOUNDATION:
			return get_foundation();
		case RENAME:
			return get_nameChange();


			
		}
		return null;
	}

	
	
}

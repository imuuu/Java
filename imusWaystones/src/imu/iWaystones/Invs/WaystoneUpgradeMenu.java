package imu.iWaystones.Invs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Interfaces.IButton;
import imu.iAPI.InvUtil.InventoryReaderStack;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;
import imu.iWaystone.Interfaces.IModDataInv;
import imu.iWaystone.Interfaces.IModDataValues;
import imu.iWaystone.Upgrades.BaseUpgrade;
import imu.iWaystone.Upgrades.PlayerUpgradePanel;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Enums.ConvUpgradeModData;
import imu.iWaystones.Enums.UpgradeType;
import imu.iWaystones.Main.ImusWaystones;
import imu.iWaystones.Managers.WaystoneManager;
import imu.iWaystones.Other.ConvUpgrade;

public class WaystoneUpgradeMenu extends CustomInvLayout implements IModDataInv
{
	private ImusWaystones _main;
	private Waystone _waystone;
	private WaystoneManager _wManager;
	private CustomInvLayout _lastinv;
	
	PlayerUpgradePanel _panel;
	boolean upgrading = false;
	
	public WaystoneUpgradeMenu(Plugin main, Player player, Waystone waystone, CustomInvLayout lastinv) 
	{
		super(main, player, "&6Upgrading: "+waystone.GetName(), 4 * 9);
		_main = ImusWaystones._instance;
		_waystone = waystone;
		_wManager = _main.GetWaystoneManager();
		_lastinv = lastinv;
	}

	enum BUTTON implements IButton
	{
		NONE,
		UPGRADE,
		BACK,
		SET_NAME,
	}
//	protected void Rename()
//	{
//		ProtocolManager 
//	}
	void LoadUpgradePanel()
	{
		_panel = _waystone.GetPlayerUpgradePanel(_player.getUniqueId());
		SetUpgradesAsync();
	}
	
	BUTTON GetButtonPress(InventoryClickEvent e)
	{
		ItemStack stack = e.getCurrentItem();
		if(stack == null)
			return BUTTON.NONE;
		
		String buttonName = getButtonName(e.getCurrentItem());
		if(buttonName == null)
			return BUTTON.NONE;
		
		return BUTTON.valueOf(buttonName);
	}
	
	@Override
	public void invClosed(InventoryCloseEvent e) {
		
	}
	
	@Override
	public void SetModData(IModDataValues modValue, String value) 
	{
		ConvUpgradeModData v = (ConvUpgradeModData)modValue;
		switch (v) 
		{
		case RENAME:
			_wManager.GetWaystone(_waystone.GetUUID()).SetName(value);
			_wManager.GetWaystone(_waystone.GetUUID()).CreateHologram();

			break;
	
		}
	}
	
	@Override
	public void openThis() 
	{
		super.openThis();
		setupButtons();
		
		
	}
	
	@Override
	public void onClickInsideInv(InventoryClickEvent e) {
		BUTTON button = GetButtonPress(e);
		
		switch (button) 
		{
		case BACK:
			_player.closeInventory();
			_lastinv.openThis();
			break;
		case NONE:
			break;
		case UPGRADE:
			if(upgrading) return;
			UpgradeAsync(_panel.GetUpgrade(UpgradeType.valueOf(Metods._ins.getPersistenData(e.getCurrentItem(),"upgrade" , PersistentDataType.STRING))));			
			break;
		case SET_NAME:
			_player.closeInventory();
			Metods._ins.ConversationWithPlayer(_player, new ConvUpgrade(ConvUpgradeModData.RENAME, this, "&3Give waystone &2new &3name?"));
			break;

		}
	}

	@Override
	public void setupButtons() {
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				
				for(int i = 0; i < _size; i++) {setupButton(BUTTON.NONE, Material.BLACK_STAINED_GLASS_PANE, " ", i);}

				setupButton(BUTTON.BACK, Material.RED_WOOL, "&bBACK", _size-9);
				setupButton(BUTTON.SET_NAME, Material.NAME_TAG, "&2Rename Waystone", _size-5);
				//setupButton(BUTTON.UPGRADE_CASTTIME, Material.LAPIS_BLOCK, "&bCast Time", 1);
				LoadUpgradePanel();
				SetUpgradesAsync();
			}
		}.runTaskAsynchronously(_main);
		
		
	}
	
	void SetUpgradesAsync()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				ItemStack stack = SetButton(_panel.get_castTime()._displayItem.clone(), BUTTON.UPGRADE);
				Metods._ins.setPersistenData(stack, "upgrade", PersistentDataType.STRING, UpgradeType.CAST_TIME.toString());
				_inv.setItem(1, stack);
				
				stack = SetButton(_panel.get_xpUsage()._displayItem.clone(), BUTTON.UPGRADE);
				Metods._ins.setPersistenData(stack, "upgrade", PersistentDataType.STRING, UpgradeType.XP_USAGE.toString());
				_inv.setItem(3,stack);
				
				stack = SetButton(_panel.get_dimension()._displayItem.clone(), BUTTON.UPGRADE);
				Metods._ins.setPersistenData(stack, "upgrade", PersistentDataType.STRING, UpgradeType.DIMENSION.toString());
				_inv.setItem(5,stack);
				
				stack = _panel.get_cooldown()._displayItem.clone();
				Metods._ins.setPersistenData(stack, "upgrade", PersistentDataType.STRING, UpgradeType.COOLDOWN.toString());
				Metods._ins.addLore(stack, "&2BASE: 60min", false);
				_inv.setItem(7,SetButton(stack, BUTTON.UPGRADE));

			}
		}.runTaskAsynchronously(_main);
		
	}
	
	void UpgradeAsync(BaseUpgrade upgrade)
	{

		if(upgrading || upgrade.IsMaxTier()) return;
		
		upgrading = true;
		new BukkitRunnable() 
		{		
			
			boolean CheckItems()
			{
				InventoryReaderStack invReader = new InventoryReaderStack(_player.getInventory());
				
				for(ItemStack stack : upgrade.GetCost())
				{
					if(!invReader.HasEnough(stack, stack.getAmount()))
					{
						return false;
					}
				}
				
				for(ItemStack stack : upgrade.GetCost())
				{
					invReader.Reduce(stack, stack.getAmount());
				}
				
				return true;
			}
			@Override
			public void run() 
			{
				
				if(CheckItems())
				{
					upgrade.IncreaseCurrentTier(1);
					_wManager.GetWaystoneManagerSQL().SaveUpgradeAsync(_player.getUniqueId(), _waystone.GetUUID(), upgrade);
					SetUpgradesAsync();					
				}

				upgrading = false;
			}
		}.runTaskAsynchronously(_main);
	}

	

}

package imu.iWaystones.Invs;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;


import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Main.ImusWaystones;
import imu.iWaystones.Managers.WaystoneManager;

public class WaystoneMenuInv extends CustomInvLayout {

	private ImusWaystones _main;
	private Waystone _waystone;
	private WaystoneManager _wManager;
	private int _page = 0;
	private ArrayList<Waystone> _discoveredWaystones = new ArrayList<>();
	
	public WaystoneMenuInv(Waystone waystone, Player player) 
	{
		super(ImusWaystones._instance, player, waystone.GetName(), 4 * 9);
		_main = ImusWaystones._instance;
		_waystone = waystone;
		_wManager = _main.GetWaystoneManager();
		
		for(UUID uuid_ws : _wManager.GetDiscovered().get(_player.getUniqueId()))
		{
			Waystone ws = _wManager.GetWaystone(uuid_ws);
			if(!IsAbleToShow(ws)) continue;
			_discoveredWaystones.add(ws);
		}
	}
	
	enum BUTTON implements IButton
	{
		NONE,
		GO_LEFT,
		GO_RIGHT,
		UPGRADE,
		WAYSTONE,
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
	public void invClosed(InventoryCloseEvent e) 
	{
		
	}

	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		BUTTON button = GetButtonPress(e);
		
		switch (button) 
		{
		case NONE:
			break;
		case GO_LEFT:
			_page = PageChance(_page, -1, _discoveredWaystones.size(), _size-9);
			System.out.println("page: "+_page);
			break;
		case GO_RIGHT:
			_page = PageChance(_page, 1, _discoveredWaystones.size(), _size-9);
			System.out.println("page: "+_page);
			break;		
		case UPGRADE:
			_player.closeInventory();
			new WaystoneUpgradeMenu(_main, _player, _waystone, this).openThis();
			break;
		case WAYSTONE:
			StartTeleport();
			_player.closeInventory();
			break;
		
		}
	}
	
	@Override
	public void openThis() {
		super.openThis();
		setupButtons();
	}
	
	@Override
	public void setupButtons() 
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				for(int i = _size-9; i < _size; i++) {setupButton(BUTTON.NONE, Material.CYAN_STAINED_GLASS_PANE, " ", i);}
				setupButton(BUTTON.GO_LEFT, Material.BIRCH_SIGN, "&b<<", _size-9);
				setupButton(BUTTON.GO_RIGHT, Material.BIRCH_SIGN, "&b>>", _size-1);
				if(_waystone.GetOwnerUUID().equals(_player.getUniqueId())) setupButton(BUTTON.UPGRADE, Material.BIRCH_BOAT, "&eUPGRADE", _size-5);
				
			}
		}.runTaskAsynchronously(_main);
		
		LoadWaystonesAsync();
	}
	
	Waystone GetWaystoneFromButton(ItemStack stack)
	{
		return _wManager.GetWaystone(UUID.fromString(Metods._ins.getPersistenData(stack, "iwm" ,PersistentDataType.STRING)));
	}
	
	void LoadWaystonesAsync()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(int i = 0; i < _size-9;i++)
				{
					int idx = i + ( _page * (_size-9));
					if(idx > _discoveredWaystones.size()-1) {_inv.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));continue;}
					
					Waystone ws = _discoveredWaystones.get(idx);

					ItemStack stack = ws.GetDisplayItem().clone();
					Metods.setDisplayName(stack, ws.GetName());
					Metods._ins.SetLores(stack, new String[] {"&7(&eClick&7)&2 to start &bTeleporting"}, false);
					Metods._ins.setPersistenData(stack, "iwm", PersistentDataType.STRING, ws.GetUUID().toString());
					SetButton(stack, BUTTON.WAYSTONE);
					_inv.setItem(i, stack);
				}
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	boolean IsAbleToShow(Waystone waystone)
	{
		return true;
	}
	
	void StartTeleport()
	{
		
	}

}

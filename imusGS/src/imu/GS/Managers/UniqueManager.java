package imu.GS.Managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopItems.ShopItemUnique;
import imu.iAPI.Main.ImusAPI;

public final class UniqueManager 
{
	Main _main;
	ShopManager _shopManager;
	ShopManagerSQL _shopManagerSQL;
	HashMap<UUID, ShopItemUnique> _uniques;
	public final String _pd_uniqueUUID = "gs.uniqueUUID";
	
	public UniqueManager(Main main, ShopManager shopManager, ShopManagerSQL shopManagerSQL)
	{
		_main = main;
		_shopManager = shopManager;
		_shopManagerSQL = shopManagerSQL;
		_uniques = new HashMap<>();
	}
	
	public HashMap<UUID, ShopItemUnique> GetUniques()
	{
		return _uniques;
	}
	
	public void AddUniqueItem(ShopItemUnique siu)
	{
		_uniques.put(siu.GetUUID(), siu);
		_shopManagerSQL.SaveUniqueItem(siu);
	}
	
	public void PutPDuuid(ItemStack stack, UUID uuid)
	{
		ImusAPI._metods.setPersistenData(stack, _pd_uniqueUUID, PersistentDataType.STRING, uuid.toString());
	}
	
	public void RemovePDuuid(ItemStack stack)
	{
		ImusAPI._metods.removePersistenData(stack, _pd_uniqueUUID);
	}
	
	public boolean IsUnique(ItemStack stack)
	{
		return ImusAPI._metods.getPersistenData(stack, _pd_uniqueUUID, PersistentDataType.STRING) == null ? false : true;
	}
	
	public boolean IsSavedUnique(ItemStack stack)
	{
		String uuidSTR = ImusAPI._metods.getPersistenData(stack, _pd_uniqueUUID, PersistentDataType.STRING);
		if(uuidSTR == null)
			return false;
		
		UUID uuid = UUID.fromString(uuidSTR);
		return _uniques.containsKey(uuid);
	}
	
}

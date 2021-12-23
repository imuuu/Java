package imu.iWaystone.Waystones;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Waystone 
{
	private String _name;
	private Block _top,_mid,_low;
	private UUID _owner_uuid;
	private String _owner_name;
	private UUID _uuid;
	
	public Waystone(Block top, Block mid, Block low) 
	{
		
		
		_uuid = UUID.randomUUID();
		
		
		_top = top; _mid = mid; _low = low;
	}
	
	
//	void SetUUIDtoBlock(Block block)
//	{
//		ImusAPI._metods.setPersistenData(block, ImusWaystones._instance.GetWaystoneManager().pd_waystoneUUID, PersistentDataType.STRING, _uuid.toString());
//	}
//	
//	public void RegisterWaystoneBlocks()
//	{
//		SetUUIDtoBlock(_top);
//		SetUUIDtoBlock(_mid);
//		SetUUIDtoBlock(_low);
//	}
	public void SetUUID(UUID uuid)
	{
		_uuid = uuid;
	}
	
	public UUID GetUUID()
	{
		return _uuid;
	}
	
	public void SetOwner(Player player)
	{
		_owner_uuid = player.getUniqueId();
		_owner_name = player.getName();
	}
	
	public UUID GetOwnerUUID()
	{
		return _owner_uuid;
	}
	
	public String GetOwnerName()
	{
		return _owner_name;
	}
	
	public Block GetTopBlock()
	{
		return _top;
	}
	
	public Block GetMidBlock()
	{
		return _mid;
	}
	
	public Block GetLowBlock()
	{
		return _low;
	}
}

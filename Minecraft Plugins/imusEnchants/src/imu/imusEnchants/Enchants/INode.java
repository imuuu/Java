package imu.imusEnchants.Enchants;

import org.bukkit.inventory.ItemStack;

public interface INode
{
	public int GetX();
	public int GetY();
	
	public void SetPosition(int x, int y);
	
	public INode[] GetNeighbors();
	
	public void SetNeighbors(INode[] nodes);
	
	public boolean IsLocked();
	
	public void SetLock(boolean lock);
	
	public String Serialize();
	public void Deserialize(String data);
	
	public int GetFlatIndex();
	
	public ItemStack GetItemStack();
}

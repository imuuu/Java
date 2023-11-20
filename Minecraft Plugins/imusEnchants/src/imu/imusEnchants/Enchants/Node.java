package imu.imusEnchants.Enchants;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.imusEnchants.main.CONSTANTS;

public class Node implements INode
{
	protected int _x;
	protected int _y;
	
	// 0 LEFT, 1 RIGHT, 2 UP, 3 DOWN
	private INode[] _neighbors;
	
	private boolean _isLock = true;
	
	public Node() {}
	
	public Node(int x, int y)
	{
		_x = x;
		_y = y;
	}
	
	@Override
	public void SetPosition(int x, int y)
	{
		_x = x;
		_y = y;
	}
	
	public int GetFlatIndex()
	{
		return _x * CONSTANTS.ENCHANT_COLUMNS + _y;
	}

	@Override
	public int GetX()
	{
		return _x;
	}

	@Override
	public int GetY()
	{
		return _y;
	}

	@Override
	public INode[] GetNeighbors()
	{
		return _neighbors;
	}

	@Override
	public void SetNeighbors(INode[] nodes)
	{
		_neighbors = nodes;
	}

	@Override
	public boolean IsLocked()
	{
		return _isLock;
	}

	@Override
	public void SetLock(boolean lock)
	{
		_isLock = lock;
	}

	@Override
    public String Serialize() 
	{
        return this.getClass().getSimpleName() + 
        		":" + GetX() + 
        		":" + GetY();
        		
    }

    @Override
    public void Deserialize(String data) 
    {
        String[] parts = data.split(":");
        _x = Integer.parseInt(parts[1]);
        _y = Integer.parseInt(parts[2]);
       
    }

	@Override
	public ItemStack GetItemStack()
	{
		return new ItemStack(Material.AIR);
	}
	
	@Override
	public String toString()
	{
		
		return Serialize();
	}

	

	
	
	
	
}

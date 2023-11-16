package imu.imusEnchants.Enchants;

import imu.imusEnchants.main.CONSTANTS;

public class Node
{
	public IEnchant _enchant;
	public int slotX = -1;
	public int slotY = -1;
	
	
	// 0 LEFT, 1 RIGHT, 2 UP, 3 DOWN
	public Node[] _nearNodes = new Node[4];
	
	public boolean isLock = true;
	
	public Node() {}
	
	public Node(int x, int y)
	{
		slotX = x;
		slotY = y;
	}
	
	public int GetFlatIndex()
	{
		return slotX * CONSTANTS.ENCHANT_COLUMNS + slotY;
	}
}

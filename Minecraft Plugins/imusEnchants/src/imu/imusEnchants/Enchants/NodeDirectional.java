package imu.imusEnchants.Enchants;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.main.CONSTANTS;

public class NodeDirectional extends Node
{
	private enum DIRECTION { UP, DOWN, LEFT, RIGHT }

    private DIRECTION _direction = DIRECTION.UP;
    private int _steps = 1;
   
    public NodeDirectional() {}
    
    public NodeDirectional(DIRECTION direction, int steps) 
    {
        _direction = direction;
        _steps = steps;
        SetLock(false);
    }
    
    public NodeDirectional(int steps) 
    {
        _steps = steps;
        RandomizeDirection();
        SetLock(false);
    }

    @Override
	public boolean IsValidGUIitem(EnchantedItem enchantedItem, ItemStack stack)
	{
    	if(enchantedItem == null) return false;
    	
    	Material toolMateril = ItemUtils.GetToolMainMaterial(enchantedItem.GetItemStack());
    	System.out.println("TOOL MAT: "+toolMateril + " stack: "+stack.getType() + " ei: "+enchantedItem.GetItemStack().getType());
    	if(toolMateril.isAir()) return false;
    	
    	if(stack.getType() != toolMateril) return false;
    	
    	return true;
    	

	}
    
    @Override
	public ItemStack GetGUIitemLoad(EnchantedItem enchantedItem)
	{
		return new ItemStack(ItemUtils.GetToolMainMaterial(enchantedItem.GetItemStack()));
	}
    
    public void RandomizeDirection() 
    {
    	DIRECTION[] directions = DIRECTION.values();
        Random random = new Random();
        _direction = directions[random.nextInt(directions.length)];
    }
    
    @Override
    public void Activate(EnchantedItem enchantedItem) 
    {
    	System.out.println("NODE");
        INode[][] nodes = enchantedItem.Get_nodes();
        int currentX = GetX();
        int currentY = GetY();

        for (int i = 0; i < _steps; i++) 
        {
            switch (_direction) 
            {
                case UP:    currentX--; break;
                case DOWN:  currentX++; break;
                case LEFT:  currentY--; break;
                case RIGHT: currentY++; break;
            }

            // Check boundaries
            if (currentX < 0 || currentY < 0 || 
                currentX >= CONSTANTS.ENCHANT_ROWS || 
                currentY >= CONSTANTS.ENCHANT_COLUMNS) {
                break; // Stop if out of bounds
            }

            // Open the node at the new position
            INode node = nodes[currentX][currentY];
            if (node != null) {
                node.SetLock(false);
            }
        }

        // Optionally, replace the original node with a new locked node
        nodes[GetX()][GetY()] = new Node(GetX(), GetY()); // Assuming Node is another type of INode
        nodes[GetX()][GetY()].SetLock(true);
    }
    
    
    
    @Override
    public String Serialize() 
    {
    	
        return this.getClass().getSimpleName() +
                ":" + GetX() +
                ":" + GetY() +
                ":" + _direction.name() + 
                ":" + _steps;
    }
    
    @Override
    public void Deserialize(String data) 
    {
        String[] parts = data.split(":");
        _x = Integer.parseInt(parts[1]);
        _y = Integer.parseInt(parts[2]);
        _direction = DIRECTION.valueOf(parts[3]); 
        _steps = Integer.parseInt(parts[4]);
    }



}

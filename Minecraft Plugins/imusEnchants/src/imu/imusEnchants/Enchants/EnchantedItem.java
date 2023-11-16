package imu.imusEnchants.Enchants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.Managers.ManagerEnchants;
import imu.imusEnchants.main.CONSTANTS;

public class EnchantedItem
{
	private ManagerEnchants _managerEnchants = ManagerEnchants.Instance;
	private Node[][] _nodes;
	private int _slots = 0;
	private boolean _isReveaveled = false;
	private ItemStack _stack;
	
	private Random random = new Random();
	
	private final String PD_REVEALED = "pd_revealed";
	private final String PD_SLOTS = "pd_slots";
	
	public EnchantedItem(ItemStack stack)
	{		
		Read(stack);
	}
	
	private void Read(ItemStack stack) 
    {
		_stack = stack;
        _isReveaveled = IsRevealed(stack);

        int slots =  GetSlots(_stack);
        if(slots > 0) _slots = slots;
        else SetSlots(ManagerEnchants.GetMaterialSlotsRange(_stack).GetRandomSlots());
        
    }
	
	private int _totalUnlocked = 0;
	
	public void GenerateNodes()
	{
		System.out.println("||||||||||| GENEslots now: "+_slots);
		_nodes = new Node[CONSTANTS.ENCHANT_ROWS][CONSTANTS.ENCHANT_COLUMNS];
		_totalUnlocked = 0;
	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
	    {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            _nodes[i][j] = new Node(i,j);
	        }
	    }
	    
	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
	    {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            Node node = _nodes[i][j];
	            node._nearNodes[0] = (j > 0) ? _nodes[i][j - 1] : null; // LEFT
	            node._nearNodes[1] = (j < CONSTANTS.ENCHANT_COLUMNS - 1) ? _nodes[i][j + 1] : null; // RIGHT
	            node._nearNodes[2] = (i > 0) ? _nodes[i - 1][j] : null; // UP
	            node._nearNodes[3] = (i < CONSTANTS.ENCHANT_ROWS - 1) ? _nodes[i + 1][j] : null; // DOWN
	        }
	    }

//	    int unlocked = 0;
//	    while (unlocked < _slots) 
//	    {
//	        int row = random.nextInt(CONSTANTS.ENCHANT_ROWS);
//	        int column = random.nextInt(CONSTANTS.ENCHANT_COLUMNS);
//	        
//	        if (_nodes[row][column].isLock && !ManagerEnchants.REDSTRICTED_SLOTS.contains(row * CONSTANTS.ENCHANT_COLUMNS + column)) 
//	        {
//	            _nodes[row][column].isLock = false;
//	            unlocked++;   
//	        }     
//	    }

//	    while (unlocked < _slots) {
//	        int row = random.nextInt(CONSTANTS.ENCHANT_ROWS);
//	        int column = random.nextInt(CONSTANTS.ENCHANT_COLUMNS);
//	        
//	        // Check and unlock the current node
//	        if (_nodes[row][column].isLock && !ManagerEnchants.REDSTRICTED_SLOTS.contains(row * CONSTANTS.ENCHANT_COLUMNS + column)) {
//	            _nodes[row][column].isLock = false;
//	            unlocked++;  
//
//	            // 20% chance to unlock a neighbor
//	            if (random.nextDouble() < 0.50) {
//	                List<Node> neighbors = new ArrayList<>(Arrays.asList(_nodes[row][column]._nearNodes));
//	                neighbors.removeIf(Objects::isNull); // Remove null entries (no neighbor)
//	                neighbors.removeIf(node -> !node.isLock); // Remove already unlocked neighbors
//	                neighbors.removeIf(node -> ManagerEnchants.REDSTRICTED_SLOTS.contains(node.slotX * CONSTANTS.ENCHANT_COLUMNS + node.slotY)); // Remove restricted slots
//
//	                if (!neighbors.isEmpty()) {
//	                    Node randomNeighbor = neighbors.get(random.nextInt(neighbors.size()));
//	                    randomNeighbor.isLock = false;
//	                    unlocked++;
//	                }
//	            }
//	        }
//	        
//	        // Ensure that we don't exceed the slot limit
//	        if (unlocked > _slots) {
//	            unlocked = _slots;
//	        }
//	    }
	    
	    while (_totalUnlocked < _slots) 
	    {
	        int row = random.nextInt(CONSTANTS.ENCHANT_ROWS);
	        int column = random.nextInt(CONSTANTS.ENCHANT_COLUMNS);

	        if (_nodes[row][column].isLock && !ManagerEnchants.REDSTRICTED_SLOTS.contains(row * CONSTANTS.ENCHANT_COLUMNS + column)) 
	        {
	            _nodes[row][column].isLock = false;
	            _totalUnlocked++;

	            TryUnlockNeighbors(_nodes[row][column], 0.20,1);
	        }
	    } 

	}

	private void TryUnlockNeighbors(Node node, double chance, int depth) 
	{
	    if (depth > 5 || _totalUnlocked >= _slots) 
	    {
	        return;
	    }

	    for (Node neighbor : node._nearNodes) 
	    {
	    	if (_totalUnlocked >= _slots) continue;
	    	 
	    	if(neighbor == null) continue;
	    	
	    	if(!neighbor.isLock) continue;
	    	
	    	if(ManagerEnchants.REDSTRICTED_SLOTS.contains(neighbor.slotX * CONSTANTS.ENCHANT_COLUMNS + neighbor.slotY)) continue;
	    	
	    	if (random.nextDouble() > chance) continue;
	        
	    	neighbor.isLock = false;
            _totalUnlocked++;
            
            if (_totalUnlocked < _slots) 
            {
                TryUnlockNeighbors(neighbor, chance * 0.8, depth + 1); // Reduce chance by 20% in each recursive call
            }
	        
	    }
	}
	
	public Node GetNodeBySlot(int slot) 
	{
	    int row = slot / CONSTANTS.ENCHANT_COLUMNS;
	    int column = slot % CONSTANTS.ENCHANT_COLUMNS;

	    if (row >= 0 && row < CONSTANTS.ENCHANT_ROWS && column >= 0 && column < CONSTANTS.ENCHANT_COLUMNS) 
	    {
	        return _nodes[row][column];
	    } 
	    
	    return null;
	}
	
	public Node[] GetUnlockedNodes() 
	{
	    Node[] unlockedNodes = new Node[_slots];
	    int index = 0;

	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
	    {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            if (_nodes[i][j].isLock) continue;
	            
	            unlockedNodes[index++] = _nodes[i][j];
                
//	            if (index >= _slots) 
//                {
//                    return unlockedNodes;
//                }
	        }
	    }

	    return unlockedNodes;
	}
	
	public ItemStack SetTooltip()
	{
		ItemUtils.AddLore(_stack, "&6Slots: &a"+_slots, false);
		
		return _stack;
	}
	
    public void SetSlots(int slots) 
    {
        this._slots = slots;
        ItemUtils.SetPersistenData(_stack, PD_SLOTS, PersistentDataType.INTEGER, slots);
    }
    
    private int GetSlots(ItemStack stack) 
    {
        Integer slots = ItemUtils.GetPersistenData(stack, PD_SLOTS, PersistentDataType.INTEGER);
        return (slots != null) ? slots : 0; 
    }
	
	public boolean IsRevealed()
	{
		return _isReveaveled;
	}
	
	private void SetRevealed(boolean revealed)
	{
		_isReveaveled = revealed;
		ItemUtils.SetPersistenData(_stack, PD_REVEALED, PersistentDataType.INTEGER, revealed ? 1 : 0);
	}
	
	private boolean IsRevealed(ItemStack stack)
	{
		Integer i = ItemUtils.GetPersistenData(stack, PD_REVEALED, PersistentDataType.INTEGER);
		return  i != null && i != 0;
	}
	
	public Node[][] Get_nodes()
	{
		if(_nodes == null) _nodes = new Node[CONSTANTS.ENCHANT_ROWS][CONSTANTS.ENCHANT_COLUMNS];
		return _nodes;
	}

	public void Set_nodes(Node[][] nodes)
	{
		this._nodes = nodes;
	}

	public int Get_slots()
	{
		return _slots;
	}

	public void Set_slots(int _slots)
	{
		this._slots = _slots;
	}

	
}

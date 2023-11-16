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
	private INode[][] _nodes;
	private int _slots = 0;
	private boolean _isReveaveled = false;
	private ItemStack _stack;
	
	private Random random = new Random();
	
	private final String PD_REVEALED = "pd_revealed";
	private final String PD_SLOTS = "pd_slots";
	private int _totalUnlocked = 0;
	
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
	
	public void Reveal(boolean force)
	{
		if(force || !LoadUnlockedNodes(_stack))
	    {
	    	GenerateNodes();
	    	SaveUnlockedNodes(_stack);
	    }
		
		_isReveaveled = true;
	}
	
	public ItemStack GetItemStack() 
	{
		return _stack;
	}
	
	public void GenerateNodes()
	{
		_totalUnlocked = 0;
		System.out.println("||||||||||| GENEslots now: "+_slots);
		
		GenenrateEmptyNodeArray();
		
	    while (_totalUnlocked < _slots) 
	    {
	        int row = random.nextInt(CONSTANTS.ENCHANT_ROWS);
	        int column = random.nextInt(CONSTANTS.ENCHANT_COLUMNS);

	        if (_nodes[row][column].IsLocked() && !ManagerEnchants.REDSTRICTED_SLOTS.contains(row * CONSTANTS.ENCHANT_COLUMNS + column)) 
	        {
	            _nodes[row][column].SetLock(false);
	            _totalUnlocked++;

	            TryUnlockNeighbors(_nodes[row][column], 0.20,1);
	        }
	    } 

	}
	
	private void GenenrateEmptyNodeArray()
	{
		_nodes = new Node[CONSTANTS.ENCHANT_ROWS][CONSTANTS.ENCHANT_COLUMNS];
		
	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
	    {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            Node node = new Node(i,j);
	            node.GetNeighbors()[0] = (j > 0) ? _nodes[i][j - 1] : null; // LEFT
	            node.GetNeighbors()[1] = (j < CONSTANTS.ENCHANT_COLUMNS - 1) ? _nodes[i][j + 1] : null; // RIGHT
	            node.GetNeighbors()[2] = (i > 0) ? _nodes[i - 1][j] : null; // UP
	            node.GetNeighbors()[3] = (i < CONSTANTS.ENCHANT_ROWS - 1) ? _nodes[i + 1][j] : null; // DOWN
	            _nodes[i][j] = node;
	        }
	    }
	    
	}
	private void TryUnlockNeighbors(INode node, double chance, int depth) 
	{
	    if (depth > 5 || _totalUnlocked >= _slots) 
	    {
	        return;
	    }

	    for (INode neighbor : node.GetNeighbors()) 
	    {
	    	if (_totalUnlocked >= _slots) continue;
	    	 
	    	if(neighbor == null) continue;
	    	
	    	if(!neighbor.IsLocked()) continue;
	    	
	    	if(ManagerEnchants.REDSTRICTED_SLOTS.contains(neighbor.GetX() * CONSTANTS.ENCHANT_COLUMNS + neighbor.GetY())) continue;
	    	
	    	if (random.nextDouble() > chance) continue;
	        
	    	neighbor.SetLock(false);
            _totalUnlocked++;
            
            if (_totalUnlocked < _slots) 
            {
                TryUnlockNeighbors(neighbor, chance * 0.8, depth + 1); // Reduce chance by 20% in each recursive call
            }
	        
	    }
	}
	
	public INode GetNodeBySlot(int slot) 
	{
	    int row = slot / CONSTANTS.ENCHANT_COLUMNS;
	    int column = slot % CONSTANTS.ENCHANT_COLUMNS;

	    if (row >= 0 && row < CONSTANTS.ENCHANT_ROWS && column >= 0 && column < CONSTANTS.ENCHANT_COLUMNS) 
	    {
	        return _nodes[row][column];
	    } 
	    
	    return null;
	}
	
	public void SetNode(INode node, int position) 
	{
	    int row = position / CONSTANTS.ENCHANT_COLUMNS;
	    int column = position % CONSTANTS.ENCHANT_COLUMNS;

	    if (row >= 0 && row < CONSTANTS.ENCHANT_ROWS && column >= 0 && column < CONSTANTS.ENCHANT_COLUMNS) 
	    {
	    	System.out.println("Position " + position + " to row: "+row + " col: "+column);
	    	node.SetPosition(row, column);
	        _nodes[row][column] = node;
	    } 
	    else 
	    {
	        System.out.println("Position " + position + " is out of the valid range for nodes.");
	    }
	}

	
	public INode[] GetUnlockedNodes() 
	{
	    INode[] unlockedNodes = new INode[_slots];
	    int index = 0;

	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
	    {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            if (_nodes[i][j].IsLocked()) continue;
	            
	            unlockedNodes[index++] = _nodes[i][j];

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
		String data = ItemUtils.GetPersistenData(stack, "unlocked_nodes", PersistentDataType.STRING);
		return  data != null;
	}
	
	public INode[][] Get_nodes()
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
	
	public void SaveUnlockedNodes() 
	{
		SaveUnlockedNodes(_stack);
	}
	
	public void SaveUnlockedNodes(ItemStack stack) 
	{
	    StringBuilder serializedData = new StringBuilder();

	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
	    {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            INode node = _nodes[i][j];
	            if (!node.IsLocked()) 
	            {
	                if (serializedData.length() > 0) serializedData.append(";");
	                
	                serializedData.append(node.Serialize());
	            }
	        }
	    }

	    ItemUtils.SetPersistenData(stack, "unlocked_nodes", PersistentDataType.STRING, serializedData.toString());
	    
	    System.out.println("item SAVED: "+stack);
	}
	
	public boolean LoadUnlockedNodes() 
	{
		return LoadUnlockedNodes(_stack);
	}
	public boolean LoadUnlockedNodes(ItemStack stack) 
	{
	    String serializedData = ItemUtils.GetPersistenData(stack, "unlocked_nodes", PersistentDataType.STRING);
	    
	    if (serializedData == null || serializedData.isEmpty()) return false;
	    
	    GenenrateEmptyNodeArray();
	    
	    String[] nodeDataArray = serializedData.split(";");
	    for (String nodeData : nodeDataArray) 
	    {
	        String[] parts = nodeData.split(":");
	        String type = parts[0];
	        INode node = null;

	        switch (type) 
	        {
	            case "NodeBooster":
	                node = new NodeBooster();
	                break;
	            case "NodeEnchant":
	                node = new NodeEnchant();
	                break;
	            default:
	                node = new Node();
	                break;
	        }

	        if (node != null) 
	        {
	            node.Deserialize(nodeData);
	            node.SetLock(false);
	            _nodes[node.GetX()][node.GetY()] = node;
	        }
	    }
	    
	    return true;
	}
	
//	public boolean LoadUnlockedNodes(ItemStack stack) 
//	{
//	    String serializedData = ItemUtils.GetPersistenData(stack, "unlocked_nodes", PersistentDataType.STRING);
//	    
//	    if (serializedData == null || serializedData.isEmpty()) return false;
//
//	    String[] nodeDataArray = serializedData.split(";");
//
//	    try 
//        {
//	    	for (String nodeData : nodeDataArray) 
//	 	    {
//	 	        String[] parts = nodeData.split(":");
//	 	        String className = parts[0];
//
//	 	        Class<?> clazz = Class.forName("imu.imusEnchants.Enchants." + className);
//	            if (INode.class.isAssignableFrom(clazz)) 
//	            {
//	                INode node = (INode) clazz.getDeclaredConstructor().newInstance();
//	                node.Deserialize(nodeData);
//	                _nodes[node.GetX()][node.GetY()] = node;
//	            }
//	 	    }
//        }
//	    catch (Exception e) 
//        {
//            e.printStackTrace(); // Handle exceptions appropriately
//            return false;
//        }
//	   
//	    
//	    return true;
//	}
	
	//WORKING
//	public void SaveUnlockedNodes(ItemStack stack) 
//	{
//	    Node[] unlockedNodes = GetUnlockedNodes();
//	    StringBuilder serializedData = new StringBuilder();
//
//	    for (Node node : unlockedNodes) 
//	    {
//	        if (serializedData.length() > 0)
//	            serializedData.append(","); // delimiter
//	        serializedData.append(node.GetFlatIndex());
//	    }
//
//	    ItemUtils.SetPersistenData(stack, "unlocked_nodes", PersistentDataType.STRING, serializedData.toString());
//	}
//	
//	public boolean LoadUnlockedNodes(ItemStack stack) 
//	{
//		
//	    String serializedData = ItemUtils.GetPersistenData(stack, "unlocked_nodes", PersistentDataType.STRING);
//	    if (serializedData == null || serializedData.isEmpty()) return false;
//	    
//	    GenenrateEmptyNodeArray();
//	    
//	    String[] nodeIndices = serializedData.split(",");
//	    for (String indexStr : nodeIndices) {
//	        int index = Integer.parseInt(indexStr);
//	        int row = index / CONSTANTS.ENCHANT_COLUMNS;
//	        int column = index % CONSTANTS.ENCHANT_COLUMNS;
//
//	        if (row >= 0 && row < CONSTANTS.ENCHANT_ROWS && column >= 0 && column < CONSTANTS.ENCHANT_COLUMNS) {
//	            _nodes[row][column].SetLock(false);
//	        }
//	    }
//	    
//	    return true;
//	}

	
}

package imu.imusEnchants.Enchants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.iAPI.Other.Metods;
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
	private final static String PD_SLOTS = "pd_slots";
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
			System.out.println(" STACK IS NOT LOADED, GENERATING NODES, force: "+force);
	    	GenerateNodes();
	    	SaveUnlockedNodes(_stack);

	    }

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
	            _nodes[i][j] = node;
	        }
	    }
	    
	}
	
	public INode[] GetNeighbors(INode node) 
	{
		return GetNeighbors(node.GetX(),node.GetY());
	}
	
	public INode[] GetNeighbors(int x, int y) 
	{
	    INode[] neighbors = new INode[4]; 
	    neighbors[0] = (y > 0) ? _nodes[x][y - 1] : null;
	    
	    neighbors[1] = (y < CONSTANTS.ENCHANT_COLUMNS - 1) ? _nodes[x][y + 1] : null;

	    neighbors[2] = (x > 0) ? _nodes[x - 1][y] : null;

	    neighbors[3] = (x < CONSTANTS.ENCHANT_ROWS - 1) ? _nodes[x + 1][y] : null;

	    return neighbors;
	}
	
	public void PrintNodes() {
	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) {
	            INode node = _nodes[i][j];
	            if (node != null) 
	            {
	            	if(node.IsLocked()) continue;

	                System.out.println("Node at [" + i + "," + j + "]: " +
	                                   (node.IsLocked() ? "Locked" : "Unlocked") +
	                                   ", Details: " + node.toString());

	            } else {
	                System.out.println("Node at [" + i + "," + j + "]: null");
	            }
	        }
	    }
	}
	private void TryUnlockNeighbors(INode node, double chance, int depth) 
	{
	    if (depth > 5 || _totalUnlocked >= _slots) 
	    {
	        return;
	    }

	    for (INode neighbor : GetNeighbors(node)) 
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

	    	node.SetPosition(row, column);
	        _nodes[row][column] = node;
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
	
//	@SuppressWarnings("unused")
//	public void ApplyEnchantsToItem() 
//	{
//	    Map<Enchantment, Integer> allEnchants = new HashMap<>();
//
//	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
//	    {
//	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
//	        {
//	            INode node = _nodes[i][j];
//	            if (node instanceof NodeEnchant) 
//	            {
//	                NodeEnchant nodeEnchant = (NodeEnchant) node;
//	                nodeEnchant.GetEnchantments().forEach((enchant, level) -> 
//	                {
//	                    if (CONSTANTS.ENABLE_MULTIPLE_SAME_ENCHANTS && allEnchants.containsKey(enchant)) 
//	                    {
//	                        allEnchants.put(enchant, allEnchants.get(enchant) + level);
//	                    } 
//	                    else 
//	                    {
//	                        allEnchants.put(enchant, Math.max(allEnchants.getOrDefault(enchant, 0), level));
//	                    }
//	                });
//	            }
//	        }
//	    }
//
//
//	    if (!_stack.getType().equals(Material.AIR)) 
//	    {
//	        _stack.getEnchantments().keySet().forEach(_stack::removeEnchantment);
//	        allEnchants.forEach((enchant, level) -> _stack.addUnsafeEnchantment(enchant, level));
//	    }
//	}
	
	@SuppressWarnings("unused")
	public void ApplyEnchantsToItem() 
	{
	    Map<Enchantment, Integer> allEnchants = new HashMap<>();

	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            INode node = _nodes[i][j];
	            if (node instanceof NodeEnchant) 
	            {
	                NodeEnchant nodeEnchant = (NodeEnchant) node;
	                int totalBoost = 0;

	                for (INode neighbor : GetNeighbors(node)) 
	                {
	                	System.out.println("Neigbor: "+neighbor);
	                    if (neighbor instanceof NodeBooster) 
	                    {
	                        NodeBooster booster = (NodeBooster) neighbor;
	                        totalBoost += booster.Power;
	                    }
	                }
	                final int boost = totalBoost;
	                nodeEnchant.GetEnchantments().forEach((enchant, level) -> 
	                {
	                    int boostedLevel = CONSTANTS.ENABLE_MULTIPLE_SAME_ENCHANTS ? 
	                        allEnchants.getOrDefault(enchant, 0) + level + totalBoost : 
	                        Math.max(allEnchants.getOrDefault(enchant, 0), level + boost);
	                    allEnchants.put(enchant, boostedLevel);
	                });
	            }
	        }
	    }

	    if (!_stack.getType().equals(Material.AIR)) {
	        _stack.getEnchantments().keySet().forEach(_stack::removeEnchantment);
	        allEnchants.forEach((enchant, level) -> _stack.addUnsafeEnchantment(enchant, level));
	    }
	}


	
	public boolean ContainsEnchant(ItemStack itemStack) 
	{
	    if (itemStack == null) 
	    {
	        return false;
	    }

	    Map<Enchantment, Integer> itemEnchants = ItemUtils.GetEnchantsWithLevels(itemStack);
	    
	    Metods._ins.printEnchants(itemStack);
	    
	    for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
	    {
	        for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
	        {
	            INode node = _nodes[i][j];
	            if (node instanceof NodeEnchant) 
	            {
	            	
	                NodeEnchant nodeEnchant = (NodeEnchant) node;
	                for (Enchantment enchant : nodeEnchant.GetEnchantments().keySet()) 
	                {
	                	System.out.println("checking enchant: "+enchant);
	                    if (itemEnchants.containsKey(enchant)) 
	                    {
	                        return true;
	                    }
	                }
	            }
	        }
	    }

	    return false;
	}
	
	public ItemStack SetTooltip()
	{
		System.out.println("Adding lore");
		ItemUtils.AddOrReplaceLore(_stack, "&6Slots: &a"+_slots);
		return _stack;
	}
	
    public void SetSlots(int slots) 
    {
        this._slots = slots;
        ItemUtils.SetPersistenData(_stack, PD_SLOTS, PersistentDataType.INTEGER, slots);
    }
    
    private static Integer GetSlots(ItemStack stack) 
    {
        Integer slots = ItemUtils.GetPersistenData(stack, PD_SLOTS, PersistentDataType.INTEGER);
        return (slots != null) ? slots : 0; 
    }
    
    public static boolean HasSlots(ItemStack stack) 
    {
    	return GetSlots(stack) > 0;
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
	    
	    System.out.println("LOADING DATA: "+stack + " =========> DATA: "+serializedData);
	    
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

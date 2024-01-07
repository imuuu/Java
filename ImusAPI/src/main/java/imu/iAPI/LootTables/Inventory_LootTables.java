package imu.iAPI.LootTables;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Buttons.ButtonWithSelector;
import imu.iAPI.Buttons.GridButton;
import imu.iAPI.Buttons.SelectorString;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.InvUtil.CustomInventory;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Utilities.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory_LootTables extends CustomInventory
{

    public Inventory_LootTables()
    {
        super(ImusAPI._instance, "&9Drop Tables", 9 * 5);
    }

    @Override
    public INVENTORY_AREA setInventoryLock()
    {
        return INVENTORY_AREA.UPPER_LOWER_INV;
    }

    @Override
    public void onOpen()
    {
        super.onOpen();
        initButtons();
    }

    @Override
    public void onClose()
    {
        super.onClose();
    }

    private void initButtons()
    {
        loadLootTables();

    }

    private void loadLootTables()
    {
        HashMap<String, ImusLootTable> _lootTables = Manager_ImusLootTable.getInstance().get_lootTables();

        List<String> keys = new ArrayList<>(_lootTables.keySet());
        List<ImusLootTable> values = new ArrayList<>(_lootTables.values());

        List<IBUTTONN> buttons = new ArrayList<>();

        for(int i = 0; i < keys.size(); i++)
        {
            ImusLootTable table = values.get(i);
            final String key = keys.get(i);

            ItemStack stack = new ItemStack(Material.CHEST);
            ItemUtils.SetDisplayName(stack, keys.get(i));
            ItemUtils.AddLore(stack, "Click to edit", true);


            Button button = new Button(i, stack);
            button.setAction(inventoryClickEvent ->
            {
                loadLootTable(key);
            });

            buttons.add(button);
        }

        GridButton grid = new GridButton(buttons, 0, 9, 4);
        addGrid(grid);
        grid.update();
        grid.generateLeftRightButtons(getSize()-9, getSize()-1);
    }

    private void loadLootTable(String name)
    {
        System.out.println("load loot table: "+name);
        ImusLootTable table = Manager_ImusLootTable.getInstance().get_lootTable(name);
        if(table == null)
        {
            System.out.println("table is null");
            return;
        }
        List<IBUTTONN> buttons = new ArrayList<>();

        for(int i = 0; i < table.getItems().size(); i++)
        {
            ILootTableItem<?> item = table.getItems().get(i);
            buttons.add(createDisplayItemButton(item));

        }

        GridButton grid = new GridButton(buttons, 0, 9, 4);
        addGrid(grid);
        grid.update();

        grid.generateLeftRightButtons(getSize()-9, getSize()-1);
    }

    private IBUTTONN createDisplayItemButton(ILootTableItem item)
    {

        double weight = item.get_weight();
        double maxAmount = item.get_maxAmount();
        double minAmount = item.get_minAmount();
        ItemStack stack;
        System.out.println("item: "+item);
        if(item instanceof LootItemStack)
        {
            stack = ((ItemStack) item.get_value()).clone();
        }
        else
        {
            stack = new ItemStack(Material.BARRIER);
            ItemUtils.SetDisplayName(stack, "Not ItemStack");

        }
        ButtonWithSelector buttonSelector = new ButtonWithSelector(0, stack);
        buttonSelector.addLores(new SelectorString("&9Weight: &e%value%", weight));
        buttonSelector.addLores(new SelectorString("&9MinAmount: &e%value%", minAmount));
        buttonSelector.addLores(new SelectorString("&9MaxAmount: &e%value%", maxAmount));





        return buttonSelector;
    }

}

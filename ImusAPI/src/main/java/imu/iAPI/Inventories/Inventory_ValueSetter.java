package imu.iAPI.Inventories;

import imu.iAPI.Buttons.ButtonWithSelector;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Interfaces.IButtonHandler;
import imu.iAPI.Interfaces.ICustomInventory;
import imu.iAPI.InvUtil.CustomInventory;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Buttons.Button;
import imu.iAPI.Utilities.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Inventory_ValueSetter extends CustomInventory
{
    private IButtonHandler _buttonHandler;
    private ICustomInventory _customInventory;
    private ButtonWithSelector _button;
    private double _value = 0;
    private String _oldValue = "";
    public Inventory_ValueSetter(ButtonWithSelector button, ICustomInventory customInventory)
    {
        super(ImusAPI._instance, "§9Set Value", 3 * 9);

        _customInventory = customInventory;
        _button = button;
        _oldValue = _button.getSelectedValue();
    }

    @Override
    public INVENTORY_AREA setInventoryLock()
    {
        return INVENTORY_AREA.UPPER_LOWER_INV;
    }

    @Override
    public void onAwake()
    {
        InitButtons();
    }

    private void confirm()
    {
        _button.setSelectedValue(_value);
        _button.updateToCustomInventory();
        _customInventory.open(getPlayer());
    }

    private void InitButtons()
    {
        for(int i = 0; i < 9; i++)
        {
            final int value = i + 1;
            ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemUtils.SetDisplayName(stack, "§a+§f/§c- §b" + value);
            ItemUtils.AddLore(stack, "&bM1 &a++ &f| &bM2 &c--", true);

            Button button = new Button(i, stack, event ->
            {
                if(event.isLeftClick())
                {
                    add(value);
                }
                else if(event.isRightClick())
                {
                    add(-value);
                }
            });
            addButton(button);
        }
        add(0);

        updateButtons(false);
    }

    private void add(double amount)
    {
    	_value += amount;
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemUtils.AddLore(stack, "§eOld Value: §f" + _oldValue, true);
        ItemUtils.SetDisplayName(stack, "§eValue: §f" + _value);
        Button button = new Button(getSize()-5,stack);
        button.setAction(event ->
        {
           confirm();
        });
        addButton(button);
        updateButton(button);
    }
}

package imu.iAPI.Buttons;

import imu.iAPI.Interfaces.ICustomInventory;
import imu.iAPI.Utilities.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ButtonWithSelector extends Button
{

    private ICustomInventory _customInventory;
    private int _selectorIndex = 0;
    private int _selectorStartIndex = -1;
    
    private ArrayList<SelectorString> lores = new ArrayList<>();

    private final String[] _firstLores = new String[]
            {
                    " ",
                    "§eM&b1 §5Edit §9& §eM§b2 §fSelect",
                    " "
            };

    public ButtonWithSelector(int position, ItemStack stack, Consumer<InventoryClickEvent> onClickAction)
    {
        super(position, stack, onClickAction);
    }

    public ButtonWithSelector(int position, ItemStack stack)
    {
        super(position, stack);
    }

    public void addLores(SelectorString selectorString)
    {
        lores.add(selectorString);
    }

    private int getEndIndex()
    {
        return _selectorStartIndex + lores.size() - 1 + _firstLores.length;
    }

    @Override
    public boolean onClick(InventoryClickEvent event)
    {
        if (event.isRightClick() && _selectorIndex < getEndIndex())
        {
            _selectorIndex++;
        }
        else
        {
            _selectorIndex = _selectorStartIndex + _firstLores.length;
        }

        if (getButtonHandler() != null)
        {
            getButtonHandler().updateButton(this);
        }

        return event.isLeftClick();
    }

    @Override
    public void onUpdate()
    {
        ItemStack stack = getItemStack();
        createLores(stack);
    }

    private void createLores(ItemStack stack)
    {
        // Initialize _selectorStartIndex if it hasn't been set
        if (_selectorStartIndex == -1)
        {
            _selectorStartIndex = getLoreAmount(stack);
            _selectorIndex = _selectorStartIndex + _firstLores.length; // Set starting index after _firstLores
        }

        // Add _firstLores to the stack
        for (int i = 0; i < _firstLores.length; i++)
        {
            ItemUtils.setLore(stack, _selectorStartIndex + i, _firstLores[i]);
        }

        // Add lores from the lores ArrayList
        for (int i = 0; i < lores.size(); i++)
        {
            SelectorString selectorString = lores.get(i);
            String lore = selectorString.getStringWithValue();
            if (i + _selectorStartIndex + _firstLores.length == _selectorIndex) // Adjust index for selection
            {
                lore = "§e=>§r" + lore;
            }
            ItemUtils.setLore(stack, _selectorStartIndex + _firstLores.length + i, lore);
        }
    }

    private int getLoreAmount(ItemStack stack)
    {
        int amount = 0;
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            if (meta.getLore() != null)
            {
                amount = meta.getLore().size();
            }
        }

        return amount;
    }


}

package imu.iAPI.Buttons;

import imu.iAPI.Enums.LINE_DIRECTION;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.Interfaces.IButtonHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LineButton implements IBUTTONN
{
    private final int MAX_ROW_SIZE = 9;
    private final int MAX_COLUMN_SIZE = 6;
    private final int _startPosition;
    private final int _endPosition;
    private List<LineButtonPart> _parts;
    private LINE_DIRECTION _direction;
    private int _pageID = 0;
    private IButtonHandler _buttonHandler;

    public LineButton(IButtonHandler buttonHandler, int startPosition, int endPosition, List<ItemStack> items, LINE_DIRECTION direction)
    {
        _startPosition = startPosition;
        _endPosition = endPosition;
        _direction = direction;
        _parts = new ArrayList<>();
        _pageID = 0;
        _buttonHandler = buttonHandler;
        loadItems(items);
    }

    public void loadItems(List<ItemStack> items)
    {
        if (_direction == LINE_DIRECTION.HORIZONTAL)
        {
            loadHorizontal(items);
        }
        else
        {
            loadVertical(items);
        }

    }

    private void loadHorizontal(List<ItemStack> items)
    {
        int index = 0;
        for (ItemStack item : items)
        {
            loadItem(index, item);
            index++;
            if (index >= getLength())
            {
                index = 0;
            }
        }
    }

    private void loadVertical(List<ItemStack> items)
    {
        int index = 0;
        for (ItemStack item : items)
        {
            loadItem(9 * index, item);
            index++;
            if (index >= MAX_COLUMN_SIZE)
            {
                index = 0;
            }
        }
    }

    private void loadItem(int slot, ItemStack item)
    {
        System.out.println("slot: " + slot+ " item: " + item.getType());
        LineButtonPart buttonPart = new LineButtonPart(_startPosition + slot, item);
        //buttonPart.setItemStack(item);
        _parts.add(buttonPart);
    }

    public void switchPage()
    {
        _pageID++;
        if (_pageID > _parts.size() - 1)
        {
            _pageID = 0;
        }
        update();
    }

    public void update()
    {
        final int offset = _pageID * getLength();
        for (int i = offset; i < offset + getLength(); i++)
        {
            LineButtonPart buttonPart = _parts.get(i);
            _buttonHandler.addButton(buttonPart);
            _buttonHandler.updateButton(buttonPart);
            System.out.println("update: " + buttonPart.getPosition() + " index: " + i);
        }
    }

    private int getLength()
    {
        return _endPosition - _startPosition;
    }
    @Override
    public ItemStack getItemStack()
    {
        return null;
    }

    @Override
    public void setItemStack(ItemStack stack)
    {

    }

    @Override
    public void setMaxStackAmount(int amount)
    {

    }

    @Override
    public int getMaxStackAmount()
    {
        return 0;
    }

    @Override
    public int getPosition()
    {
        return _startPosition;
    }

    public int getEndPosition()
    {
        return _endPosition;
    }

    @Override
    public boolean isPositionLocked()
    {
        return false;
    }

    @Override
    public void setLockPosition(boolean lockPostion)
    {

    }

    @Override
    public void onUpdate()
    {

    }

    @Override
    public void action(InventoryClickEvent event)
    {

    }

    @Override
    public void setLastClickType(ClickType clickType)
    {

    }

    @Override
    public ClickType getLastClickType()
    {
        return null;
    }
}

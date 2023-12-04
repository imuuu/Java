package imu.iAPI.Buttons;

import imu.iAPI.Enums.LINE_DIRECTION;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.Interfaces.IButtonHandler;
import imu.iAPI.Utilities.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LineButton implements IBUTTONN
{
    private final int MAX_ROW_SIZE = 9;
    private final int MAX_COLUMN_SIZE = 6;
    private int _startPosition;
    private final int _lineLenght;
    private List<LineButtonPart> _parts;
    private LINE_DIRECTION _direction;
    private int _pageID = 0;
    private IButtonHandler _buttonHandler;

    private ItemStack _emptyStack;

    public LineButton(IButtonHandler buttonHandler, int startPosition, int lineLenght, List<ItemStack> items, LINE_DIRECTION direction)
    {
        _startPosition = startPosition;
        _lineLenght = lineLenght;
        _direction = direction;
        _parts = new ArrayList<>();
        _pageID = 0;
        _buttonHandler = buttonHandler;
        createEmptyStack();
        loadItems(items);
    }

    private void createEmptyStack()
    {
        _emptyStack = ItemUtils.SetDisplayNameEmpty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        ItemUtils.SetTag(_emptyStack, "empty");
    }

    private boolean isItemEmpty(ItemStack item)
    {
        return ItemUtils.HasTag(item, "empty");
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
        for (int i = index; i < getLength(); i++)
        {
            loadItem(i, _emptyStack);
        }
    }

    private void loadVertical(List<ItemStack> items)
    {
        int index = 0;
        for (ItemStack item : items)
        {
            loadItem(9 * index, item);
            index++;

            if (index >= getLength())
            {
                index = 0;
            }
        }
        for (int i = index; i < getLength(); i++)
        {
            loadItem(i * 9, _emptyStack);
        }

    }

    private void loadItem(int slot, ItemStack item)
    {
        System.out.println("slot: " + slot + " item: " + item.getType());
        LineButtonPart buttonPart = new LineButtonPart(_startPosition + slot, item);
        //buttonPart.setItemStack(item);
        _parts.add(buttonPart);
    }

    private boolean canShift(int offset)
    {
        if(_parts.isEmpty())
        {
            return false;
        }

        if (offset == 1)
        {
            return !isItemEmpty(_parts.get(_parts.size() - 1).getItemStack());
        }
        else if (offset == -1)
        {
            return !isItemEmpty(_parts.get(_startPosition + _lineLenght-1).getItemStack());
        }
        return false;
    }

    private void shiftPositions(int offset)
    {
        if (!canShift(offset))
        {
            Bukkit.getLogger().info("can't shift");
            return;
        }

        if (offset == 1)
        {
            LineButtonPart lastPart = _parts.remove(_parts.size() - 1);
            _parts.add(0, lastPart);
        }
        else if (offset == -1)
        {
            LineButtonPart firstPart = _parts.remove(0);
            _parts.add(firstPart);
        }

        // Update positions of parts
        for (int i = 0; i < _parts.size(); i++)
        {
            LineButtonPart part = _parts.get(i);
            int newPosition = calculateNewPosition(i);
            part.setPosition(newPosition);
        }
    }

    private int calculateNewPosition(int indexInList)
    {
        if (_direction == LINE_DIRECTION.HORIZONTAL)
        {
            return _startPosition + indexInList;
        }
        else
        { // For vertical direction
            return _startPosition + indexInList * 9;
        }
    }

    public void nextSlot()
    {
        shiftPositions(-1);
        update();
    }

    public void previousSlot()
    {
        shiftPositions(1);
        update();
    }

    public void nextPage()
    {
        _pageID++;
        if (_pageID >= getTotalPages())
        {
            _pageID = 0;
        }
        update();
    }

    public void previousPage()
    {
        _pageID--;
        if (_pageID < 0)
        {
            _pageID = getTotalPages() - 1; // Wrap around to the last page if less than 0
        }
        update();
    }

    private int getTotalPages()
    {
        return (int) Math.ceil((double) _parts.size() / getLength());
    }

    public void update()
    {
        final int offset = _pageID * getLength();
        for (int i = 0; i < getLength(); i++)
        {
            int index = offset + i;
            LineButtonPart buttonPart = _parts.get(index);
            int slot = buttonPart.getPosition();
           /* if ( slot < 0 || slot > 53 || slot < _startPosition || slot > _startPosition + getLength() - 1)
            {
                Bukkit.getLogger().info("slot: " + slot + " is out of bounds, material is: " + buttonPart.getItemStack().getType());
                continue;
            }*/
            _buttonHandler.addButton(buttonPart);
            _buttonHandler.updateButton(buttonPart);

        }
    }

    public IBUTTONN getPreviousButton()
    {
        ItemStack stack = new ItemStack(Material.BIRCH_SIGN);
        ItemUtils.SetDisplayName(stack, "&b<< Page");

        return new Button(-1, stack, inventoryClickEvent ->
        {
            previousPage();
        });
    }

    public IBUTTONN getNextButton()
    {
        ItemStack stack = new ItemStack(Material.BIRCH_SIGN);
        ItemUtils.SetDisplayName(stack, "&bPage >>");

        return new Button(-1, stack, inventoryClickEvent ->
        {
            nextPage();
        });
    }

    public IBUTTONN getPreviousSlotButton()
    {
        ItemStack stack = new ItemStack(Material.BIRCH_SIGN);
        ItemUtils.SetDisplayName(stack, "&b<< Scroll");

        return new Button(-1, stack, inventoryClickEvent ->
        {
            previousSlot();
        });
    }

    public IBUTTONN getNextSlotButton()
    {
        ItemStack stack = new ItemStack(Material.BIRCH_SIGN);
        ItemUtils.SetDisplayName(stack, "&bScroll >>");

        return new Button(-1, stack, inventoryClickEvent ->
        {
            nextSlot();
        });
    }

    /*private int getLength()
    {
        return _lineLenght - _startPosition;
    }*/

    private int getLength()
    {
        return _lineLenght;
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

    @Override
    public void setPosition(int position)
    {
        _startPosition = position;
    }

    public int getEndPosition()
    {
        return _lineLenght;
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

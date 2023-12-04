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
    private int _pageID = 0;
    private IButtonHandler _buttonHandler;

    private ItemStack _emptyStack;

    private final int _height;

    public LineButton(IButtonHandler buttonHandler, int startPosition, int lineLenght, int height, List<ItemStack> items)
    {
        _startPosition = startPosition;
        _lineLenght = lineLenght;
        _parts = new ArrayList<>();
        _pageID = 0;
        _height = height;
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
        loadHorizontal(items);

    }

    private void loadHorizontal(List<ItemStack> items)
    {
        int itemsPerPage = getLength() * _height;
        int totalSlots = (int) Math.ceil((double) items.size() / itemsPerPage) * itemsPerPage;

        for (int i = 0; i < totalSlots; i++)
        {
            int row = (i / getLength()) % _height;
            int column = i % getLength();
            ItemStack item = i < items.size() ? items.get(i) : _emptyStack;

            loadItem(column, row, item);
        }
    }

    private void loadItem(int column, int row, ItemStack item)
    {
        int slot = _startPosition + (row * 9) + column;
        LineButtonPart buttonPart = new LineButtonPart(slot, item);
        _parts.add(buttonPart);
    }

    private int calculateNewPosition(int indexInList)
    {
        int row = indexInList / getLength();
        int column = indexInList % getLength();
        return _startPosition + column + (row * 9);
    }

    private void loadItem(int slot, ItemStack item)
    {
        LineButtonPart buttonPart = new LineButtonPart(_startPosition + slot, item);
        _parts.add(buttonPart);
    }

    private boolean canShift(int offset)
    {
        if (_parts.isEmpty())
        {
            return false;
        }

        final int itemsPerPage = getLength() * _height;
        final int currentPageStartIndex = _pageID * itemsPerPage;
        final int currentPageEndIndex = Math.min((_pageID + 1) * itemsPerPage, _parts.size()) - 1;

        // Adjusted to include _startPosition in the index calculation
        int adjustedStartIndex = Math.max(currentPageStartIndex - _startPosition, 0);
        int adjustedEndIndex = Math.min(currentPageEndIndex - _startPosition, _parts.size() - 1);

        if (offset == 1)
        {
            // Check if the last item on the current page is not empty (shift left)
            return !isItemEmpty(_parts.get(_parts.size() - 1).getItemStack());
        }
        else if (offset == -1)
        {
            // Check if the first item on the current page is not empty (shift right)
            return adjustedStartIndex+itemsPerPage < _parts.size() && !isItemEmpty(_parts.get(adjustedStartIndex+itemsPerPage).getItemStack());
        }

        return false;
    }


    private void shiftPositions(int offset)
    {
        if (!canShift(offset))
        {
            return;
        }

        if (offset == 1)
        {
            // Shift left - move the last item to the front
            LineButtonPart lastPart = _parts.remove(_parts.size() - 1);
            _parts.add(0, lastPart);
        }
        else if (offset == -1)
        {
            // Shift right - move the first item to the end
            LineButtonPart firstPart = _parts.remove(0);
            _parts.add(firstPart);
        }

        // Update positions of parts considering _startPosition
        for (int i = 0; i < _parts.size(); i++)
        {
            LineButtonPart part = _parts.get(i);
            int newPosition = calculateNewPosition(i); // calculateNewPosition accounts for _startPosition
            part.setPosition(newPosition);
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
        final int itemsPerPage = getLength() * _height;
        return (int) Math.ceil((double) _parts.size() / itemsPerPage);
    }

    public void update()
    {
        final int itemsPerPage = getLength() * _height; // Total items per page
        final int offset = _pageID * itemsPerPage; // Calculate the offset for the current page

        for (int i = 0; i < itemsPerPage; i++)
        {
            int index = offset + i;

            // Check if the index is within the bounds of the _parts list
            if (index >= _parts.size())
            {
                break; // Exit the loop if the index exceeds the list size
            }

            LineButtonPart buttonPart = _parts.get(index);
            int slot = buttonPart.getPosition();

            // Optionally, you can check if the slot is within the inventory bounds
            if (slot < 0 || slot > 53)
            {
                Bukkit.getLogger().info("slot: " + slot + " is out of bounds, material is: " + buttonPart.getItemStack().getType());
                continue;
            }

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

package imu.iAPI.Buttons;

import imu.iAPI.Interfaces.IBUTTONN;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class LineButtonPart implements IBUTTONN
{
    private int _position;
    private ItemStack _stack;

    public LineButtonPart(int position, ItemStack stack)
    {
        _position = position;
        _stack = stack;
    }

    @Override
    public ItemStack getItemStack()
    {
        return _stack;
    }

    @Override
    public void setItemStack(ItemStack stack)
    {
        _stack  = stack;
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
        return _position;
    }

    @Override
    public boolean isPositionLocked()
    {
        return true;
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

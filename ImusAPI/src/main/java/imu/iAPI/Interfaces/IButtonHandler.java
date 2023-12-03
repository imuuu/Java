package imu.iAPI.Interfaces;

import imu.iAPI.Enums.INVENTORY_AREA;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface IButtonHandler
{
    public void addButton(IBUTTONN button);
    public void addButton(int position, IBUTTONN button);
    public IBUTTONN removeButton(int position);
    public IBUTTONN removeButton(IBUTTONN button);
    public IBUTTONN getButton(int position);
    public void setInventoryLock(INVENTORY_AREA lock);
    public void updateButtons(boolean clearEmpties);
    public void updateButton(IBUTTONN button);
    public void updateButton(int position);
    public void clearButtons();
    public void onHandlerOpen();
    public void onHandlerClose();
    public void handlePickupAll(InventoryClickEvent event, IBUTTONN button);
    public INVENTORY_AREA getInventoryArea(InventoryClickEvent e);
}

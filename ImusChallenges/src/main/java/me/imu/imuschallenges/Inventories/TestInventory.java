package me.imu.imuschallenges.Inventories;

import imu.iAPI.Buttons.LineButton;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Enums.LINE_DIRECTION;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.InvUtil.CustomInventory;
import me.imu.imuschallenges.ImusChallenges;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TestInventory extends CustomInventory
{
    public TestInventory()
    {
        super(ImusChallenges.getInstance(), "Testing", 54);
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

    private void initButtons()
    {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.APPLE));
        items.add(new ItemStack(Material.WHITE_WOOL));
        items.add(new ItemStack(Material.STONE));

        items.add(new ItemStack(Material.PODZOL));
        items.add(new ItemStack(Material.YELLOW_BED));
        items.add(new ItemStack(Material.NETHER_STAR));

        items.add(new ItemStack(Material.NETHER_BRICK));
        items.add(new ItemStack(Material.BLUE_BANNER));
        items.add(new ItemStack(Material.EGG));

        items.add(new ItemStack(Material.DIAMOND));
        items.add(new ItemStack(Material.LAPIS_BLOCK));
        items.add(new ItemStack(Material.GOLD_BLOCK));

        items.add(new ItemStack(Material.EMERALD));


        LineButton lineButton = new LineButton(getButtonHandler(), 1, 6, items, LINE_DIRECTION.VERTICAL);
        lineButton.update();

        IBUTTONN nextButton = lineButton.getNextSlotButton();
        nextButton.setPosition(53);
        addButton(nextButton);

        IBUTTONN prevButton = lineButton.getPreviousSlotButton();
        prevButton.setPosition(45);
        addButton(prevButton);

        updateButtons(false);



    }
}

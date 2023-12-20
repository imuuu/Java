package me.imu.imuschallenges.Inventories;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Buttons.GridButton;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.InvUtil.CustomInventory;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Managers.Manager_Vault;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.InvUtil;
import imu.iAPI.Utilities.ItemUtils;
import me.imu.imuschallenges.ImusChallenges;
import me.imu.imuschallenges.Managers.ManagerChallengeShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class InventoryChallengeShop extends CustomInventory
{
    private ManagerChallengeShop _managerChallengeShop = ManagerChallengeShop.getInstance();


    public InventoryChallengeShop()
    {
        super(ImusChallenges.getInstance(), "&6Challenge Shop", 6*9);

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
        ManagerChallengeShop.getInstance().addPlayerToShop(getPlayer());
        InitButtons();
    }

    @Override
    public void onClose()
    {
        super.onClose();
        ManagerChallengeShop.getInstance().removePlayerFromShop(getPlayer());
    }

    private void setNormalShopClose()
    {
        ArrayList<IBUTTONN> buttons = new ArrayList<>();
        GridButton gridButton = new GridButton(buttons,3+9,4,4);
        gridButton.setEmptyStack(ItemUtils.SetDisplayNameEmpty(new ItemStack(Material.RED_STAINED_GLASS_PANE)));
        addGrid(gridButton);
        gridButton.update();
        return;
    }

    private void InitButtons()
    {
        for(int i = 0; i < getSize(); i++)
        {
            IBUTTONN button = getEmptyButton(i, Material.BLACK_STAINED_GLASS_PANE);
            addButton(button);
        }
        updateButtons(false);

        if(_managerChallengeShop.hasPlayerBuyNormal(getPlayer().getUniqueId()))
        {
            setNormalShopClose();
            return;
        }

        _managerChallengeShop.getShopStatsAsync(getPlayer(), shopStats ->
        {
            ArrayList<IBUTTONN> buttons = new ArrayList<>();

            if(shopStats == null)
            {
                setNormalShopClose();
            }
            ArrayList<ItemStack> items = ManagerChallengeShop.getInstance().getGeneratedItems();
            ArrayList<Integer> cost = ManagerChallengeShop.getInstance().getItemCosts();

            for(int i = 0; i < items.size(); i++)
            {
                final int index = i;
                if( i < shopStats.getBought_normal_slots())
                {
                    ItemStack item = items.get(i);
                    ItemStack stack = item.clone();
                    ItemUtils.AddLore(stack, Metods.msgC(" "), true);
                    ItemUtils.AddLore(stack, Metods.msgC("&a&lCost: "+cost.get(i) + " &4Challenge points"), true);
                    Button button = new Button(i, stack, inventoryClickEvent ->
                    {
                        OnButtonBuyItem(index);
                    });
                    buttons.add(button);
                    continue;
                }

                ItemStack stack = getEmptyButton(i, Material.PURPLE_STAINED_GLASS_PANE).getItemStack();
                String displayName = Metods.msgC("&c&lBuy slot");
                ItemUtils.SetDisplayName(stack, displayName);
                ItemUtils.AddLore(stack, Metods.msgC(" "), true);
                ItemUtils.AddLore(stack, Metods.msgC("&aCost: "+_managerChallengeShop.getSlotPrice(i) + " &2$"), true);
                Button button = new Button(i, stack);
                button.setAction(inventoryClickEvent ->
                {
                    IBUTTONN thisButton = getButton(button.getUUID());
                    if(inventoryClickEvent.isLeftClick())
                    {
                        ItemUtils.SetDisplayName(thisButton.getItemStack(), displayName + " &6&k#&r &e(Confirm by &bM2)");
                        ItemUtils.AddGlow(thisButton.getItemStack());
                        ItemUtils.SetTag(thisButton.getItemStack(), "buy");
                        updateButton(thisButton);

                    }
                    if(inventoryClickEvent.isRightClick() && ItemUtils.HasTag(button.getItemStack(), "buy"))
                    {
                        OnButtonBuySlot(index);
                    }

                    //OnButtonBuySlot(index);
                });
                buttons.add(button);


            }

            GridButton gridButton = new GridButton(buttons,3+9,4,4);
            gridButton.setEmptyStack(ItemUtils.SetDisplayNameEmpty(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)));
            addGrid(gridButton);
            gridButton.update();
            //gridButton.generateLeftRightButtons(45, 53);
        });
    }

    private void OnButtonBuyItem(int index)
    {
        getPlayer().sendMessage(Metods.msgC("&9You bought an item(s) with &2" + _managerChallengeShop.getItemCosts().get(index) + " &6challenge points!"));
        _managerChallengeShop.setPlayerBuyNormal(getPlayer().getUniqueId(), true);
        InvUtil.AddItemToInventoryOrDrop(getPlayer(), ManagerChallengeShop.getInstance().getGeneratedItems().get(index).clone());
        getPlayer().closeInventory();
    }

    private void OnButtonBuySlot(int index)
    {
        //int cost = ManagerChallengeShop.getInstance().
        if(Manager_Vault.hasMoney(getPlayer(), _managerChallengeShop.getSlotPrice(index)))
        {
            Manager_Vault.takeMoney(getPlayer(), _managerChallengeShop.getSlotPrice(index));
            ManagerChallengeShop.getInstance().addSlotsToPlayerShopAsync(getPlayer(), 1);
            getPlayer().closeInventory();
            getPlayer().sendMessage(Metods.msgC("&aYou bought a slot!"));
        }
        else
        {
            getPlayer().sendMessage(Metods.msgC("&cYou don't have enough money!"));
        }

    }


}

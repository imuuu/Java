package me.imu.imuschallenges.Events;

import imu.iAPI.Other.Metods;
import me.imu.imuschallenges.CONSTANTS;
import me.imu.imuschallenges.ImusChallenges;
import me.imu.imuschallenges.Managers.ManagerCCollectMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class CollectionEventListener implements Listener
{
    private ImusChallenges _main = ImusChallenges.getInstance();
    private final ManagerCCollectMaterial _manager = ManagerCCollectMaterial.getInstance();

    public CollectionEventListener()
    {

    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player))
        {
            return;
        }

        Player player = (Player) event.getEntity();

        if(!player.hasPermission(CONSTANTS.PERM_SERVER_WIDE_COLLECTION_CHALLENGE))
        {
            return;
        }

        Material material = event.getItem().getItemStack().getType();

        if (_manager.isMaterialCollected(material))
        {
            return;
        }
        //Bukkit.getLogger().info(ChatColor.GREEN + "Player picked up item: "+event.getEntity());
        informPlayer(player, material);
        _manager.markMaterialAsCollected(material, player);

    }

    private void informPlayer(Player player, Material material)
    {
        if(player.hasPermission(CONSTANTS.PERM_SERVER_WIDE_COLLECTION_CHALLENGE_BROADCAST))
        {
            Bukkit.getServer().broadcast(Metods.msgC("&9" + player.getName() + " &9has &6Researched &3" + material.name()), CONSTANTS.PERM_SERVER_WIDE_COLLECTION_CHALLENGE_BROADCAST);
            return;
        }

        String message;
        message = Metods.msgC("&9New &6Research &9found! The &3" + material.name());

        player.sendMessage(message);


    }

}

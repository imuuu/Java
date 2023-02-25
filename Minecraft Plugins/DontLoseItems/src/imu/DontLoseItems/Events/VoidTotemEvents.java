package imu.DontLoseItems.Events;

import imu.DontLoseItems.main.DontLoseItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import imu.DontLoseItems.CustomItems.VoidTotemController;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.HashSet;

public class VoidTotemEvents implements Listener {
    private static VoidTotemEvents instance;
    private final VoidTotemController controller;

    private final HashSet<Player> players;
    @EventHandler
    public void voidDamageEvent(EntityDamageEvent e)
    {
        if(e.getEntity() instanceof Player)
        {
            Player player = (Player)e.getEntity();
            if(e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if(players.contains(player)) {
                    e.setCancelled(true);
                    return;
                }

                if(validateTotem(player.getInventory())) {
                    player.playEffect(EntityEffect.TOTEM_RESURRECT);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1200, 0));
                    players.add(player);
                    controller.findSafeBlock(player);
                }
            }
        }
    }

    private boolean validateTotem(PlayerInventory inv) {
        ItemStack offhand = inv.getItemInOffHand();
        ItemStack mainhand = inv.getItemInMainHand();

        if(getPersistant(offhand)) {
            inv.setItemInOffHand(new ItemStack(Material.AIR));
            return true;
        } else if(getPersistant(mainhand)) {
            inv.setItemInMainHand(new ItemStack(Material.AIR));
            return true;
        } else return false;
    }

    private boolean getPersistant(ItemStack item) {
        NamespacedKey key = new NamespacedKey(DontLoseItems.Instance, "totemtype");
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;

        String totemtype = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if(totemtype == null) return false;

        return totemtype.matches("void");
    }

    //recipe event ?
    public void setSaved(Player player) {
        player.removePotionEffect(PotionEffectType.LEVITATION);
        Color clr = new Color(45, 250, 158);
        player.sendMessage(ChatColor.of(clr) + "You were saved by the Void Totem!");
        player.sendMessage(ChatColor.of(clr) + "Be more careful next time!");
        players.remove(player);
    }

    public VoidTotemEvents() {
        instance = this;
        controller = new VoidTotemController();
        players = new HashSet<>();
    }

    public static VoidTotemEvents instance() {
        return instance;
    }
}

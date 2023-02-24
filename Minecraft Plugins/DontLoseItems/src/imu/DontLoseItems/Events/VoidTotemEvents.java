package imu.DontLoseItems.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import imu.DontLoseItems.CustomItems.VoidTotemController;

public class VoidTotemEvents implements Listener {
    private static VoidTotemEvents instance;
    private final VoidTotemController controller;

    @EventHandler
    public void voidDamageEvent(EntityDamageEvent e) 
    {
        if(e.getEntity() instanceof Player) 
        {
        	Player player = (Player)e.getEntity();
            if(e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                //redundant for now
                //Location safeLoc = controller.findSafeBlock(player);
                controller.findSafeBlock(player);
                //player.teleport(safeLoc);
            }
        }
    }

    //recipe event ?

    public VoidTotemEvents() {
        instance = this;
        controller = new VoidTotemController();
    }

    public VoidTotemEvents instance() {
        return instance;
    }
}

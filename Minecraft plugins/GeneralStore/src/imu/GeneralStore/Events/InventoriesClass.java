package imu.GeneralStore.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class InventoriesClass implements Listener
{
	Main _main;
	public InventoriesClass(Main main) 
	{
		_main = main;
	}
	
	@EventHandler
	public void onInvClic(PrepareAnvilEvent event)
	{
		if(event.getView().getPlayer() instanceof Player)
		{
			Player player = (Player) event.getView().getPlayer();
			AnvilInventory anvil_inv = (AnvilInventory) event.getInventory();
			ItemStack[] content = anvil_inv.getContents();

			if((_main.getItemM().findLoreIndex( content[0], "Soulbound") > -1 && _main.getItemM().findLoreIndex( content[1], "Soulbound") < 0) || 
			   (_main.getItemM().findLoreIndex( content[0], "Soulbound") < 0 && _main.getItemM().findLoreIndex( content[1], "Soulbound") > -1) 	)
			{
				if(content[0] == null || content[1] == null)
				{
					event.setResult(null);
					return;
				}
				event.setResult(null);
				player.sendMessage(ChatColor.RED + "You can't mix Soulbound item and normal item!");
			}
		}
		
	}
	
}

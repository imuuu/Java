package imu.UIC.INVs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import imu.UIC.main.Main;
import net.md_5.bungee.api.ChatColor;

public class SetMenuINV extends CustomInvLayout
{

	public SetMenuINV(Main main, Player player, String name, int size) 
	{
		super(main, player, name, size);
		setButtons();
	}
	
	enum Button
	{
		GO_BACK_MAINMENU(0);
				
		int type;
		
		Button(int i)
		{
			this.type = i;
		}
		public int getType()
		{
			return type;
		}		
	}
	
	void setButtons()
	{
		setSwitch(Button.GO_BACK_MAINMENU.getType(), Material.RED_STAINED_GLASS_PANE, ChatColor.AQUA + "GO BACK", 9);
	}
	
	void buttonTree(Button button)
	{
		
		switch(button)
		{
			case GO_BACK_MAINMENU:
				_main.getMainMenuManager().openNewInv(_player);
				break;
			default:
				System.out.println("No button");
		}
	}
	@EventHandler
	void onInvClick(InventoryClickEvent e)
	{
		if(isThisInv(e))
		{
			e.setCancelled(true);
			
			ItemStack stack = e.getCurrentItem();
			Integer bSwitch = getButtonSwitch(stack);
			if(bSwitch != null)
			{
				buttonTree(Button.values()[bSwitch]);
			}
			
			
		}
	}

}

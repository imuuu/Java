package imu.GeneralStore.SubCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.main.Main;

public class subStoreCmd implements CommandInterface
{
	Main main = Main.getInstance();
	
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
 
        //if(args.length > 1) return false;

        newInvenotry(player);
        player.sendMessage("IT is store prk!");
        
        
		//ConfigMaker cm = new ConfigMaker(main,"test.yml");
		//FileConfiguration config = cm.getConfig();
		//
		//for(Entry<String, ArrayList<Material>> entry :  main.sameGat.entrySet())
		//{
        //
		//	for(Material mat : entry.getValue())
		//	{
		//		config.set(entry.getKey()+"."+mat.name(), mat);
		//	}
		//}
		//cm.saveConfig();
		
		
        return false;
    }
    
    public void newInvenotry(Player player)
    {
    	main.shopManager.openShop(player, "General Store");
    	
    }
 
}
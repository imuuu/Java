package imu.GeneralStore.main;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import imu.GeneralStore.Commands.GeneralStoreCmd;
import imu.GeneralStore.Events.InventoriesClass;
import imu.GeneralStore.Handlers.CommandHandler;
import imu.GeneralStore.Other.ConfigMaker;
import imu.GeneralStore.Other.Shop;
import imu.GeneralStore.SubCommands.subStoreCmd;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin
{

	public HashMap<Player, ItemStack[]> playerInvContent = new HashMap<Player, ItemStack[]>();
		
	static Main instance;
	static Economy econ = null;
	
	public Shop shop1;
	
	public String playerInvContentYAML ="playerInvContent.yml";
    
	public void registerCommands() 
    {
    	
        CommandHandler handler = new CommandHandler();

        String cmd1="gs";
        handler.registerCmd(cmd1, new GeneralStoreCmd());       
        handler.registerSubCmd(cmd1, "shop", new subStoreCmd());
        
        
        getCommand(cmd1).setExecutor(handler);
        
    }
	
	@Override
	public void onEnable() 
	{
		instance = this;
		shop1 = new Shop(ChatColor.DARK_RED + "General Store");
		setupEconomy();
		makeInvConfig();
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" General Store has been activated!");
		getServer().getPluginManager().registerEvents(new InventoriesClass(), this);
	}
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static Economy getEconomy() 
	{
        return econ;
    }
	
	private boolean setupEconomy() 
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	void makeInvConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, playerInvContentYAML);
		FileConfiguration config = cm.getConfig();
		if(!cm.isExists())
		{			
			config.options().header("===Stores player content===");
			cm.saveConfig();
		}
		
	}

	
}

package me.imu.imuschallenges;

import imu.iAPI.CmdUtil.CmdHelper;
import imu.iAPI.Handelers.CommandHandler;
import imu.iAPI.Other.ImusTabCompleter;
import imu.iAPI.Other.MySQL;
import me.imu.imuschallenges.Commands.ExampleCmd;
import me.imu.imuschallenges.Events.CollectionEventListener;
import me.imu.imuschallenges.Managers.ManagerAchievementChallenges;
import me.imu.imuschallenges.Managers.ManagerCCollectMaterial;
import me.imu.imuschallenges.Managers.ManagerChallenges;
import me.imu.imuschallenges.SubCommands.SubOpenInvCmd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ImusChallenges extends JavaPlugin
{

    private static ImusChallenges _instance;

    public static ImusChallenges getInstance() {return _instance;}

    private MySQL _SQL;
    private ImusTabCompleter _tab_cmd1;
    private CmdHelper _cmdHelper;

    //Managers
    private static ManagerChallenges _managerChallenges;
    private static ManagerCCollectMaterial _managerCCollectMaterial;

    public static ManagerChallenges getManagerChallenges() {return _managerChallenges;}


    @Override
    public void onEnable()
    {
        _instance = this;
        connectDataBase();
        registerCommands();
        System.out.println("ImusChallenges has been enabled!");
        registerPermissions();
        _managerChallenges = new ManagerChallenges();
        _managerCCollectMaterial = new ManagerCCollectMaterial(this);
        getServer().getPluginManager().registerEvents(new CollectionEventListener(), this);
        getServer().getPluginManager().registerEvents(new ManagerAchievementChallenges(), this);


    }

    @Override
    public void onDisable()
    {
        System.out.println("ImusChallenges has been disabled!");
    }

    private void registerPermissions()
    {
        String permissionNode = CONSTANTS.PERM_SERVER_WIDE_COLLECTION_CHALLENGE;
        Permission permission = new Permission(permissionNode, "Allows compete server wide research materials competition", PermissionDefault.NOT_OP);
        Bukkit.getPluginManager().addPermission(permission);

        permissionNode = CONSTANTS.PERM_SERVER_WIDE_COLLECTION_CHALLENGE_BROADCAST;
        permission = new Permission(permissionNode, "Allows hear other research findings", PermissionDefault.NOT_OP);
        Bukkit.getPluginManager().addPermission(permission);
    }
    private boolean connectDataBase()
    {
        Bukkit.getLogger().info(ChatColor.GREEN + "[imusChallenges] Connecting to database...");
        _SQL = new MySQL(this, 4,"ImusChallenges");
        return true;
    }

    public MySQL GetSQL()
    {
        return _SQL;
    }

    public void registerCommands()
    {
        String _pluginName = "[ImusChallenges]";
        _cmdHelper = new CmdHelper(_pluginName);

        HashMap<String, String[]> cmd1AndArguments = new HashMap<>();
        CommandHandler handler = new CommandHandler(this);
        String cmd1 = "imuschallenges";
        handler.registerCmd(cmd1, new ExampleCmd());

        String cmd1_sub1 = "inv";
        String full_sub1 = cmd1 + " " + cmd1_sub1;
        _cmdHelper.setCmd(full_sub1, "Open Enchant Inv", full_sub1);
        handler.registerSubCmd(cmd1, cmd1_sub1, new SubOpenInvCmd(_cmdHelper.getCmdData(full_sub1)));
        handler.setPermissionOnLastCmd("ic.inv");

        cmd1AndArguments.put(cmd1, new String[] { "inv" });
        // cmd1AndArguments.put("create", new String[] {"card"});

        // register cmds
        getCommand(cmd1).setExecutor(handler);

        // register tabcompleters
        _tab_cmd1 = new ImusTabCompleter(cmd1, cmd1AndArguments, "ic.tabcompleter");
        getCommand(cmd1).setTabCompleter(_tab_cmd1);

    }
}

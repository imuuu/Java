package me.imu.imuschallenges;

import me.imu.imuschallenges.Managers.ManagerChallenges;
import org.bukkit.plugin.java.JavaPlugin;

public class ImusChallenges extends JavaPlugin
{

    private static ImusChallenges _instance;
    private static ImusChallenges getInstance() { return _instance; }

    private static ManagerChallenges _managerChallenges;
    @Override
    public void onEnable()
    {
        _instance = this;
        System.out.println("ImusChallenges has been enabled!");

    }

    @Override
    public void onDisable()
    {
        System.out.println("ImusChallenges has been disabled!");
    }
}

package imu.iMiniGames.Other;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Other.PlayerDataCard;
import imu.iMiniGames.Handlers.GameCard;

public class PlayerGameDataCard extends PlayerDataCard
{

	public PlayerGameDataCard(Plugin main, Player p, String dataFolderName) 
	{
		super(main, p, dataFolderName);
	}
	
	
	public void setDataToPLAYER(GameCard card, Player player)
	{	
		
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				if(player != null)
				{
					player.setHealth(_health);
					player.setFoodLevel(_foodLevel);
					changeExp(player, Integer.MAX_VALUE);
					changeExp(player, _xp);		
					
					player.setGameMode(_gamemode);
					player.addPotionEffects(_potionEffects);

					player.setFireTicks(_fireTick);
					if(player.getAllowFlight() && _isFlying)
					{
						player.setFlying(_isFlying);
					}
					
					if(card != null && card instanceof CombatGameCard && ((CombatDataCard) card.getDataCard()).isOwnGearKit)
					{
						
						player.getInventory().setContents(((CombatGameCard)card).checkAndApplyCombatConsumambles(player,_invContent));
					}else
					{
						player.getInventory().setContents(_invContent);
					}
					System.out.println("Minigame info: player: "+player.getName() + " has got all his levels/items/mode back!");
					_player.teleport(_location);	
				}
				else
				{
					System.out.println("Couldn't find player with uuid PlayerDataCard:setDataToPLAYER");
				}
			}
		}.runTask(_main);
		
	}
}

package imu.iMiniGames.Prompts;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import imu.iMiniGames.Arenas.Arena;
import imu.iMiniGames.Main.Main;
import net.md_5.bungee.api.ChatColor;

public class ConvPromptSetDescriptionArena extends StringPrompt
{

	Main _main;
	Player _player;

	int _ans_id;

	String _question;
	Arena _arena;
	public ConvPromptSetDescriptionArena(Main main, Player p, String question, Arena arena)
	{
		_main = main;
		_player  = p;

		_question = question;
		_arena = arena;

	}
	@Override
	public Prompt acceptInput(ConversationContext con, String anwser) 
	{
		//_card.putDataValue(_ans_id, anwser);
		//new SpleefGamePlaner(_main, _player, _card);
		_arena.set_description(anwser);
		_player.sendMessage(ChatColor.GOLD + "Description set!");
		_arena.sendArenaCreationgINFO(_player);
		return null;
	}

	@Override
	public String getPromptText(ConversationContext arg0) 
	{		
		
		return _question;
	}
	
	

}

package imu.GS.Prompts;

import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import imu.GS.ENUMs.ITEM_MOD_DATA;
import imu.GS.Invs.ShopModModifyINV;
import imu.GS.ShopUtl.ShopItemModData;
import imu.iAPI.Main.ImusAPI;

public class ConvPromptModModifyINV extends StringPrompt
{

	Player _player;

	int _ans_id;

	String _question;
	ShopModModifyINV _smmi;
	ShopItemModData _modData;
	ITEM_MOD_DATA _dataName;
		
	public ConvPromptModModifyINV(Player p, ITEM_MOD_DATA dataName, ShopModModifyINV smmi ,ShopItemModData modData, String question)
	{
		_player = p;
		_modData = modData;
		_dataName = dataName;
		_question = question;
		_smmi = smmi;
	}
	
	@Override
	public Prompt acceptInput(ConversationContext con, String anwser) 
	{
		//con.getForWhom().sendRawMessage("Thank you for ur awnser!: "+anwser );
		if(_dataName != ITEM_MOD_DATA.SELL_TIME_START)
		{
			if(_dataName == ITEM_MOD_DATA.DISTANCE_LOC)
			{
				Location loc =  _player.getLocation();
				String locStr = loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
				anwser = anwser.replace("this", locStr);
			}
			if(!_modData.SetAndCheck(_dataName, anwser)) return this;			
		}
		else
		{
			String[] times = anwser.split(" ");
			if(times.length != 2) return this;
			
			if(!_modData.SetAndCheck(ITEM_MOD_DATA.SELL_TIME_START, times[0])) return this;
			if(!_modData.SetAndCheck(ITEM_MOD_DATA.SELL_TIME_END, times[1])) return this;
		
		}
				
		if(_dataName == ITEM_MOD_DATA.WORLD_NAMES && anwser.equalsIgnoreCase("this")) anwser = _player.getWorld().getName();
		
		
		_smmi.SetModData(_modData);
		_smmi.openThis();
		
		return null;
	}

	@Override
	public String getPromptText(ConversationContext arg0) 
	{		
		return _question;
	}

}

package imu.GS.Prompts;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import imu.GS.ENUMs.ITEM_MOD_DATA;
import imu.GS.Invs.ShopModModifyINV;
import imu.GS.ShopUtl.ShopItemModData;

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
		_modData = modData;
		_question = question;
		_smmi = smmi;
	}
	@Override
	public Prompt acceptInput(ConversationContext con, String anwser) 
	{
		//con.getForWhom().sendRawMessage("Thank you for ur awnser!: "+anwser );

		if(!_modData.SetAndCheck(_dataName, anwser))
		{
			return this;
		}
		_smmi.openThis();
		return null;
	}

	@Override
	public String getPromptText(ConversationContext arg0) 
	{		
		return _question;
	}

}

package imu.iMiniGames.Invs;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import imu.iMiniGames.Arenas.CombatArena;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.PlanerManager;
import imu.iMiniGames.Other.CombatDataCard;
import imu.iMiniGames.Other.CombatGameCard;
import imu.iMiniGames.Prompts.ConvPromptCombatGamePlaner;

public class CombatGamePlaner extends GamePlaner
{

	CombatDataCard _card;
	PlanerManager _pm;
	
	ArrayList<Integer> wrongs = new ArrayList<>();
	
	public CombatGamePlaner(Main main, Player player, CombatDataCard card) 
	{
		super(main, player, ChatColor.DARK_AQUA + ""+ChatColor.BOLD + "Combat Planer");
		
		_card = card;
		_pm = main.get_planerManager();
		_card.set_bestOfMax(_main.get_combatManager().get_maximum_best_of());
		reset();
	}
	
	void reset()
	{
		setupButtons();
		checkAnwsers();
	}
	
	void setupButtons() 
	{
		ItemStack mod;
		String lore;
		setupButton(BUTTON.EXIT, Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "EXIT", _size-9);
		mod = setupButton(BUTTON.CONFIRM, Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "CONFIRM", _size-1);
		_itemM.addLore(mod, ChatColor.YELLOW + "Confirm your game plan and start sending invites..", true);
		
		setupButton(BUTTON.RESET, Material.LAVA_BUCKET, ChatColor.RED +""+ChatColor.BOLD+ "RESET", 8);
		
		addLoreSetRemove(setupButton(BUTTON.SET_ARENA, Material.PAINTING, ChatColor.AQUA + "Set Arena", 0));
		String arenaName = _card.get_arena() != null ? _card.get_arena().get_displayName() : "Random";
		_card.putDataValue(0, arenaName);
		
		 mod = addLoreSetRemove(setupButton(BUTTON.ADD_BET,Material.GOLD_INGOT, ChatColor.AQUA + "Place Bet", 2));
		 _itemM.addLore(mod, ChatColor.YELLOW + "How much every player pays", true);
		
		 mod = addLoreSetRemove(setupButton(BUTTON.ADD_PLAYERS, Material.WITHER_SKELETON_SKULL, ChatColor.AQUA + "Add Players", 4));
		 _itemM.addLore(mod, ChatColor.YELLOW + "Add your friends with you!", true);
		 
		 mod = addLoreSetRemove(setupButton(BUTTON.POTION_EFFECTS, Material.EXPERIENCE_BOTTLE, ChatColor.AQUA + "Add Potion Effects", 6));
		 _itemM.addLore(mod, ChatColor.YELLOW + "Add Potion effects to your match!", true);
		 
		 if(!_card.get_invPotionEffects().isEmpty())
		 {
			 _itemM.addLore(mod, ChatColor.BLUE + "== Bellow has added effects! ==", true);
			 for(Entry<PotionEffectType, PotionEffect> entry : _card.get_invPotionEffects().entrySet())
			 {
				 PotionEffect ef = entry.getValue();
				 _itemM.addLore(mod, ChatColor.DARK_PURPLE + ef.getType().getName() + " "+ef.getAmplifier(),true);
			 }
		 }
		 
		 mod = setupButton(BUTTON.ADD_BEST_OF_AMOUNT, Material.WRITABLE_BOOK, ChatColor.AQUA + "Add Best of (amount)", 10);
		 _itemM.addLore(mod, ChatColor.AQUA + "M1: "+ChatColor.GREEN + "Increase"+ChatColor.AQUA + " M2: "+ChatColor.RED + "Decrease", false);	
		 _itemM.addLore(mod, ChatColor.YELLOW + "Add how many wins person needs to win the small tournament!", true);
		 _itemM.addLore(mod, ChatColor.AQUA + "Best of: " +ChatColor.DARK_GREEN + _card.get_bestOfAmount(), true);
		 
		 mod = addLoreSetRemove(setupButton(BUTTON.SET_KIT, Material.NETHERITE_CHESTPLATE, ChatColor.AQUA + "Set Kit", 12));
		 _itemM.addLore(mod, ChatColor.YELLOW + "Choose kit which all players use in combat!", true);
		 if(_card.get_kit() != null)
		 {
			 lore = ChatColor.translateAlternateColorCodes('&', "&bSet to: "+_card.get_kit().get_kitName());
		 }else
		 {
			 lore = ChatColor.translateAlternateColorCodes('&', "&bSet to: &cRandom");
		 }
		 _itemM.addLore(mod, lore, true);
		 
	}
	
	ItemStack addLoreSetRemove(ItemStack stack)
	{
		return _itemM.addLore(stack, ChatColor.AQUA + "M1: "+ChatColor.GREEN + "Set"+ChatColor.AQUA + " M2: "+ChatColor.RED + "Remove", false);		
	}
	
	void setPDvalue(ItemStack stack, String value)
	{
		_pm.setPDvalue(stack,value);
		String str = "Set to: ";
		
		_itemM.removeLore(stack, str);
		_itemM.addLore(stack, ChatColor.AQUA + str +ChatColor.DARK_GREEN + value, true);
		
	}
	void removePDvalue(ItemStack stack)
	{
		if(stack == null)
			return;
		
		_pm.removePDvalue(stack);
		String str = "Set to: ";
		
		_itemM.removeLore(stack, str);
		
	}
	
	void checkAnwsers()
	{
		if(!_card.get_invDataValues().isEmpty())
		{
			for(int i = 0; i < _inv.getContents().length; ++i)
			{
				String value = _card.getDataValue(i);
				if(value != null)
				{
					setPDvalue(_inv.getItem(i), value);
					if(wrongs.contains(i))
					{
						_inv.getItem(i).setType(Material.YELLOW_STAINED_GLASS);
					}
				}else
				{
					removePDvalue(_inv.getItem(i));
				}
			}
		}
			
	}
	
	CombatGameCard confirm()
	{
				
		CombatGameCard gameCard = new CombatGameCard();
		gameCard.set_maker(_player);
		wrongs.clear();
		
		for(int i = 0; i < _inv.getContents().length; ++i)
		{
			ItemStack s = _inv.getItem(i);
			if(s != null)
			{
				
				BUTTON button = getButton(s);

				String value = _pm.getPDvalue(s);
				
				if(button == BUTTON.SET_ARENA)
				{
					CombatArena arena;
					if(value != null && value.equalsIgnoreCase("random"))
					{
						int arenas_size = _main.get_combatManager().getArenas().size();
						if(arenas_size > 0)
						{
							//Random rand = new Random();
							int r = new Random().nextInt(arenas_size);
							arena = _main.get_combatManager().getArena(r);
						}
						else
						{
							wrongs.add(i);
							_player.sendMessage(ChatColor.RED + "Didn't find any arenas");
							break;
						}
					}
					else
					{
						arena = _card.get_arena();						
					}
					
					if(arena != null)
					{
						gameCard.set_arena(arena);
					}
					else
					{
						_player.sendMessage(ChatColor.RED + "Didn't find arena with that name: " + value);
						wrongs.add(i);
					}
					break;
				}
			}
		}
	
		if(gameCard.get_arena() == null)
		{
			_player.sendMessage(ChatColor.RED+"Something went wrong! Coulnd't find arena");
			return null;
		}
		for(int i = 0; i < _inv.getContents().length; ++i)
		{
			ItemStack s = _inv.getItem(i);
			if(s != null)
			{
				
				BUTTON button = getButton(s);

				String value = _pm.getPDvalue(s);
				
				switch (button) 
				{
				case ADD_BET:
					if(value == null)
						continue;
					
					if(_itemM.isDigit(value))
					{
						double bet =Double.parseDouble(value);
						if(bet > 0)
						{
							gameCard.set_bet(bet);
						}else
						{
							_player.sendMessage(ChatColor.RED + "Bet need to be positive!");
							wrongs.add(i);
						}
						
					}else
					{
						_player.sendMessage(ChatColor.RED + "Please add only numbers");
						wrongs.add(i);
					}
					break;
				case ADD_PLAYERS:
					Player p;
					String[] players_str = value != null ? value.trim().replaceAll(" +"," ").split(" ") : null;

					if(value != null && players_str.length > 0)
					{
						boolean found = false;
						for(String pName : players_str)
						{
							p = Bukkit.getPlayer(pName);
							if(p != null)
							{
								found = true;
								gameCard.putPlayer(p);
								
								if(_main.isPlayerBlocked(p))
								{
									wrongs.add(i);
									_player.sendMessage(ChatColor.RED + p.getName() + " has blocked minigames! You can't in invite him/her!");
								}
							}
						}
						// adds player if not added
						gameCard.putPlayer(_player);
						

						if(gameCard.get_players_accept().size() > gameCard.get_arena().get_maxPlayers())
						{
							_player.sendMessage(ChatColor.RED+ "Too many players for the arena!");
							wrongs.add(i);
						}
						
						
						if(!found)
						{
							_player.sendMessage(ChatColor.RED+ "Didnt find any players with given names");
							wrongs.add(i);
						}
						

						if(gameCard.get_players_accept().size() < 2)
						{
							_player.sendMessage(ChatColor.RED + "Not enough players! Need at least 2");
							wrongs.add(i);
						}
													
					}
					else
					{
						_player.sendMessage(ChatColor.RED + "You need at least 2 players");
						wrongs.add(i);
					}
					break;
				case SET_ARENA:					
					break;
				default:
					break;
					
				}
			}
		}
		
		if(!wrongs.isEmpty())
		{
			
			return null;
		}
		else
		{
			
			double total_bet = Math.round((gameCard.get_bet() * gameCard.get_players_accept().size() * (1.0-_main.get_combatGameHandler().getBet_fee_percent()))*100)/100;
			gameCard.set_total_bet(total_bet);
			gameCard.set_combatDataCard(_card);
			
			return gameCard;
		}
	}
	void saveDataCard()
	{
		_main.get_combatManager().savePlayerDataCard(_player, _card);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		int rawSlot = e.getRawSlot();
		int slot = e.getSlot();
		
		if(isThisInv(e) && (rawSlot == slot))
		{			
			e.setCancelled(true);
			ItemStack stack = e.getCurrentItem();
			
			ConversationFactory cf = null;
			String question = null;
			Conversation conv = null;
			
			BUTTON button = getButton(stack);
			
			if(e.getClick() == ClickType.LEFT)
			{
				switch (button) {
				
				case NONE:				
					break;
				
				case EXIT:
					_player.closeInventory();
					break;
				case CONFIRM:
					CombatGameCard gameCard = confirm();
					if(gameCard != null)
					{	
						_main.get_combatGameHandler().savePlayerGameCard(_player, gameCard);						
						_main.get_combatGameHandler().repearStartGame(_player, gameCard);
						//saveDataCard();
						_player.closeInventory();
						
					}
					else
					{
						//System.out.println("Card null");
						checkAnwsers();
					}
					break;
				case ADD_BET:
					cf = new ConversationFactory(_main);
					question = ChatColor.DARK_PURPLE + "Give your bet";
					conv = cf.withFirstPrompt(new ConvPromptCombatGamePlaner(_main, _player, slot, question)).withLocalEcho(true).buildConversation(_player);
					conv.begin();
					_player.closeInventory();
					break;
				case ADD_PLAYERS:
					cf = new ConversationFactory(_main);
					question = ChatColor.DARK_PURPLE + "Give players seprate with space!(ex: imu joksu789";
					conv = cf.withFirstPrompt(new ConvPromptCombatGamePlaner(_main, _player, slot, question)).withLocalEcho(true).buildConversation(_player);
					conv.begin();
					_player.closeInventory();
					break;
				case RESET:
					_card = new CombatDataCard(_player);
					reset();
					break;
					
				case SET_ARENA:
					new CombatGamePlanerChooseArenaINV(_main, _player, _card);
					break;
				case POTION_EFFECTS:
					new CombatGamePlanerPotionEffectsINV(_main, _player, _card);
					break;
				case ADD_BEST_OF_AMOUNT:
					_card.addBestOfAmount(1);
					setupButtons();
					checkAnwsers();
					break;
				default:
					break;
				}
			}
			else if(e.getClick() == ClickType.RIGHT)
			{
				switch (button) 
				{
				case ADD_BET:
					_card.removeDataValue(slot);
					if(wrongs.contains(slot))
						wrongs.remove(slot);
					//setupButtons();
					checkAnwsers();
					break;
				case ADD_PLAYERS:
					_card.removeDataValue(slot);
					if(wrongs.contains(slot))
						wrongs.remove(slot);
					//setupButtons();
					checkAnwsers();
					break;

				case SET_ARENA:
					_card.set_arena(null);
					reset();
					break;
				case POTION_EFFECTS:
					_card.get_invPotionEffects().clear();
					reset();
					break;
				case ADD_BEST_OF_AMOUNT:
					_card.addBestOfAmount(-1);
					setupButtons();
					checkAnwsers();
					break;
				default:
					break;
				
				
				
				}
			}
			
			
		}
	
	}
	
	@EventHandler
	public void invClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			saveDataCard();
			e.getPlayer().sendMessage(ChatColor.GOLD + "Spleef plan has saved!");
		}
	}
}

package imu.iMiniGames.Other;

import imu.iMiniGames.Arenas.CombatArena;
import imu.iMiniGames.Handlers.GameCard;

public class CombatGameCard extends GameCard
{

	CombatDataCard _combatDataCard;

	public CombatArena get_arena() {
		return (CombatArena)_arena;
	}
	public void set_arena(CombatArena _arena) {
		this._arena = _arena;
	}
	
	public CombatDataCard get_combatDataCard() {
		return _combatDataCard;
	}
	public void set_combatDataCard(CombatDataCard _combatDataCard) {
		this._combatDataCard = _combatDataCard;
	}
	
	
	
	
	
	
	

	
	
	
	
	
}

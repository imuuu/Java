package imu.iMiniGames.Other;

import imu.iMiniGames.Arenas.SpleefArena;
import imu.iMiniGames.Handlers.GameCard;

public class SpleefGameCard extends GameCard
{

	SpleefDataCard _spleefDataCard;
	
	SpleefArena _arena = null;

	public SpleefArena get_arena() {
		return _arena;
	}
	public void set_arena(SpleefArena _arena) {
		this._arena = _arena;
	}
	
	
	
	public SpleefDataCard get_spleefDataCard() {
		return _spleefDataCard;
	}
	public void set_spleefDataCard(SpleefDataCard _spleefDataCard) {
		this._spleefDataCard = _spleefDataCard;
	}
	
	
	
	
	
	
}

package imu.iMiniGames.Other;

public class MiniGamePlayerStats 
{
	private int _score = 0;
	private double _hp = 0;
	private double _damage_done = 0;
	private double _damage_taken = 0;
	
	public MiniGamePlayerStats()
	{
		
	}

	public int get_score() {
		return _score;
	}

	public void set_score(int _score) {
		this._score = _score;
	}

	public void addScore(int amount)
	{
		_score += amount;
	}
	public double get_hp() {
		return _hp;
	}

	public void set_hp(double _hp) {
		this._hp = _hp;
	}

	public double get_damage_done() {
		return _damage_done;
	}

	public void set_damage_done(double _damage_done) {
		this._damage_done = _damage_done;
	}
	
	public void addDamageDone(double amount)
	{
		_damage_done += amount;
	}
	public void addDamageTaken(double amount)
	{
		_damage_taken += amount;
	}
	
	public double get_damage_taken() {
		return _damage_taken;
	}

	public void set_damage_taken(double _damage_taken) {
		this._damage_taken = _damage_taken;
	}
	
}

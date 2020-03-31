package Other;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
public class TeleChecks {
	
		
	Player _player;
	Location _startLoc;
	Plugin _plugin;
	
	double t = 0;
	double t_count = 0;
	Location _animLoc;
	
	public TeleChecks(Player p, Plugin plugin) 
	{
		_player = p;
		_plugin = plugin;
		_startLoc = p.getLocation();
		_animLoc = p.getLocation();
	}
	
	public boolean canTeleport()
	{
		boolean canTele=true;
		if(_player.getLocation().distance(_startLoc) > 1)
		{
			canTele=false;
		}

		return canTele;
	}
	
	
	void drawCircleHurrican(Particle particle,int particle_count, double radius , double t_delta, double y_deltaM )
	{
		double x, y, z;
		Location loc = new Location(_startLoc.getWorld(), _startLoc.getX(), _startLoc.getY(), _startLoc.getZ());
		double tt = (Math.PI/10 * t_count) ;
			
		x = radius * Math.cos(tt + t_delta);
		y = tt * y_deltaM;		
		z = radius * Math.sin(tt + t_delta);
		
		loc.add(x,y,z);
		_player.getWorld().spawnParticle(particle,loc, particle_count,0,0,0);
	}
	
	void drawCircle(double radius, Particle particle, int particle_count)
	{
		double x, y, z;
		Location loc;
		for(int angle = 0 ; angle < 360 ; angle++)
		{
			loc = new Location(_startLoc.getWorld(), _startLoc.getX(), _startLoc.getY(), _startLoc.getZ());
			x = radius * Math.cos(angle);
			y = 0;
			z = radius * Math.sin(angle);
			loc.add(x,y,z);
			_player.getWorld().spawnParticle(particle, loc, particle_count,0,0,0);
		}
	
	}
	
	public boolean drawAnimation(double total_time_seconds)
	{
		double change= 20 * total_time_seconds;
		if(t_count < change*0.3 )
		{
			for(int i = 0; i < 2; i++)
			{
				drawCircleHurrican(Particle.DRIP_LAVA, 1, 1, i*10, 0.1);
			}			
			
		}
		else if(t_count < change*0.6)
		{

			drawCircle((t_count-change*0.3)*0.03, Particle.DRIP_LAVA, 1);
					
		}
		
		
		
		if(t_count >= change)
			return true;
		
		t_count +=1;
		
		return false;
	}
}

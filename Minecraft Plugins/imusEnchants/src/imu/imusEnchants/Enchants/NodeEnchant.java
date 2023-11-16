package imu.imusEnchants.Enchants;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class NodeEnchant extends Node
{
	public Enchantment Enchant;
	
	public NodeEnchant(){}
	
	public NodeEnchant(Enchantment enchantment)
	{
		Enchant = enchantment;
		SetLock(false);
	}
	
	@Override
    public String Serialize() 
	{
        return this.getClass().getSimpleName() + 
        		":" + GetX() + 
        		":" + GetY() + 
        		":" + Enchant.getKey().toString();
    }

    @Override
    public void Deserialize(String data) 
    {
        String[] parts = data.split(":");
        _x = Integer.parseInt(parts[1]);
        _y = Integer.parseInt(parts[2]);
        this.Enchant = Enchantment.getByKey(NamespacedKey.fromString(parts[3]));
    }
}

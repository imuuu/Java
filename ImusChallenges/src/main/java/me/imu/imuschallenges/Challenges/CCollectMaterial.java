package me.imu.imuschallenges.Challenges;

import me.imu.imuschallenges.CHALLENGE_TYPE;
import org.bukkit.Material;

public class CCollectMaterial extends Challenge
{
    private Material _material;
    private int _amount;
    private String[] _lore;
    private CHALLENGE_TYPE _challengeType;

    public CCollectMaterial(String name, String[] lore, CHALLENGE_TYPE challenge_type, Material material, int amount)
    {
        super(name);
        _material = material;
        _amount = amount;
        _lore = lore;
    }



}

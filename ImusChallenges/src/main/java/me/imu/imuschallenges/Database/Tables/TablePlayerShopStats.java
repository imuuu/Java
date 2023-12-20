package me.imu.imuschallenges.Database.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import me.imu.imuschallenges.CONSTANTS;

@DatabaseTable(tableName = "player_shop_stats")
public class TablePlayerShopStats
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "player_id")
    private TablePlayers player;

    @DatabaseField(canBeNull = false, defaultValue = "4")
    private int bought_normal_slots;

    public TablePlayerShopStats() {}

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public TablePlayers getPlayer()
    {
        return player;
    }

    public void setPlayer(TablePlayers player)
    {
        this.player = player;
    }

    public int getBought_normal_slots()
    {
        return bought_normal_slots;
    }

    public void setBought_normal_slots(int bought_normal_slots)
    {
        this.bought_normal_slots = bought_normal_slots;
    }
}

package me.imu.imuschallenges.Managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import me.imu.imuschallenges.Database.Tables.TablePointType;
import me.imu.imuschallenges.Enums.POINT_TYPE;
import me.imu.imuschallenges.ImusChallenges;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerPointType
{
    private static ManagerPointType _instance;

    public static ManagerPointType getInstance()
    {
        return _instance;
    }

    private final ImusChallenges _main;
    private final Dao<TablePointType, Integer> pointTypeDao;
    private List<TablePointType> pointTypeList;

    private static final String[] DEFAULT_POINT_TYPES = new String[]{
            POINT_TYPE.CHALLENGE_POINT.toString(),
    };

    public ManagerPointType(ImusChallenges main)
    {
        _main = main;
        _instance = this;
        try
        {
            pointTypeDao = DaoManager.createDao(_main.getSource(), TablePointType.class);
            TableUtils.createTableIfNotExists(_main.getSource(), TablePointType.class);
            LoadData();

        } catch (SQLException e)
        {
            throw new RuntimeException("Could not create DAO or table for TablePointType", e);
        }
    }

    private void LoadData()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ensureDefaultPointTypes();
                loadPointTypes();
            }
        }.runTaskAsynchronously(_main);
    }

    private void ensureDefaultPointTypes()
    {
        for (String pointType : DEFAULT_POINT_TYPES)
        {
            try
            {
                if (pointTypeDao.queryForEq("pointTypeName", pointType).isEmpty())
                {
                    addPointType(pointType);
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void loadPointTypes()
    {
        try
        {
            pointTypeList = new ArrayList<>(pointTypeDao.queryForAll());
        } catch (SQLException e)
        {
            pointTypeList = new ArrayList<>();
            e.printStackTrace();
        }
    }

    public List<TablePointType> getPointTypes()
    {
        return pointTypeList;
    }

    public TablePointType getPointTypeByName(String pointTypeName)
    {
        for (TablePointType pointType : pointTypeList)
        {
            if (pointType.getPointTypeName().equalsIgnoreCase(pointTypeName))
            {
                return pointType;
            }
        }
        // Return null if no matching point type is found
        return null;
    }

    public TablePointType findOrCreatePointType(String pointTypeName)
    {
        TablePointType pointType = getPointTypeByName(pointTypeName);
        if (pointType == null)
        {
            addPointType(pointTypeName);
            pointType = getPointTypeByName(pointTypeName);
        }
        return pointType;
    }
    private void addPointType(String pointTypeName)
    {
        Bukkit.getLogger().info("[ImusChallenges] Creating point type: " + pointTypeName);
        TablePointType pointType = new TablePointType();
        pointType.setPointTypeName(pointTypeName);
        try
        {
            pointTypeDao.create(pointType);
            pointTypeList.add(pointType);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}

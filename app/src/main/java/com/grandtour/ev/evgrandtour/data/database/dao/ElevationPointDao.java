package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;

@Dao
public interface ElevationPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<ElevationPoint> elevationPoints);

    @Query("DELETE FROM ElevationPoint")
    int deleteAll();

    @Query("UPDATE elevationpoint SET elevation =:elevation WHERE elevationPointId =:elevationPointId")
    void updateElevationPoint(int elevationPointId, double elevation);

    @Query("SELECT * FROM elevationpoint WHERE routeLegId IN (:routeLegId)")
    List<ElevationPoint> getElevationPointsForRouteLegIds(List<Long> routeLegId);

    @Query("SELECT * FROM elevationpoint")
    Maybe<List<ElevationPoint>> getAllElevationPoints();

    @Query("SELECT * FROM elevationpoint WHERE routeLegId =:routeLegId")
    Maybe<List<ElevationPoint>> getElevationPointsForRouteLegId(Integer routeLegId);

    @Query("SELECT * FROM ElevationPoint WHERE routeLegId IN (SELECT routeLegId FROM RouteLeg WHERE startCheckpointId BETWEEN :startCheckpointId AND :endCheckpointId) ")
    Maybe<List<ElevationPoint>> getElevationPointsBetweenTwoCheckpoints(int startCheckpointId, int endCheckpointId);

}

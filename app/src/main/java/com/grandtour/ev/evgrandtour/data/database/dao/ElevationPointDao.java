package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

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

    @Query("SELECT * FROM elevationpoint WHERE routeLegId =:routeLegId")
    Maybe<List<ElevationPoint>> getElevationPointsForRouteLegId(Integer routeLegId);

}

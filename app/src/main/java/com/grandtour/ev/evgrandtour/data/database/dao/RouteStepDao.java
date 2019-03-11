package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RouteStepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertRouteLeg(List<RouteStep> routeSteps);

    @Query("DELETE FROM RouteStep")
    int deleteAll();

    @Query("SELECT * FROM ROUTESTEP WHERE routeLegId = :legId")
    List<RouteStep> getStepsForLegId(int legId);

}

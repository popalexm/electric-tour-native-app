package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RouteStepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertRouteLeg(List<RouteStep> routeSteps);

    @Query("DELETE FROM RouteStep")
    int deleteAll();

    @Query("SELECT * FROM ROUTESTEP WHERE routeLegId = :legId")
    List<RouteStep> getStepsForLegId(int legId);

}

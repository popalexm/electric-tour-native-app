package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.support.annotation.NonNull;

import java.util.List;

@Dao
public interface RouteStepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertRouteLeg(@NonNull List<RouteStep> routeSteps);
}

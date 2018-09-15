package com.grandtour.ev.evgrandtour.data.persistence.dao;

import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWaypoint;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

@Dao
public interface RouteWaypointsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] addWaypoints(List<RouteWaypoint> waypoints);

}

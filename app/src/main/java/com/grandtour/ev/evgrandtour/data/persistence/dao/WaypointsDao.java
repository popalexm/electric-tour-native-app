package com.grandtour.ev.evgrandtour.data.persistence.dao;

import com.grandtour.ev.evgrandtour.data.persistence.models.Waypoint;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

@Dao
public interface WaypointsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] addWaypoints(List<Waypoint> waypoints);

    @Query("DELETE FROM waypoints")
    int deleteAllStoredWaypoints();

    @Query("SELECT * FROM waypoints ")
    Maybe<List<Waypoint>> getAvailableWaypoints();

}

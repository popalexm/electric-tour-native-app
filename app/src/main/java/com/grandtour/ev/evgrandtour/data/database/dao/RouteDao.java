package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.database.models.RouteWithWaypoints;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Route route);

    @Query("Select * from ROUTE")
    Maybe<List<RouteWithWaypoints>> getAllRoutesAddWaypoints();

    @Query("DELETE FROM ROUTE")
    int deleteAll();

    @Query("Select count(*) from ROUTE")
    Single<Integer> getTotalAvailableRoutes();
}

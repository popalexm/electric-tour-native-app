package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.Route;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Route route);

    @Query("DELETE FROM ROUTE")
    int deleteAll();

    @Query("Select * from ROUTE WHERE tourId = :tourId")
    List<Route> getRoutesForTourId(String tourId);
}

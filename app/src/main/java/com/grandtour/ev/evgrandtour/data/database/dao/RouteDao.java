package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.Route;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Route route);

    @Query("DELETE FROM ROUTE")
    int deleteAll();

    @Query("Select * from ROUTE WHERE tourId = :tourId")
    Maybe<List<Route>> getRouteForSelectedTour(@NonNull String tourId);
}

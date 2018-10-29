package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.Tour;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface TourDao {

    @Query("SELECT tourId FROM TOUR WHERE isCurrentSelection= 1")
    Maybe<String> getCurrentlySelectedTourId();

    @Query("SELECT name FROM TOUR WHERE isCurrentSelection= 1")
    Maybe<String> getCurrentlySelectedTourName();

    @Query("UPDATE TOUR SET isCurrentSelection = :isSelected WHERE tourId = :tourId")
    void updateTourSelectionById(String tourId, Integer isSelected);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<Tour> toursList);

    @Query("DELETE FROM TOUR")
    int deleteAll();
}

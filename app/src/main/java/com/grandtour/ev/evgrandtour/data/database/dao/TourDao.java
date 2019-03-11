package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.Tour;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
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

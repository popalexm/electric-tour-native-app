package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.InPlanningTrip;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface InPlanningTripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(InPlanningTrip inPlanningTrip);

    @Query("DELETE FROM InPlanningTrip")
    int deleteAll();

    @Query("SELECT count(*) FROM InPlanningTrip")
    int checkNumberOfTableRows();

    @Query("SELECT * FROM InPlanningTrip WHERE tripId = :tripId")
    InPlanningTrip getInPlanningTripById(long tripId);

    @Query("SELECT * FROM InPlanningTrip LIMIT 1")
    InPlanningTrip getLastInPlanningTrip();
}

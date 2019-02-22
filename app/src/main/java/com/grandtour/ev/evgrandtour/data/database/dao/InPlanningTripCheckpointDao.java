package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.InPlanningCheckpoint;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface InPlanningTripCheckpointDao {

    @Query("SELECT * FROM InPlanningCheckpoint WHERE tripId = :tripId")
    List<InPlanningCheckpoint> getInPlanningCheckpointsByTripId(long tripId);

    @Query("SELECT * FROM InPlanningCheckpoint WHERE checkpointId = :checkpointId")
    InPlanningCheckpoint getInPlanningCheckpointById(long checkpointId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(InPlanningCheckpoint inPlanningCheckpoint);
}

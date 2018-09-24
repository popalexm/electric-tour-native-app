package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface CheckpointsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<Checkpoint> checkpoints);

    @Query("UPDATE Checkpoint SET distanceToNextCheckpoint= :distanceToNext WHERE checkpointId = :checkpointId")
    void updateCheckpointById(int checkpointId, Integer distanceToNext);

    @Query("DELETE FROM Checkpoint")
    int deleteAll();

    @Query("SELECT * FROM Checkpoint")
    Maybe<List<Checkpoint>> getAllCheckpoints();

    @Query("SELECT DISTINCT SUM(distanceToNextCheckpoint) FROM Checkpoint")
    Maybe<Integer> getAllDistances();
}

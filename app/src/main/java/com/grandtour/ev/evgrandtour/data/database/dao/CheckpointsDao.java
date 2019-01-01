package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface CheckpointsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<Checkpoint> checkpoints);

    @Query("UPDATE Checkpoint SET distanceToNextCheckpoint = :distanceToNext , durationToNextCheckpoint = :durationToNextCheckpoint  WHERE checkpointId = :checkpointId")
    void updateCheckpointById(int checkpointId, Integer distanceToNext, Integer durationToNextCheckpoint);

    @Query("DELETE FROM Checkpoint")
    int deleteAll();

    @Query("SELECT * FROM Checkpoint WHERE tourId= :tourId")
    Maybe<List<Checkpoint>> getAllCheckpointsForTourId(String tourId);

    @Query("SELECT * FROM Checkpoint WHERE (checkpointName LIKE :queryText OR checkpointId LIKE :queryText) AND tourId = :tourId")
    Maybe<List<Checkpoint>> getCheckpointsThatMatchQuery(String queryText, String tourId);

    @Query("SELECT * FROM Checkpoint WHERE checkpointId >= :originCheckpointId AND checkpointId BETWEEN :startCheckPointId AND :endCheckpointId LIMIT :maximumCheckpointsToRetrieve")
    Single<List<Checkpoint>> getNextCheckpointsFromOrigin(int originCheckpointId, int maximumCheckpointsToRetrieve, int startCheckPointId, int endCheckpointId);

    @Query("SELECT DISTINCT SUM(distanceToNextCheckpoint) FROM Checkpoint")
    Maybe<Integer> getDistanceForEntireTour();

    @Query("SELECT DISTINCT SUM(durationToNextCheckpoint) FROM Checkpoint")
    Maybe<Integer> getDrivingTimeForEntireTour();

    @Query("SELECT SUM(distanceToNextCheckpoint) FROM Checkpoint WHERE checkpointId BETWEEN :startCheckpointId AND :endCheckPointId")
    Maybe<Integer> getDistanceBetweenTwoCheckpoints(int startCheckpointId, int endCheckPointId);

    @Query("SELECT SUM(durationToNextCheckpoint) FROM Checkpoint WHERE checkpointId BETWEEN :startCheckpointId AND :endCheckPointId")
    Maybe<Integer> getDrivingTimeBetweenTwoCheckpoints(int startCheckpointId, int endCheckPointId);

    @Query("SELECT * FROM Checkpoint WHERE checkpointId BETWEEN :startCheckpointId AND :endCheckPointId")
    Maybe<List<Checkpoint>> getAllCheckpointsBetweenStartAndEndCheckpointIds(int startCheckpointId, int endCheckPointId);
}

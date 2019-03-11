package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;

@Dao
public interface RouteLegDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRouteLeg(@NonNull RouteLeg routeLeg);

    @Query("DELETE FROM RouteLeg")
    int deleteAll();

    @Query("SELECT * FROM RouteLeg WHERE routeId = :routeId")
    List<RouteLeg> getRouteLegsForTourId(int routeId);

    @Query("SELECT * FROM RouteLeg WHERE startCheckpointId BETWEEN :startCheckpointId AND :endCheckpointId")
    Maybe<List<RouteLeg>> getRouteLegsForStartAndEndCheckpoints(int startCheckpointId, int endCheckpointId);

    @Query("SELECT * FROM RouteLeg WHERE startCheckpointId =:startCheckpoint AND endCheckpointId =:endCheckpoint")
    RouteLeg getRouteLegForStartCheckpointAndEndCheckpoint(int startCheckpoint, int endCheckpoint);

}

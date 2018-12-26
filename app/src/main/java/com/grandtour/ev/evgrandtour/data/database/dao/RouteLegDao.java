package com.grandtour.ev.evgrandtour.data.database.dao;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

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

}

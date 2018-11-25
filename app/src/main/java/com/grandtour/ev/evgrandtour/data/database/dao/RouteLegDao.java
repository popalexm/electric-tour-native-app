package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.support.annotation.NonNull;

@Dao
public interface RouteLegDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRouteLeg(@NonNull RouteLeg routeLeg);

}

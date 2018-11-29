package com.grandtour.ev.evgrandtour.data.database.dao;

import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

@Dao
public interface ElevationPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<ElevationPoint> elevationPoints);

}

package com.grandtour.ev.evgrandtour.data.persistence.dao;

import com.grandtour.ev.evgrandtour.data.persistence.models.Checkpoint;

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

    @Query("DELETE FROM Checkpoint")
    int deleteAll();

    @Query("SELECT * FROM Checkpoint")
    Maybe<List<Checkpoint>> getAllCheckpoints();

}

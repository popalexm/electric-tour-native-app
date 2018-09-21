package com.grandtour.ev.evgrandtour.data.persistence;

import com.grandtour.ev.evgrandtour.data.persistence.dao.CheckpointsDao;
import com.grandtour.ev.evgrandtour.data.persistence.dao.RouteDao;
import com.grandtour.ev.evgrandtour.data.persistence.dao.RouteWaypointsDao;
import com.grandtour.ev.evgrandtour.data.persistence.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.persistence.models.Route;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWaypoint;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Checkpoint.class, RouteWaypoint.class, Route.class}, version = 1)
public abstract class LocalStorageManager extends RoomDatabase {

    @NonNull
    private static final String DATABASE_NAME = "grand_tour_database";
    private static LocalStorageManager instance;

    public abstract CheckpointsDao checkpointsDao();

    public abstract RouteWaypointsDao routeWaypointsDao();

    public abstract RouteDao routeDao();

    @NonNull
    public static LocalStorageManager getInstance(@NonNull Context context) {
        if (LocalStorageManager.instance == null) {
            LocalStorageManager.instance = Room.databaseBuilder(context.getApplicationContext(), LocalStorageManager.class, LocalStorageManager.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return LocalStorageManager.instance;
    }
}

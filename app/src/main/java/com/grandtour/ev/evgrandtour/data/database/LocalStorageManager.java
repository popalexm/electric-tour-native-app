package com.grandtour.ev.evgrandtour.data.database;

import com.grandtour.ev.evgrandtour.data.database.dao.CheckpointsDao;
import com.grandtour.ev.evgrandtour.data.database.dao.ElevationPointDao;
import com.grandtour.ev.evgrandtour.data.database.dao.RouteDao;
import com.grandtour.ev.evgrandtour.data.database.dao.RouteLegDao;
import com.grandtour.ev.evgrandtour.data.database.dao.RouteStepDao;
import com.grandtour.ev.evgrandtour.data.database.dao.TourDao;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.data.database.models.Tour;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Checkpoint.class, Route.class, Tour.class, RouteStep.class, RouteLeg.class, ElevationPoint.class}, version = 8)
public abstract class LocalStorageManager extends RoomDatabase {

    @NonNull
    private static final String DATABASE_NAME = "grand_tour_database";
    private static LocalStorageManager instance;

    public abstract TourDao tourDao();

    public abstract CheckpointsDao checkpointsDao();

    public abstract RouteDao routeDao();

    public abstract RouteLegDao routeLegDao();

    public abstract RouteStepDao routeStepDao();

    public abstract ElevationPointDao elevationPointDao();

    @NonNull
    public static LocalStorageManager getInstance(@NonNull Context context) {
        if (LocalStorageManager.instance == null) {
            LocalStorageManager.instance = Room.databaseBuilder(context.getApplicationContext(), LocalStorageManager.class, LocalStorageManager.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return LocalStorageManager.instance;
    }
}

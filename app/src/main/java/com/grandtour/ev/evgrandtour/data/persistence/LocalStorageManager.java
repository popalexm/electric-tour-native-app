package com.grandtour.ev.evgrandtour.data.persistence;

import com.grandtour.ev.evgrandtour.data.persistence.dao.RouteDao;
import com.grandtour.ev.evgrandtour.data.persistence.dao.RouteWaypointsDao;
import com.grandtour.ev.evgrandtour.data.persistence.dao.WaypointsDao;
import com.grandtour.ev.evgrandtour.data.persistence.models.Route;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWaypoint;
import com.grandtour.ev.evgrandtour.data.persistence.models.Waypoint;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Database(entities = {Waypoint.class, RouteWaypoint.class , Route.class}, version = 1)
public abstract class LocalStorageManager extends RoomDatabase {

    @NonNull
    public static final String WAYPOINTS_TABLE_NAME = "waypoints";
    @NonNull
    private static final String DATABASE_NAME = "grand_tour_database";

    @Nullable
    private static LocalStorageManager instance;

    public abstract WaypointsDao waypointsDao();

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

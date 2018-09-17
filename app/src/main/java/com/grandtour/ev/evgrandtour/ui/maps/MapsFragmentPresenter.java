package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.NetworkAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.Route;
import com.grandtour.ev.evgrandtour.data.network.models.response.RoutesResponse;
import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWaypoint;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWithWaypoints;
import com.grandtour.ev.evgrandtour.data.persistence.models.Waypoint;
import com.grandtour.ev.evgrandtour.domain.CalculateRouteUseCase;
import com.grandtour.ev.evgrandtour.domain.DeletePreviousWaypointsUseCase;
import com.grandtour.ev.evgrandtour.domain.DeleteRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.GetAvailableRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.GetAvailableWaypointsUseCase;
import com.grandtour.ev.evgrandtour.domain.SaveRouteToDatabaseUseCase;
import com.grandtour.ev.evgrandtour.domain.SaveWaypointsUseCase;
import com.grandtour.ev.evgrandtour.services.LocationUpdatesService;
import com.grandtour.ev.evgrandtour.ui.utils.DocumentUtils;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MapsFragmentPresenter implements MapsFragmentContract.Presenter {

    private final String TAG = MapsFragmentPresenter.class.getSimpleName();

    @NonNull
    private final MapsFragmentContract.View view;
    private boolean isViewAttached;
    @Nullable
    private LocationUpdatesService locationUpdatesService;
    @Nullable
    private Disposable routesCalculationRequests;
    private boolean areRouteRequestsInProgress;

    @NonNull
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            locationUpdatesService = binder.getService();
            locationUpdatesService.requestLocationUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationUpdatesService = null;
        }
    };

    MapsFragmentPresenter(@NonNull MapsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onAttach() {
        isViewAttached = true;
    }

    @Override
    public void onDetach() {
          isViewAttached = false;
    }

    @Override
    public void onMapReady() {
        if (isViewAttached) {
            if (locationUpdatesService != null) {
                locationUpdatesService.requestLocationUpdates();
            }
            loadAvailableWaypoints();
            loadAvailableRoutes();
        }
    }

    @Override
    public void onStartLocationService(@NonNull Context context) {
        Intent intent = new Intent(context, LocationUpdatesService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStopLocationService(@NonNull Context context) {
        context.unbindService(serviceConnection);
    }

    @Override
    public void onCurrentLocationChanged(@NonNull Location location) {
        if (isViewAttached) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            view.updateCurrentUserLocation(latLng);
        }
    }

    @Override
    public void onLocalFileOpened(@NonNull Uri fileUri) {
        String json = "";
        try {
            json = DocumentUtils.readJSONFromUri(Injection.provideGlobalContext(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
            displayMessage(Injection.provideGlobalContext().getString(R.string.message_error_format_invalid));
        }
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new GsonBuilder().create();
            try {
                Waypoint[] waypoints = gson.fromJson(json, Waypoint[].class);
                List<Waypoint> waypointsFromJson = Arrays.asList(waypoints);
                saveNewWaypoints(waypointsFromJson);
            } catch (JsonSyntaxException e){
                e.printStackTrace();
                displayMessage(Injection.provideGlobalContext().getString(R.string.message_error_opening_file));
            }
        }
    }

    @Override
    public void onClearWaypointsClicked() {
        deleteAllStoredWaypoints();
    }

    @Override
    public void onCalculateRoutesClicked(@NonNull List<Marker> waypoints) {
        if (!areRouteRequestsInProgress) {
            requestDirectionsForWaypoints(waypoints);
        }
    }

    @Override
    public void onStopCalculatingRoutesClicked() {
        if (routesCalculationRequests != null && !routesCalculationRequests.isDisposed()) {
            routesCalculationRequests.dispose();
            areRouteRequestsInProgress = false;
            view.clearMapRoutes();
            view.showLoadingStatus(false, "");
        }
    }


    private void displayMessage(@NonNull String msg){
        if (isViewAttached) {
            view.showMessage(msg);
        }
    }

    private void requestDirectionsForWaypoints(@NonNull List<Marker> waypointList) {
        areRouteRequestsInProgress = true;
        if (isViewAttached) {
            view.showLoadingStatus(true , Injection.provideGlobalContext().getString(R.string.message_calculating_routes));
        }
        NetworkAPI networkAPI = Injection.provideNetworkApi();
        routesCalculationRequests = new CalculateRouteUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), networkAPI, waypointList).perform()
                .doOnComplete(() -> {
                    areRouteRequestsInProgress = false;
                    view.showLoadingStatus(false, "");
                }).subscribe(response -> {
            if (response != null) {
                processDirectionsResponse(response);
            }
        });
    }

    private void processDirectionsResponse(@NonNull Response<RoutesResponse> response){
            RoutesResponse routesResponse = response.body();
            if (routesResponse != null) {
                List<Route> routes = routesResponse.getRoutes();
                if (routes != null && routes.size() > 0) {
                    String poly = routesResponse.getRoutes()
                            .get(0)
                            .getOverviewPolyline()
                            .getPoints();
                    List<LatLng> mapPoints = MapUtils.convertPolyLineToMapPoints(poly);

                    drawRouteFromPoints(mapPoints);
                    saveNewRouteLocally(mapPoints);
                }
          }
    }

    private void saveNewRouteLocally(@NonNull List<LatLng> mapPoints) {
        new SaveRouteToDatabaseUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), mapPoints).perform()
                .subscribe(new SingleObserver<Long[]>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onSuccess(Long[] longs) {
            }

            @Override
            public void onError(Throwable e) { e.printStackTrace(); }
        });
    }

    private void saveNewWaypoints(@NonNull List<Waypoint> waypoints) {
        LocalStorageManager storageManager = Injection.provideStorageManager();
        DeletePreviousWaypointsUseCase deletePreviousWaypointsUseCase = new DeletePreviousWaypointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), storageManager);
        SaveWaypointsUseCase saveWaypointsUseCase = new SaveWaypointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), storageManager, waypoints);
        deletePreviousWaypointsUseCase.perform().andThen(saveWaypointsUseCase.perform())
                .subscribe(new SingleObserver<long[]>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onSuccess(long[] longs) {
                Context ctx = Injection.provideGlobalContext();
                String message = ctx.getString(R.string.format_start_number_end_message, ctx.getString(R.string.message_added), longs.length,
                        ctx.getString(R.string.message_checkpoints));
                displayMessage(message);
                loadAvailableWaypoints();
            }

            @Override
            public void onError(Throwable e) { e.printStackTrace(); }
        });
    }

    private void loadAvailableWaypoints() {
        GetAvailableWaypointsUseCase getAvailableWaypointsUseCase = new GetAvailableWaypointsUseCase(Schedulers.io() , AndroidSchedulers.mainThread() , Injection.provideStorageManager());
        getAvailableWaypointsUseCase.perform().subscribe(new MaybeObserver<List<Waypoint>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onSuccess(List<Waypoint> waypoints) {
                loadWaypointsOnMapView(waypoints);
            }

            @Override
            public void onError(Throwable e) { e.printStackTrace(); }

            @Override
            public void onComplete() { }
        });
    }

    private void loadWaypointsOnMapView(@NonNull List<Waypoint> waypoints) {
        Single.fromCallable(new Callable<List<MarkerOptions>>() {
            @Override
            public List<MarkerOptions> call() {
                return MapUtils.convertWaypointsToMarkers(waypoints);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MarkerOptions>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<MarkerOptions> markerOptions) {
                        if (waypoints.size() > 0) {
                            if (isViewAttached) {
                                view.loadDestinations(markerOptions);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void loadAvailableRoutes() {
        new GetAvailableRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(new MaybeObserver<List<RouteWithWaypoints>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<RouteWithWaypoints> routeWithWaypoints) {
                        for (RouteWithWaypoints route : routeWithWaypoints) {
                            List<RouteWaypoint> routes = route.routeWaypoints;
                            List<LatLng> routeMapPoints = new ArrayList<>();
                            for (RouteWaypoint routeWaypoint : routes) {
                                LatLng routeMapPoint = new LatLng(routeWaypoint.getLat(), routeWaypoint.getLng());
                                routeMapPoints.add(routeMapPoint);
                            }
                            drawRouteFromPoints(routeMapPoints);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void deleteAllStoredWaypoints() {
        DeleteRoutesUseCase deleteRoutesUseCase = new DeleteRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager());
        DeletePreviousWaypointsUseCase deletePreviousWaypointsUseCase = new DeletePreviousWaypointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager());
        deletePreviousWaypointsUseCase.perform()
                .andThen(deleteRoutesUseCase.perform())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onComplete() {
                if (isViewAttached) {
                    view.clearMapWaypoints();
                    view.clearMapRoutes();
                }
            }

            @Override
            public void onError(Throwable e) { e.printStackTrace(); }
        });
    }

    private void drawRouteFromPoints(List<LatLng> routeMapPoints) {
        PolylineOptions routePolyline = MapUtils.generateRoute(routeMapPoints);
        if (isViewAttached) {
            view.drawWaypointRoute(routePolyline);
        }
    }
}

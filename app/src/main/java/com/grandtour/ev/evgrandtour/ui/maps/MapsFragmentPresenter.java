package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.NetworkAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Route;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWaypoint;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWithWaypoints;
import com.grandtour.ev.evgrandtour.domain.CalculateRouteUseCase;
import com.grandtour.ev.evgrandtour.domain.DeleteRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.DeleteStoredCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.GetAvailableRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.LoadCheckpointsFromStorageAsMarkersUseCase;
import com.grandtour.ev.evgrandtour.domain.LoadCheckpointsFromStorageUseCase;
import com.grandtour.ev.evgrandtour.domain.SaveCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.SaveRouteToDatabaseUseCase;
import com.grandtour.ev.evgrandtour.services.LocationUpdatesService;
import com.grandtour.ev.evgrandtour.ui.maps.models.ImportCheckpoint;
import com.grandtour.ev.evgrandtour.ui.utils.DocumentUtils;
import com.grandtour.ev.evgrandtour.ui.utils.JSONParsingUtils;
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
import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
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
            loadAvailableCheckpoints();
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
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                ImportCheckpoint[] checkpoints = gson.fromJson(json, ImportCheckpoint[].class);
                List<ImportCheckpoint> checkPointsFromJson = Arrays.asList(checkpoints);
                List<Checkpoint> toSaveCheckpoints = JSONParsingUtils.processImportedCheckpoints(checkPointsFromJson);
                saveCheckpoints(toSaveCheckpoints);
            } catch (JsonSyntaxException e){
                e.printStackTrace();
                displayMessage(Injection.provideGlobalContext().getString(R.string.message_error_opening_file));
            }
        }
    }

    @Override
    public void onClearCheckpointsClicked() {
        deleteAllCheckpointsAndRoutes();
    }

    @Override
    public void onCalculateRoutesClicked() {
        if (!areRouteRequestsInProgress) {
            requestDirectionsForCheckpoints();
        }
    }

    @Override
    public void onStopCalculatingRoutesClicked() {
        if (routesCalculationRequests != null && !routesCalculationRequests.isDisposed()) {
            routesCalculationRequests.dispose();
            areRouteRequestsInProgress = false;
        }
        if (isViewAttached) {
            view.showLoadingView(false, false, "");
        }
        deletedAllStoredRoutes();
    }


    private void displayMessage(@NonNull String msg){
        if (isViewAttached) {
            view.showMessage(msg);
        }
    }

    private void requestDirectionsForCheckpoints() {
        areRouteRequestsInProgress = true;
        NetworkAPI networkAPI = Injection.provideNetworkApi();
        LocalStorageManager storageManager = Injection.provideStorageManager();
        new LoadCheckpointsFromStorageUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), storageManager).perform()
                .subscribe(new MaybeObserver<List<Checkpoint>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<Checkpoint> checkpoints) {
                        routesCalculationRequests = new CalculateRouteUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), checkpoints, networkAPI,
                                storageManager).perform()
                                .doOnComplete(() -> {
                                    areRouteRequestsInProgress = false;
                                    view.showLoadingView(false, true, "");
                                    loadAvailableCheckpoints();
                                    if (routesCalculationRequests != null) {
                                        routesCalculationRequests.dispose();
                                    }
                                })
                                .doOnSubscribe(subscription -> {
                                    if (isViewAttached) {
                                        view.showLoadingView(true, true, Injection.provideGlobalContext()
                                                .getString(R.string.message_calculating_routes));
                                    }
                                })
                                .subscribe(response -> {
                                    if (response != null) {
                                        processDirectionsResponse(response);
                                    }
                                });
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
                    saveRouteToDatabase(mapPoints);
                }
          }
    }

    private void saveRouteToDatabase(@NonNull List<LatLng> mapPoints) {
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

    private void saveCheckpoints(@NonNull List<Checkpoint> checkpoints) {
        LocalStorageManager storageManager = Injection.provideStorageManager();
        DeleteStoredCheckpointsUseCase deleteStoredCheckpointsUseCase = new DeleteStoredCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                storageManager);
        SaveCheckpointsUseCase saveCheckpointsUseCase = new SaveCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), storageManager,
                checkpoints);
        deleteStoredCheckpointsUseCase.perform()
                .andThen(saveCheckpointsUseCase.perform())
                .subscribe(new SingleObserver<long[]>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onSuccess(long[] longs) {
                Context ctx = Injection.provideGlobalContext();
                String message = ctx.getString(R.string.format_start_number_end_message, ctx.getString(R.string.message_added), longs.length,
                        ctx.getString(R.string.message_checkpoints));
                displayMessage(message);
                loadAvailableCheckpoints();
            }

            @Override
            public void onError(Throwable e) { e.printStackTrace(); }
        });
    }

    private void loadAvailableCheckpoints() {
        new LoadCheckpointsFromStorageAsMarkersUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(new MaybeObserver<List<Pair<Integer, MarkerOptions>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (isViewAttached) {
                            view.showLoadingView(true, false, Injection.provideGlobalContext()
                                    .getString(R.string.message_loading_checkpoints));
                        }
                    }

                    @Override
                    public void onSuccess(List<Pair<Integer, MarkerOptions>> markerOptionsList) {
                        if (isViewAttached) {
                            if (markerOptionsList.size() > 0) {
                                view.loadCheckpoints(markerOptionsList);
                            }
                            view.showLoadingView(false, false, "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (isViewAttached) {
                            view.showLoadingView(false, false, "");
                        }
                    }

                    @Override
                    public void onComplete() {
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

    private void deleteAllCheckpointsAndRoutes() {
        DeleteRoutesUseCase deleteRoutesUseCase = new DeleteRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager());
        DeleteStoredCheckpointsUseCase deleteStoredCheckpointsUseCase = new DeleteStoredCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager());
        deleteStoredCheckpointsUseCase.perform()
                .andThen(deleteRoutesUseCase.perform())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onComplete() {
                if (isViewAttached) {
                    view.clearMapCheckpoints();
                    view.clearMapRoutes();
                }
            }

            @Override
            public void onError(Throwable e) { e.printStackTrace(); }
        });
    }

    private void deletedAllStoredRoutes() {
        new DeleteStoredCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (isViewAttached) {
                            view.clearMapRoutes();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void drawRouteFromPoints(List<LatLng> routeMapPoints) {
        PolylineOptions routePolyline = MapUtils.generateRoute(routeMapPoints);
        if (isViewAttached) {
            view.drawCheckpointsRoute(routePolyline);
        }
    }
}

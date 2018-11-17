package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.SharedPreferencesKeys;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.data.network.NetworkExceptions;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.useCases.CalculateTotalRoutesLengthUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetAvailableRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetFollowingCheckpointsFromOrigin;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadCheckpointMarkersForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadCheckpointsForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.QueryForRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SaveToursDataLocallyUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SetTourSelectionStatusUseCase;
import com.grandtour.ev.evgrandtour.services.LocationsUpdatesService;
import com.grandtour.ev.evgrandtour.services.RouteDirectionsRequestsService;
import com.grandtour.ev.evgrandtour.services.notifications.NotificationManager;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.search.SearchResultViewModel;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;
import com.grandtour.ev.evgrandtour.ui.utils.NetworkUtils;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapsFragmentPresenter extends BasePresenter implements MapsFragmentContract.Presenter, ServiceConnection {

    @NonNull
    private final String TAG = MapsFragmentPresenter.class.getSimpleName();
    @Nullable
    private Service routeDirectionsRequestService;
    @Nullable
    private Service locationUpdatesService;
    @NonNull
    private final ServiceConnection serviceConnection = this;
    private boolean isServiceBound;
    @NonNull
    private final MapsFragmentContract.View view;
    @NonNull
    private final GpsLocationManager gpsLocationManager = GpsLocationManager.getInstance();
    @NonNull
    private List<LatLng> currentSelectedRoutePoints = new ArrayList<>();

    MapsFragmentPresenter(@NonNull MapsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapReady() {
        if (isViewAttached) {
            reloadAvailableCheckpointsAndRoutes();
            if (Injection.provideSharedPreferences()
                    .getBoolean(SharedPreferencesKeys.KEY_LOCATION_TRACKING_ENABLED, false)) {
                startLocationTracking();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(Injection.provideGlobalContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gpsLocationManager.stopRequestingLocationUpdates();
        }
    }

    @Override
    public void onUnBindDirectionsRequestService() {
        if (isServiceBound) {
            Injection.provideGlobalContext()
                    .unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    @Override
    public void onCalculatingRoutesStarted() {
        if (isViewAttached) {
            view.showLoadingView(true);
        }
    }

    @Override
    public void onCalculatingRoutesDone() {
        if (isViewAttached) {
            view.showLoadingView(false);
        }
        reloadAvailableCheckpointsAndRoutes();
    }

    @Override
    public void onRoutesRequestsError(@NonNull String errorType) {
        if (TextUtils.equals(errorType, NetworkExceptions.UNKNOWN_HOST.name())) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.error_message_no_internet_connection));
        }
        if (TextUtils.equals(errorType, NetworkExceptions.STREAM_RESET_EXCEPTION.name())) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.error_message_internet_connection_intrerupted));
        }
        if (isViewAttached) {
            view.showLoadingView(false);
        }
    }

    @Override
    public void onCurrentLocationChanged(@NonNull Location location) {
        if (isViewAttached) {
            LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            view.updateCurrentUserLocation(locationLatLng);
            boolean areDeviationNotificationsEnabled = Injection.provideSharedPreferences()
                    .getBoolean(SharedPreferencesKeys.KEY_ROUTE_DEVIATION_NOTIFICATIONS_ENABLED, false);
            if (currentSelectedRoutePoints.size() > 0 && areDeviationNotificationsEnabled) {
                boolean isLocationOnRoute = PolyUtil.isLocationOnPath(locationLatLng, currentSelectedRoutePoints, true, 1000);
                if (!isLocationOnRoute) {
                    NotificationManager.getInstance()
                            .notifyAboutRouteDeviation();
                }
            }
        }
    }

    @Override
    public void onNavigationClicked(@NonNull Marker originMarker) {
        Integer checkpointId = (Integer) originMarker.getTag();
        if (checkpointId != null) {
            addSubscription(new GetFollowingCheckpointsFromOrigin(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(),
                    checkpointId).perform()
                    .subscribe(checkpoints -> {
                        if (checkpoints.size() != 0) {
                            String navUri = MapUtils.composeUriForMapsIntentRequest(checkpoints);
                            if (isViewAttached) {
                                view.startGoogleMapsDirections(navUri);
                            }
                        }
                    }));
        }
    }

    @Override
    public void onNewRoutesReceived(@NonNull ArrayList<LatLng> routeMapPoints) {
        drawRouteFromMapPoints(routeMapPoints);
    }

    @Override
    public void onCalculateDistanceBetweenTwoCheckpointsClicked() {
        addSubscription(new LoadCheckpointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(checkpoints -> {
                    if (checkpoints.size() != 0) {
                        view.showCalculateDistanceDialog(checkpoints);
                    }
                }));
    }

    @Override
    public void onChooseTourClicked() {
        boolean isNetworkAvailable = NetworkUtils.isInternetConnectionAvailable(Injection.provideGlobalContext());
        if (isNetworkAvailable){
            view.showTourPickerDialog();
        } else {
            String errorMsg = Injection.provideGlobalContext().getString(R.string.message_no_internet_reloading_saved_tour);
            displayShortMessage(errorMsg);
            reloadAvailableCheckpointsAndRoutes();
        }
    }

    @Override
    public void onTourSelected(@NonNull String tourId, @NonNull List<TourDataResponse> tourDataResponses) {
        addSubscription(new SaveToursDataLocallyUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager(), tourDataResponses)
                .perform()
                .doOnError(Throwable::printStackTrace)
                .subscribe(() -> addSubscription(new SetTourSelectionStatusUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), tourId).perform()
                        .doOnComplete(this::startRouteDirectionsRequests)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe())));
    }

    @Override
    public void onNewSearchQuery(@NonNull String text) {
        addSubscription(
                new QueryForRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), text.toLowerCase()).perform()
                        .doOnSuccess(checkpoints -> {
                            view.displaySearchResults(generateSearchResults(checkpoints));
                        })
                        .subscribe());
    }



    @Override
    public void onSearchQueryCleared() {
        view.clearSearchResults();
    }

    @Override
    public void onSettingsClicked() {
        if (isViewAttached) {
            view.showSettingsDialog();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Connected to service " + name.getClassName());
        if (name.getClassName()
                .equals(LocationsUpdatesService.class.getName())) {
            LocationsUpdatesService.LocationServiceBinder binder = (LocationsUpdatesService.LocationServiceBinder) service;
            locationUpdatesService = binder.getService();
        } else if (name.getClassName()
                .equals(RouteDirectionsRequestsService.class.getName())) {
            RouteDirectionsRequestsService.RouteDirectionsLocalBinder binder = (RouteDirectionsRequestsService.RouteDirectionsLocalBinder) service;
            routeDirectionsRequestService = binder.getService();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Disconnected from service " + name.getClassName());
        if (name.getClassName()
                .equals(LocationsUpdatesService.class.getName())) {
            locationUpdatesService = null;
        } else if (name.getClassName()
                .equals(RouteDirectionsRequestsService.class.getName())) {
            routeDirectionsRequestService = null;
        }
    }

    @Override
    public void OnSearchResultClicked(@NonNull Integer checkpointId) {
        view.hideSoftKeyboard();
        view.moveToMarker(checkpointId);
        view.clearSearchResults();
    }

    @Override
    public void onLocationTrackingSettingsUpdate(boolean isLocationTrackingEnabled) {
        if (isLocationTrackingEnabled) {
            startLocationTracking();
        } else {
            stopLocationTracking();
        }
    }

    private void displayShortMessage(@NonNull String msg) {
        if (isViewAttached) {
            view.showMessage(msg);
        }
    }

    private void startRouteDirectionsRequests() {
        Context context = Injection.provideGlobalContext();
        Intent serviceIntent = new Intent(context, RouteDirectionsRequestsService.class);
        context.startService(serviceIntent);
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        isServiceBound = true;
    }

    private void startLocationTracking() {
        Context context = Injection.provideGlobalContext();
        Intent serviceIntent = new Intent(context, LocationsUpdatesService.class);
        context.startService(serviceIntent);
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        isServiceBound = true;
    }

    private void stopLocationTracking() {
        if (locationUpdatesService != null) {
            locationUpdatesService.stopSelf();
        }
    }

    private void reloadAvailableCheckpointsAndRoutes() {
        if (isViewAttached) {
            view.showLoadingView(true);
            view.clearMapCheckpoints();
            view.clearMapRoutes();
        }
        Maybe<List<Route>> getAvailableRoutes = new GetAvailableRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager()).perform()
                .doOnSuccess(routes -> {
                    for (Route route : routes) {
                        currentSelectedRoutePoints = MapUtils.convertPolyLineToMapPoints(route.getRoutePolyline());
                        drawRouteFromMapPoints(currentSelectedRoutePoints);
                    }
                })
                .doOnError(Throwable::printStackTrace);

        Maybe<List<Pair<Integer, MarkerOptions>>> getAvailableCheckpoints = new LoadCheckpointMarkersForSelectedTourUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .doOnSuccess(checkpoints -> {
                    if (isViewAttached) {
                        if (checkpoints.size() > 0) {
                            view.loadCheckpoints(checkpoints);
                            view.centerMapToCurrentSelectedRoute();
                        }
                    }
                })
                .doOnError(Throwable::printStackTrace);

        Maybe.concat(getAvailableCheckpoints, getAvailableRoutes)
                .doOnComplete(() -> {
                    if (isViewAttached) {
                        view.showLoadingView(false);
                    }
                })
                .subscribe();

        addSubscription(new CalculateTotalRoutesLengthUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager())
                .perform()
                .doOnComplete(() -> {
                    view.showTotalRouteInformation(Injection.provideGlobalContext()
                            .getString(R.string.title_no_tour_selected), true);
                })
                .subscribe(distanceDurationPair -> {
                    String infoMessage = MapUtils.generateInfoMessage(distanceDurationPair);
                    view.showTotalRouteInformation(infoMessage, true);

                }, Throwable::printStackTrace));
    }

    private void drawRouteFromMapPoints(@NonNull List<LatLng> routeMapPoints) {
        PolylineOptions routePolyline = MapUtils.generateRoute(routeMapPoints);
        if (isViewAttached) {
            view.drawCheckpointsRoute(routePolyline);
        }
    }

    private List<SearchResultViewModel> generateSearchResults(@NonNull Iterable<Checkpoint> checkpoints) {
        List<SearchResultViewModel> searchResultViewModels = new ArrayList<>();
        for (Checkpoint details : checkpoints) {
            SearchResultViewModel viewModel = new SearchResultViewModel(details.getCheckpointId(), String.valueOf(details.getOrderInTourId()),
                    details.getCheckpointName(), this);
            searchResultViewModels.add(viewModel);
        }
        return searchResultViewModels;
    }
}
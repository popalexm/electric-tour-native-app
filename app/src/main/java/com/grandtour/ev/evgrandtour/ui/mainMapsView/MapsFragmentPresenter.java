package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.SharedPreferencesKeys;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.data.network.NetworkExceptions;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.services.DirectionsElevationService;
import com.grandtour.ev.evgrandtour.domain.services.LocationsUpdatesService;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadElevationPointsForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadMapCheckpointForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadMapCheckpointsForFilteredCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadNextCheckpointsFromOriginPoint;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadRouteInformationUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadRouteLegsAndStepsForBetweenCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadRouteLegsAndStepsForEntireTripUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.QueryForCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SaveToursDataLocallyUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SetTourSelectionStatusUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.chartView.ChartDataCreatedListener;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.chartView.ChartViewDataHandler;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.SearchResultModel;
import com.grandtour.ev.evgrandtour.ui.notifications.NotificationManager;
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
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapsFragmentPresenter extends BasePresenter
        implements MapsFragmentContract.Presenter, ServiceConnection, OnSuccessListener<Location>, ChartDataCreatedListener {

    @NonNull
    private final String TAG = MapsFragmentPresenter.class.getSimpleName();
    private final int NAVIGATION_CHECKPOINTS_SIZE = 11;

    @NonNull
    private final ServiceConnection serviceConnection = this;
    @NonNull
    private final MapsFragmentContract.View view;
    @NonNull
    private final GpsLocationManager gpsLocationManager = GpsLocationManager.getInstance();
    @Nullable
    private Service routeDirectionsRequestService;
    @Nullable
    private Service locationUpdatesService;
    private boolean isServiceBound;
    @NonNull
    private final List<LatLng> currentSelectedRoutePoints = new ArrayList<>();
    @NonNull
    private final List<Checkpoint> navigationPathWayPoints = new ArrayList<>();
    @NonNull
    private final ArrayList<MapCheckpoint> displayedTripCheckpoints = new ArrayList<>();


    MapsFragmentPresenter(@NonNull MapsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapReady() {
        if (isViewAttached) {
            loadEntireTourDataOnMap();
            if (Injection.provideSharedPreferences()
                    .getBoolean(SharedPreferencesKeys.KEY_LOCATION_TRACKING_ENABLED, false)) {
                startLocationTracking();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        displayedTripCheckpoints.clear();
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
        dismissLoadingView();
        loadEntireTourDataOnMap();
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
        dismissLoadingView();
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
    public void onNavigationClicked() {
        if (navigationPathWayPoints.size() != 0) {
            String navUri = MapUtils.composeUriForMapsIntentRequest(navigationPathWayPoints);
            if (isViewAttached) {
                view.startGoogleMapsDirections(navUri);
            }
        }
    }

    @Override
    public void onChooseTourClicked() {
        boolean isNetworkAvailable = NetworkUtils.isInternetConnectionAvailable(Injection.provideGlobalContext());
        if (isNetworkAvailable) {
            view.showTourPickerDialog();
        } else {
            String errorMsg = Injection.provideGlobalContext()
                    .getString(R.string.message_no_internet_reloading_saved_tour);
            displayShortMessage(errorMsg);
            loadEntireTourDataOnMap();
        }
    }

    @Override
    public void onTourSelected(@NonNull String tourId, @NonNull List<TourDataResponse> tourDataResponses) {
        addSubscription(
                new SaveToursDataLocallyUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), tourDataResponses).perform()
                        .doOnError(Throwable::printStackTrace)
                        .subscribe(() -> addSubscription(
                                new SetTourSelectionStatusUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(),
                                        tourId).perform()
                                        .doOnComplete(this::startRouteDirectionsRequests)
                                        .doOnError(Throwable::printStackTrace)
                                        .subscribe())));
    }

    @Override
    public void onNewSearchQuery(@NonNull String text) {
        addSubscription(new QueryForCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), displayedTripCheckpoints, text.toLowerCase()).perform()
                        .doOnSuccess(checkpoints -> {
                            view.displaySearchResults(generateSearchResults(checkpoints));
                        })
                        .doOnError(Throwable::printStackTrace)
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
    public void onFilterButtonClicked() {
        if (isViewAttached) {
            view.showFilteringOptionsView();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (name.getClassName()
                .equals(LocationsUpdatesService.class.getName())) {
            LocationsUpdatesService.LocationServiceBinder binder = (LocationsUpdatesService.LocationServiceBinder) service;
            locationUpdatesService = binder.getService();
        } else if (name.getClassName()
                .equals(DirectionsElevationService.class.getName())) {
            DirectionsElevationService.RouteDirectionsLocalBinder binder = (DirectionsElevationService.RouteDirectionsLocalBinder) service;
            routeDirectionsRequestService = binder.getService();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (name.getClassName()
                .equals(LocationsUpdatesService.class.getName())) {
            locationUpdatesService = null;
        } else if (name.getClassName()
                .equals(DirectionsElevationService.class.getName())) {
            routeDirectionsRequestService = null;
        }
    }

    @Override
    public void OnSearchResultClicked(@NonNull Integer checkpointId) {
        view.hideSoftKeyboard();
        for (MapCheckpoint checkpoint : displayedTripCheckpoints) {
            Integer checkpointIdFromDisplayedPoint = checkpoint.getMapCheckpointId();
            if (checkpointId.equals(checkpointIdFromDisplayedPoint)) {
                view.moveCameraToCurrentLocation(checkpoint.getPosition());
            }
        }
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

    @Override
    public void onSelectedCheckpointRouteFilters(@NonNull List<MapCheckpoint> toFilterRouteByCheckpoints) {
        int startCheckpointId = toFilterRouteByCheckpoints.get(0)
                .getMapCheckpointId();
        int endCheckpointId = toFilterRouteByCheckpoints.get(1)
                .getMapCheckpointId();

        if (startCheckpointId < endCheckpointId) {
            loadTourDataBetweenTwoCheckpointsOfARoute(startCheckpointId, endCheckpointId);
        } else {
            // If a user selects 2 checkpoints in reverse , switch then for the loading logic / database query to work
            loadTourDataBetweenTwoCheckpointsOfARoute(endCheckpointId, startCheckpointId);
        }
    }

    @Override
    public void onClearFilteredRouteClicked() {
        view.clearFilteringChipsSelectionStatus();
        loadEntireTourDataOnMap();
    }

    @Override
    public void onFilterChipSelectionRemoved() {
        loadEntireTourDataOnMap();
    }

    @Override
    public void onMyLocationButtonClicked() {
        if (ActivityCompat.checkSelfPermission(Injection.provideGlobalContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gpsLocationManager.getLastKnownLocation(this);
        }
    }

    @Override
    public void onMarkerClicked(int markerCheckpointId) {
        List<MapCheckpoint> mapCheckpoints = displayedTripCheckpoints;
        int startCheckpointId = mapCheckpoints.get(0)
                .getMapCheckpointId();
        int endCheckpointId = mapCheckpoints.get(mapCheckpoints.size() - 1)
                .getMapCheckpointId();
        addSubscription(
                new LoadNextCheckpointsFromOriginPoint(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), markerCheckpointId,
                        NAVIGATION_CHECKPOINTS_SIZE, startCheckpointId, endCheckpointId).perform()
                        .doOnSuccess(navigationPathData -> {
                            if (navigationPathData != null) {
                                if (isViewAttached) {
                                    view.highLightNavigationPath(navigationPathData.getNavigationPathRouteLegs());
                                    view.showSelectTripButton(false);
                                    view.showNavigationButton(true);
                                }
                                navigationPathWayPoints.addAll(navigationPathData.getNavigationPathWayPoints());
                            }
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    @Override
    public void onMarkerInfoWindowClosed() {
        if (isViewAttached) {
            navigationPathWayPoints.clear();
            view.clearAllHighlightedPaths();
            view.showNavigationButton(false);
            view.showSelectTripButton(true);
        }
    }

    private void displayShortMessage(@NonNull String msg) {
        if (isViewAttached) {
            view.showMessage(msg);
        }
    }

    private void startRouteDirectionsRequests() {
        Context context = Injection.provideGlobalContext();
        Intent serviceIntent = new Intent(context, DirectionsElevationService.class);
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

    /**
     * Loads tour data that is filtered between 2 checkpoints ids, showing only the route polyline and elevation data
     * between the 2
     */
    private void loadTourDataBetweenTwoCheckpointsOfARoute(int startCheckpointId, int endCheckpointId) {

        Maybe<List<MapCheckpoint>> getAllMapCheckpointsBetweenFilterCheckpoints = new LoadMapCheckpointsForFilteredCheckpointsUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager(), startCheckpointId, endCheckpointId).perform()
                .doOnSuccess(this::loadMapCheckpointsOnMapView);

        Maybe<List<Pair<RouteLeg, List<RouteStep>>>> getRouteLegsAndStepsBetweenFilterCheckpoints = new LoadRouteLegsAndStepsForBetweenCheckpointsUseCase(
                Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), startCheckpointId, endCheckpointId).perform()
                .doOnSuccess(this::loadRoutePolylineOnMapView);

        Maybe<Pair<Pair<Integer, Integer>, String>> getRouteInformationUseCase = new LoadRouteInformationUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager(), startCheckpointId, endCheckpointId).perform()
                .doOnComplete(this::displayNoRouteSelectedWarning)
                .doOnSuccess(routeInformation -> {
                    Pair<Integer, Integer> routeDistanceDurationPair = routeInformation.first;
                    Pair<String, String> formattedRouteInfo = MapUtils.generateInfoMessage(routeDistanceDurationPair);
                    String routeTitle = routeInformation.second;
                    displayRouteInformation(formattedRouteInfo, routeTitle);
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                });

        Maybe<List<ElevationPoint>> getElevationPointsUseCase = new LoadElevationPointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager(), startCheckpointId, endCheckpointId).perform()
                .doOnSuccess(this::createChartViewEntryData)
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                });

        addSubscription(Maybe.concat(getAllMapCheckpointsBetweenFilterCheckpoints, getRouteLegsAndStepsBetweenFilterCheckpoints, getRouteInformationUseCase,
                getElevationPointsUseCase)
                .doOnComplete(this::dismissLoadingView)
                .doOnSubscribe(subscription -> clearMapAndDisplayLoadingProgressBar())
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                })
                .subscribe());
    }

    /**
     * Loads entire tour with associated checkpoints and polyline extracted from the RouteSteps and Legs that form the route
     */
    private void loadEntireTourDataOnMap() {

        Maybe<List<Pair<RouteLeg, List<RouteStep>>>> getAvailableRoutesStepsUseCase = new LoadRouteLegsAndStepsForEntireTripUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .doOnSuccess(this::loadRoutePolylineOnMapView)
                .doOnError(Throwable::printStackTrace);

        Maybe<List<MapCheckpoint>> getAvailableCheckpointsUseCase = new LoadMapCheckpointForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager()).perform()
                .doOnSuccess(mapCheckpoints -> {
                    loadMapCheckpointsOnMapView(mapCheckpoints);
                    loadFilteringOptions(mapCheckpoints);
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                });

        Maybe<Pair<Pair<Integer, Integer>, String>> getRouteInformationUseCase = new LoadRouteInformationUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager(), null, null).perform()
                .doOnComplete(this::displayNoRouteSelectedWarning)
                .doOnSuccess(routeInformation -> {
                    Pair<Integer, Integer> routeDistanceDuration = routeInformation.first;
                    Pair<String, String> formattedRouteDistanceDurationPair = MapUtils.generateInfoMessage(routeDistanceDuration);
                    String routeTitle = routeInformation.second;
                    displayRouteInformation(formattedRouteDistanceDurationPair, routeTitle);
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                });

        Maybe<List<ElevationPoint>> getElevationPointsUseCase = new LoadElevationPointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager(), null, null).perform()
                .doOnSuccess(this::createChartViewEntryData)
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                });

        addSubscription(Maybe.concat(getAvailableCheckpointsUseCase, getAvailableRoutesStepsUseCase, getRouteInformationUseCase, getElevationPointsUseCase)
                .doOnComplete(this::dismissLoadingView)
                .doOnSubscribe(subscription -> clearMapAndDisplayLoadingProgressBar())
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                })
                .subscribe());
    }

    private void displayRouteInformation(@NonNull Pair<String, String> formattedRouteInfo, @NonNull String routeTitle) {
        if (isViewAttached) {
            view.showTotalRouteInformation(formattedRouteInfo.first, formattedRouteInfo.second, routeTitle);
        }
    }

    private void displayNoRouteSelectedWarning() {
        if (isViewAttached) {
            Context context = Injection.provideGlobalContext();
            String noInfoAvailable = context.getString(R.string.message_no_information_available);
            view.showTotalRouteInformation(noInfoAvailable, noInfoAvailable, context.getString(R.string.title_no_tour_selected));
            view.animateRouteSelectionButton();
            view.animateRouteInformationText();
        }
    }

    private void clearMapAndDisplayLoadingProgressBar() {
        if (isViewAttached) {
            view.showLoadingView(true);
            // Clear stored map checkpoints in the Presenter object
            displayedTripCheckpoints.clear();
            // Clear markers and routes from map
            view.clearMapCheckpoints();
            view.clearMapRoutes();
        }
    }

    private void loadMapCheckpointsOnMapView(@NonNull List<MapCheckpoint> mapCheckpoints) {
        if (isViewAttached && mapCheckpoints.size() > 0) {
            // Clear previous map points
            displayedTripCheckpoints.clear();
            displayedTripCheckpoints.addAll(mapCheckpoints);
            // Load new trip checkpoints and center to route
            view.loadCheckpointsOnMapView(mapCheckpoints);
            view.centerMapToCurrentSelectedRoute(mapCheckpoints);
        }
    }

    private void loadFilteringOptions(@NonNull List<MapCheckpoint> mapCheckpoints) {
        if (isViewAttached) {
            view.loadAvailableFilterPoints(mapCheckpoints);
        }
    }

    private void loadRoutePolylineOnMapView(@NonNull Iterable<Pair<RouteLeg, List<RouteStep>>> routeLegsStepsList) {
        if (isViewAttached) {
            for (Pair<RouteLeg, List<RouteStep>> routeLegStepsPair : routeLegsStepsList) {
                List<RouteStep> steps = routeLegStepsPair.second;
                int routeLegId = routeLegStepsPair.first.getRouteLegId();
                List<LatLng> routeLegPolylinePoints = new ArrayList<>();
                for (RouteStep routeStep : steps) {
                    List<LatLng> stepLinePoints = MapUtils.convertPolyLineToMapPoints(routeStep.getRouteStepPolyline());
                    routeLegPolylinePoints.addAll(stepLinePoints);
                }
                drawRouteStepFromMapPoints(routeLegPolylinePoints, routeLegId);
                currentSelectedRoutePoints.addAll(routeLegPolylinePoints);
            }
        }
    }

    private void dismissLoadingView() {
        if (isViewAttached) {
            view.showLoadingView(false);
        }
    }

    private void createChartViewEntryData(@NonNull List<ElevationPoint> elevationPoints) {
        new ChartViewDataHandler(elevationPoints, this);
    }

    private void drawRouteStepFromMapPoints(@NonNull Iterable<LatLng> routeMapPoints, int routeStepId) {
        PolylineOptions routePolyline = MapUtils.generateRoute(routeMapPoints);
        if (isViewAttached) {
            view.drawRouteStepLineOnMap(routePolyline, routeStepId);
        }
    }

    @NonNull
    private List<SearchResultModel> generateSearchResults(@NonNull Iterable<MapCheckpoint> checkpoints) {
        List<SearchResultModel> searchResultModels = new ArrayList<>();
        for (MapCheckpoint details : checkpoints) {
            SearchResultModel searchResultModel = new SearchResultModel(details.getMapCheckpointId(), String.valueOf(details.getOrderInRouteId()),
                    details.getMapCheckpointTitle(), this);
            searchResultModels.add(searchResultModel);
        }
        return searchResultModels;
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            view.moveCameraToCurrentLocation(currentLocation);
        }
    }

    @Override
    public void OnChartDataCreated(@NonNull LineData lineData, @NonNull Description description) {
        if (isViewAttached) {
            view.showChartView(lineData, description);
        }
    }
}

package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import com.grandtour.ev.evgrandtour.domain.useCases.GetAvailableRouteLegsAndStepsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetFollowingCheckpointsFromOrigin;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadElevationPointsForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadMapCheckpointForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadMapCheckpointsForFilteredCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadRouteInformationUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadRouteLegsAndStepsForFilteredCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.QueryForRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SaveToursDataLocallyUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SetTourSelectionStatusUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.search.SearchResultViewModel;
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
import android.graphics.Color;
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

public class MapsFragmentPresenter extends BasePresenter implements MapsFragmentContract.Presenter, ServiceConnection {

    @NonNull
    private final String TAG = MapsFragmentPresenter.class.getSimpleName();
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
    private List<LatLng> currentSelectedRoutePoints = new ArrayList<>();

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
    public void onNavigationClicked(@NonNull MapCheckpoint originMarker) {
        Integer checkpointId = originMarker.getMapCheckpointId();
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

    @Override
    public void onPolylineClicked(Integer routeLegId) {
        view.showElevationChartForRouteLegDialog(routeLegId);
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
        clearMapAndDisplayLoadingProgressBar();

        Maybe<List<MapCheckpoint>> getAllMapCheckpointsBetweenFilterCheckpoints = new LoadMapCheckpointsForFilteredCheckpointsUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager(), startCheckpointId, endCheckpointId).perform()
                .doOnSuccess(this::loadMapCheckpointsOnMapView);

        Maybe<List<Pair<RouteLeg, List<RouteStep>>>> getRouteLegsAndStepsBetweenFilterCheckpoints = new LoadRouteLegsAndStepsForFilteredCheckpointsUseCase(
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

        Maybe<List<ElevationPoint>> elevationPointsUseCase = new LoadElevationPointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager(), startCheckpointId, endCheckpointId).perform()
                .doOnSuccess(this::createChartViewEntryData)
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                });

        addSubscription(Maybe.concat(getAllMapCheckpointsBetweenFilterCheckpoints, getRouteLegsAndStepsBetweenFilterCheckpoints, getRouteInformationUseCase,
                elevationPointsUseCase)
                .doOnComplete(this::dismissLoadingView)
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
        clearMapAndDisplayLoadingProgressBar();

        Maybe<List<Pair<RouteLeg, List<RouteStep>>>> getAvailableRoutesStepsUseCase = new GetAvailableRouteLegsAndStepsUseCase(Schedulers.io(),
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

        Maybe<Pair<Pair<Integer, Integer>, String>> loadRouteInformationUseCase = new LoadRouteInformationUseCase(Schedulers.io(),
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

        Maybe<List<ElevationPoint>> elevationPointsUseCase = new LoadElevationPointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager(), null, null).perform()
                .doOnSuccess(this::createChartViewEntryData)
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingView();
                });

        addSubscription(Maybe.concat(getAvailableCheckpointsUseCase, getAvailableRoutesStepsUseCase, loadRouteInformationUseCase, elevationPointsUseCase)
                .doOnComplete(this::dismissLoadingView)
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
            view.showTotalRouteInformation(context.getString(R.string.title_no_tour_selected), noInfoAvailable, noInfoAvailable);
            view.animateRouteSelectionButton();
            view.animateRouteInformationText();
        }
    }

    private void clearMapAndDisplayLoadingProgressBar() {
        if (isViewAttached) {
            view.showLoadingView(true);
            view.clearMapCheckpoints();
            view.clearMapRoutes();
        }
    }

    private void loadMapCheckpointsOnMapView(@NonNull List<MapCheckpoint> mapCheckpoints) {
        if (isViewAttached && mapCheckpoints.size() > 0) {
            view.loadCheckpointsOnMapView(mapCheckpoints);
            view.centerMapToCurrentSelectedRoute();
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
                List<LatLng> routeLegPolyline = new ArrayList<>();
                for (RouteStep routeStep : steps) {
                    List<LatLng> stepLinePoints = MapUtils.convertPolyLineToMapPoints(routeStep.getRouteStepPolyline());
                    routeLegPolyline.addAll(stepLinePoints);
                }
                drawRouteStepFromMapPoints(routeLegPolyline, routeLegId);
                currentSelectedRoutePoints.addAll(routeLegPolyline);
            }
        }
    }

    private void dismissLoadingView() {
        if (isViewAttached) {
            view.showLoadingView(false);
        }
    }

    private void createChartViewEntryData(@NonNull List<ElevationPoint> elevationPoints) {
        if (isViewAttached) {
            List<Entry> elevationPointEntries = new ArrayList<>();
            for (int i = 0; i < elevationPoints.size(); i++) {
                ElevationPoint elevationPoint = elevationPoints.get(i);
                int startCheckpointId = elevationPoint.getStartCheckpointOrderId();
                elevationPointEntries.add(new Entry(startCheckpointId, (float) elevationPoint.getElevation()));
            }
            prepareChartViewData(elevationPointEntries);
        }
    }

    // TODO Delegate this to a handler class that prepared data for chart view
    private void prepareChartViewData(@NonNull List<Entry> elevationPointsList) {
        if (elevationPointsList.size() > 0) {
            int labelColor = Injection.provideGlobalContext()
                    .getResources()
                    .getColor(R.color.colorLightGrey);
            int accentColor = Injection.provideGlobalContext()
                    .getResources()
                    .getColor(R.color.colorAccent);

            String lineLabel = Injection.provideGlobalContext()
                    .getResources()
                    .getString(R.string.label_line_chart);
            String lineChartDescription = Injection.provideGlobalContext()
                    .getResources()
                    .getString(R.string.label_line_chart_values);

            LineDataSet dataSet = new LineDataSet(elevationPointsList, lineLabel);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setColor(accentColor);
            dataSet.setCircleColor(accentColor);

            dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSet.setDrawCircles(false);

            LineData lineData = new LineData(dataSet);

            Description description = new Description();
            description.setText(lineChartDescription);
            description.setTextColor(labelColor);
            if (isViewAttached) {
                view.showChartView(lineData, description);
            }
        }
    }

    private void drawRouteStepFromMapPoints(@NonNull List<LatLng> routeMapPoints, int routeStepId) {
        PolylineOptions routePolyline = MapUtils.generateRoute(routeMapPoints);
        if (isViewAttached) {
            view.drawRouteStepLineOnMap(routePolyline, routeStepId);
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

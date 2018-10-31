package com.grandtour.ev.evgrandtour.services;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.NetworkExceptions;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Route;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.domain.useCases.CalculateRouteUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadCheckpointsForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SaveRouteToDatabaseUseCase;
import com.grandtour.ev.evgrandtour.services.notifications.NotificationsUtils;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.http2.StreamResetException;

public class RouteDirectionsRequestsService extends Service {

    private static final int ROUTE_REQUESTS_NOTIFICATION_ID = 12;
    @NonNull
    public static final String ROUTE_MAP_POINTS_BUNDLE = "routeMapPointsParcelable";
    @NonNull
    public static final String ROUTE_START_REQUESTS_BUNDLE = "routeDirectionsRequestsStart";
    @NonNull
    public static final String REQUEST_ERROR_CODE = "requestErrorCode";
    @NonNull
    public static final String ACTION_ROUTE_BROADCAST = "RouteResultsBroadcast";
    @NonNull
    private final IBinder localBinder = new RouteDirectionsLocalBinder();
    @Nullable
    private Disposable directionsRequestsDisposable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestDirectionsForAvailableCheckpoints();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        return localBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Notification notification = NotificationsUtils.createNotification(this, getString(R.string.message_route_calculations_running_in_background),
                getString(R.string.app_name));
        startForeground(RouteDirectionsRequestsService.ROUTE_REQUESTS_NOTIFICATION_ID, notification);
        return true;
    }

    private void requestDirectionsForAvailableCheckpoints() {
        Disposable disposable = new LoadCheckpointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager()).perform()
                .subscribe(this::startRouteDirectionsRequests);
    }

    private void startRouteDirectionsRequests(@NonNull List<Checkpoint> checkpoints) {
        directionsRequestsDisposable = new CalculateRouteUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), checkpoints, Injection.provideDirectionsApi(),
                Injection.provideStorageManager()).perform()
                .doOnComplete(() -> {
                    broadcastDirectionRequestProgress(false);
                    if (directionsRequestsDisposable != null) {
                        directionsRequestsDisposable.dispose();
                    }
                    stopSelf();
                })
                .doOnSubscribe(subscription -> {
                    broadcastDirectionRequestProgress(true);
                })
                .doOnError(throwable -> {
                    if (throwable instanceof UnknownHostException) {
                        broadcastRequestError(NetworkExceptions.UNKNOWN_HOST);
                    } else if (throwable instanceof StreamResetException) {
                        broadcastRequestError(NetworkExceptions.STREAM_RESET_EXCEPTION);
                    }
                    stopSelf();
                })
                .subscribe(response -> {
                    RoutesResponse routesResponse = response.body();
                    if (routesResponse != null) {
                        List<Route> routes = routesResponse.getRoutes();
                        if (routes != null && routes.size() > 0) {
                            String poly = routesResponse.getRoutes()
                                    .get(0)
                                    .getOverviewPolyline()
                                    .getPoints();
                            RouteDirectionsRequestsService.saveRouteToDatabase(poly);
                        }
                    }
                });
    }

    private static void saveRouteToDatabase(@NonNull String routePolyline) {
        new SaveRouteToDatabaseUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), routePolyline).perform()
                .subscribe();
    }

    private void broadcastDirectionRequestProgress(boolean areDirectionsRequestsInProgress) {
        Intent intent = new Intent(RouteDirectionsRequestsService.ACTION_ROUTE_BROADCAST);
        intent.putExtra(RouteDirectionsRequestsService.ROUTE_START_REQUESTS_BUNDLE, areDirectionsRequestsInProgress);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    private void broadcastRequestError(@NonNull NetworkExceptions exceptions) {
        Intent intent = new Intent(RouteDirectionsRequestsService.ACTION_ROUTE_BROADCAST);
        intent.putExtra(RouteDirectionsRequestsService.REQUEST_ERROR_CODE, exceptions.name());
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    public class RouteDirectionsLocalBinder extends Binder {

        public RouteDirectionsRequestsService getService() {
            return RouteDirectionsRequestsService.this;
        }
    }
}

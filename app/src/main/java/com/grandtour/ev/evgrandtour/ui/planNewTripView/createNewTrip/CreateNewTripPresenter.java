package com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.models.request.AddInPlanningTripRequest;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateNewTripPresenter extends BasePresenter implements CreateNewTripContract.Presenter {

    @NonNull
    private final CreateNewTripContract.View view;

    CreateNewTripPresenter(@NonNull CreateNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onCheckForPreviousInPlanningTrip(){
        addSubscription(Injection.provideCloudApi()
                .getCurrentInPlanningTripForUser(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                          if(isViewAttached()){
                              if (response.code() == 200 && response.body() != null) {
                                  onPreviousInPlanningTrip(response.body().getTripId());
                              }
                          }
                        }, Throwable::printStackTrace
                ));
    }

    @Override
    public void onNextPressed(@NonNull String newTripName, @NonNull String newTripDescription) {
        if (TextUtils.isEmpty(newTripName)) {
            if (isViewAttached()) {
                view.displayErrorOnTripNameField(Injection.provideGlobalContext()
                        .getString(R.string.error_trip_name_is_mandatory));
            }
        } else {
            if (isViewAttached) {
                view.removeErrorOnTripNameField();
            }
            startAddTripRequest(createInPlanningTripRequest(newTripName, newTripDescription));
        }
    }

    private void startAddTripRequest(@NonNull AddInPlanningTripRequest request) {
        addSubscription(Injection.provideCloudApi()
                .postPlannedTrip(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() == 200 && response.body() != null) {
                        onTripPlannedSuccessfully(response.body());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    onTripRequestFailed();
                }));
    }

    private void onPreviousInPlanningTrip(@NonNull Integer tripId){
        if(isViewAttached){
            view.moveToTripCheckpointsPlanningScreen(tripId);
        }
    }

    private void onTripPlannedSuccessfully(@NonNull Integer tripId) {
        if (isViewAttached()) {
            view.moveToTripCheckpointsPlanningScreen(tripId);
        }
    }

    private void onTripRequestFailed() {
        if (isViewAttached()) {
            view.showMessage(Injection.provideGlobalContext().getResources().getString(R.string.error_while_creating_your_trip));
        }
    }

    @NonNull
    private AddInPlanningTripRequest createInPlanningTripRequest(@NonNull String newTripName, @NonNull String newTripDescription){
        AddInPlanningTripRequest request = new AddInPlanningTripRequest();
        request.setUserId(1);
        request.setTripName(newTripName);
        request.setTripDescription(newTripDescription);
        return request;
    }

}

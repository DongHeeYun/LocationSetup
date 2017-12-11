package com.locationsetup;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE;
import static com.google.android.gms.location.GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES;
import static com.google.android.gms.location.GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS;

/**
 * Created by sky on 2017-12-11.
 */

public class GeofenceManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = GeofenceManager.class.getSimpleName();

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100; // 1 mile, 1.6 km

    protected GoogleApiClient mGoogleApiClient;
    protected List<Geofence> mGeofenceList;

    private Context mContext;

    public GeofenceManager(Context context) {
        mContext = context;
        buildGoogleApiClient();
        mGeofenceList = new ArrayList<>();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public GeofencingRequest getGeofencingRequest(List<LocationItem> items) {

        for (LocationItem item : items) {
            /*String requestId = item.getId();
            if (requestId == null) requestId = item.getName();*/
            String requestId = item.getName();
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(requestId)
                    .setCircularRegion(
                            item.getLatitude(),
                            item.getLongitude(),
                            GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER |
                GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void addGeofences(List<LocationItem> items) {
        if (!mGoogleApiClient.isConnected()) {
            Log.i(TAG, "GoogleApiClient is not connected");
            return;
        }
        if (items.size() == 0) return;

        try {
            PendingResult<Status> g = LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(items),
                    getGeofencePendingIntent()
            );
            g.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    String message;
                    if (status.isSuccess()) {
                        // 성공 후 동작
                    } else {
                        switch (status.getStatusCode()) {
                            case GEOFENCE_NOT_AVAILABLE:
                                message = "Geonfence에서 거부되었습니다.";
                                break;
                            case GEOFENCE_TOO_MANY_GEOFENCES:
                                message = "Geonfence를 너무 많이 등록하였습니다.";
                                break;
                            case GEOFENCE_TOO_MANY_PENDING_INTENTS:
                                message = "Geonfence를 너무 많은 펜딩인텐트를 등록하였습니다.";
                                break;
                            default:
                                message = "알수 없는 에러가 발생하였습니다.";
                        }
                    }
                    Log.d(TAG, "PendingResult:" + status.isSuccess());
                }
            }); // Result processed in onResult().
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException:", e);
            return;
        }
    }

    public boolean removeGenfences() {
        if (!mGoogleApiClient.isConnected()) {
            return false;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    String message;
                    if (status.isSuccess()) {
                        // 성공시 동작
                    } else {
                        // 실패시 동작
                    }
                }
            });
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException:", e);
        }
        return true;
    }

    public boolean removeGeofences(String geofenceId) {
        if (!mGoogleApiClient.isConnected()) {
            return false;
        }
        List<String> geofenceIds = new ArrayList<>();
        geofenceIds.add(geofenceId);
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    geofenceIds
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    String message;
                    if (status.isSuccess()) {
                        // 성공시 동작
                    } else {
                        // 실패시 동작
                    }
                }
            });
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException:", e);
        }
        return true;
    }

    public boolean restartGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            return false;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        addGeofences(FileManager.items);
                    } else {
                        // 실패시 동작
                    }
                }
            });
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException:", e);
        }
        return true;
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(mContext, GeofenceService.class);
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

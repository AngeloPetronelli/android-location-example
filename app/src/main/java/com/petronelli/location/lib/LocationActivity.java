package com.petronelli.location.lib;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * @author Angelo Petronelli on 23/05/2017.
 */
public class LocationActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = getClass().getSimpleName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;
    private static final int PERMISSION_RESOLUTION_REQUEST = 101;

    private static final int FAST_INTERVAL = 6000;     // 6 second
    private static final int INTERVAL = 60000;         // 1 minute

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LocationCallback callback;

    public void setLocationCallback(LocationCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isGooglePlayServicesAvailable(false)) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mLocationRequest = new LocationRequest();
            mLocationRequest.setFastestInterval(FAST_INTERVAL);
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_RESOLUTION_REQUEST &&
                (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            startLocationUpdates();
        }
    }

    public void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (checkLocationPermission()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (checkLocationPermission()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    public void getLastLocation() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (checkLocationPermission()) {
                onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
            }
        }
    }

    public boolean checkLocationPermission() {
        boolean permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!permission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RESOLUTION_REQUEST);
        }
        return permission;
    }

    private boolean isGooglePlayServicesAvailable(boolean showWarning) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result) && showWarning) {
                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    ////

    @Override
    public void onConnected(Bundle bundle) {
        if (checkLocationPermission()) {
            startLocationUpdates();
        }
    }

    @Override
    public final void onLocationChanged(Location location) {
        if (callback != null) {
            callback.locationChanged(location);
        }
    }

    @Override
    public final void onConnectionSuspended(int i) {
        if (callback != null) {
            callback.connectionSuspended(i);
        }
    }

    @Override
    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (callback != null) {
            callback.connectionFailed(connectionResult);
        }
    }
}

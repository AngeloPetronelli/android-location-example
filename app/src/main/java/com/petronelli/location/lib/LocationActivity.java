package com.petronelli.location.lib;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

/**
 * @author Angelo Petronelli on 23/05/2017.
 */
public abstract class LocationActivity extends AppCompatActivity {

    private final String TAG = "LocationActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;
    private static final int PERMISSION_RESOLUTION_REQUEST = 101;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient providerClient;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;

    private LocationCallback locationCallback;

    private LocationRequest locationRequest;
    private Location currentLocation;

    public abstract void onLocationChanged(Location location);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isGooglePlayServicesAvailable(true)) {
            providerClient = LocationServices.getFusedLocationProviderClient(this);
            settingsClient = LocationServices.getSettingsClient(this);

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    if (currentLocation != null) {
                        currentLocation = locationResult.getLastLocation();

                        onLocationChanged(currentLocation);
                    }
                }
            };

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(locationRequest);
            locationSettingsRequest = builder.build();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_RESOLUTION_REQUEST &&
                (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            startLocationUpdated();
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

    //// START/STOP

    public void startLocationUpdated() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    if (checkLocationPermission()) {
                        Log.i(TAG, "startLocationUpdated");
                        providerClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, e -> Log.e(TAG, e.getLocalizedMessage()));
    }

    public void stopLocationUpdated() {
        providerClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(this, task -> Log.i(TAG, "stopLocationUpdated"));
    }

}

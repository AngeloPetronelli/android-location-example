package com.petronelli.location.lib;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;


public interface LocationCallback {

    void locationChanged(Location location);

    void connectionSuspended(int i);

    void connectionFailed(@NonNull ConnectionResult connectionResult);
}

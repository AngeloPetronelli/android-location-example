package com.petronelli.location.activities;

import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.petronelli.location.R;
import com.petronelli.location.databinding.ActivityMainBinding;
import com.petronelli.location.lib.LocationActivity;
import com.petronelli.location.lib.LocationCallback;

/**
 * @author Angelo Petronelli on 23/05/2017.
 */
public class MainActivity extends LocationActivity implements LocationCallback {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBinding.getLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });

        setLocationCallback(this);
    }

    ////

    @Override
    public void locationChanged(Location location) {
        mBinding.time.setText(String.valueOf(location.getTime()));
        mBinding.provider.setText(String.valueOf(location.getProvider()));
        mBinding.latitude.setText(String.valueOf(location.getLatitude()));
        mBinding.longitude.setText(String.valueOf(location.getLongitude()));
        mBinding.accuracy.setText(String.valueOf(location.getAccuracy()));
    }

    @Override
    public void connectionSuspended(int i) {

    }

    @Override
    public void connectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }
}

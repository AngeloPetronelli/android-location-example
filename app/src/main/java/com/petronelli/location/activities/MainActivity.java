package com.petronelli.location.activities;

import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.petronelli.location.R;
import com.petronelli.location.databinding.ActivityMainBinding;
import com.petronelli.location.lib.LocationActivity;

/**
 * @author Angelo Petronelli on 23/05/2017.
 */
public class MainActivity extends LocationActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBinding.getLastLocation.setOnClickListener(v -> startLocationUpdated());
    }

    ////

    @Override
    public void onLocationChanged(Location location) {
        mBinding.time.setText(String.valueOf(location.getTime()));
        mBinding.provider.setText(String.valueOf(location.getProvider()));
        mBinding.latitude.setText(String.valueOf(location.getLatitude()));
        mBinding.longitude.setText(String.valueOf(location.getLongitude()));
        mBinding.accuracy.setText(String.valueOf(location.getAccuracy()));
    }
}

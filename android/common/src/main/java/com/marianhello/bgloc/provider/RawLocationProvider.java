



package com.marianhello.bgloc.provider;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.marianhello.bgloc.Config;
import com.marianhello.logging.LoggerManager;

/**
 * Created by finch on 7.11.2017.
 */

public class RawLocationProvider extends AbstractLocationProvider implements LocationListener {
    private LocationManager locationManager;
    private boolean isStarted = false;

    public RawLocationProvider(Context context) {
        super(context);
        PROVIDER_ID = Config.RAW_PROVIDER;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onStart() {
        if (isStarted) {
            return;
        }

        Criteria criteria = new Criteria();
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setSpeedRequired(true);
//        criteria.setCostAllowed(true);
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setHorizontalAccuracy(translateDesiredAccuracy(mConfig.getDesiredAccuracy()));
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);


        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(true);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        Log.e("RawLocationProvider","RawLocationProvider onStart ");
        try {
            String manager = locationManager.getBestProvider(criteria, true);
            Log.e("RawLocationProvider","RawLocationProvider manager =  "+manager);
            locationManager.requestLocationUpdates(manager  , mConfig.getInterval(), mConfig.getDistanceFilter(), this );
            isStarted = true;
        } catch (SecurityException e) {
            logger.error("Security exception: {}", e.getMessage());
            this.handleSecurityException(e);
        }
    }

    @Override
    public void onStop() {
        if (!isStarted) {
            return;
        }
        try {
            locationManager.removeUpdates(this);
            Log.e("RawLocationProvider","stop");
        } catch (SecurityException e) {
            logger.error("Security exception: {}", e.getMessage());
            this.handleSecurityException(e);
        } finally {
            isStarted = false;
        }
    }

    @Override
    public void onConfigure(Config config) {
        super.onConfigure(config);
        if (isStarted) {
            onStop();
            onStart();
        }
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void onLocationChanged(Location location) {
        logger.debug("Location change: {}", location.toString());
        Log.e("RawLocationProvider","onLocationChanged "+location.toString());
        showDebugToast("acy:" + location.getAccuracy() + ",v:" + location.getSpeed());
        handleLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        logger.debug("Provider {} status changed: {}", provider, status);
        Log.e("RawLocationProvider","onStatusChanged provider= "+provider+",status="+status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        logger.debug("Provider {} was enabled", provider);
        Log.e("RawLocationProvider","onProviderEnabled "+ provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        logger.debug("Provider {} was disabled", provider);
        Log.e("RawLocationProvider","onProviderDisabled "+ provider);
    }

    /**
     * Translates a number representing desired accuracy of Geolocation system from set [0, 10, 100, 1000].
     * 0:  most aggressive, most accurate, worst battery drain
     * 1000:  least aggressive, least accurate, best for battery.
     */
    private Integer translateDesiredAccuracy(Integer accuracy) {
        if (accuracy >= 1000) {
            return Criteria.ACCURACY_LOW;
        }
        if (accuracy >= 100) {
            return Criteria.ACCURACY_MEDIUM;
        }
        if (accuracy >= 10) {
            return Criteria.ACCURACY_HIGH;
        }
        if (accuracy >= 0) {
            return Criteria.ACCURACY_HIGH;
        }

        return Criteria.ACCURACY_MEDIUM;
    }

    @Override
    public void onDestroy() {
        logger.debug("Destroying RawLocationProvider");
        this.onStop();
        super.onDestroy();
    }
}



/*
According to apache license

This is fork of christocracy cordova-plugin-background-geolocation plugin
https://github.com/christocracy/cordova-plugin-background-geolocation

This is a new class
*/

package com.marianhello.bgloc.provider;

import android.content.Context;
import android.util.Log;

import com.marianhello.bgloc.Config;
import com.tenforwardconsulting.bgloc.DistanceFilterLocationProvider;

import java.lang.IllegalArgumentException;

/**
 * LocationProviderFactory
 */
public class LocationProviderFactory {

    private Context mContext;

    public LocationProviderFactory(Context context) {
        this.mContext = context;
    };

    public LocationProvider getInstance (Integer locationProvider) {
        LocationProvider provider;
        switch (locationProvider) {
            case Config.DISTANCE_FILTER_PROVIDER:
//                provider = new DistanceFilterLocationProvider(mContext);
                provider = new RawLocationProvider(mContext);
                break;
            case Config.ACTIVITY_PROVIDER:
//                provider = new ActivityRecognitionLocationProvider(mContext);
                provider = new RawLocationProvider(mContext);
                break;
            case Config.RAW_PROVIDER:
                provider = new RawLocationProvider(mContext);
                break;
            default:
                provider = new RawLocationProvider(mContext);
                //throw new IllegalArgumentException("Provider not found");
        }

        Log.e("RawLocationProvider","provider = "+provider.getClass().getName());

        return provider;
    }
}

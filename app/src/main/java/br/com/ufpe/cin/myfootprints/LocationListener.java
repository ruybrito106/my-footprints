package br.com.ufpe.cin.myfootprints;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

public class LocationListener implements android.location.LocationListener {

    private static final String TAG = "LOCATION_LISTENER";
    private Location previousLocation;
    private LocationUpdateDAO dbInstance;

    public LocationListener(String provider, Context ctx) {
        previousLocation = new Location(provider);
        dbInstance = LocationUpdateDAO.getInstance(ctx);
    }

    public long currentTimestampSeconds() {
        Date date = new Date();
        return date.getTime();
    }

    @Override
    public void onLocationChanged(Location location) {
        previousLocation.set(location);

        if (location.getAccuracy() <= 150f) {
            LocationUpdate update = new LocationUpdate(
                    previousLocation.getLatitude(),
                    previousLocation.getLongitude(),
                    (int)(currentTimestampSeconds()/1000)
            );

            dbInstance.insertLocationUpdate(update);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}

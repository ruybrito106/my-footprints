package br.com.ufpe.cin.myfootprints

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder
import android.util.Log

class LocationUpdateService : Service() {
    private var locationManager: LocationManager? = null

    private var locationListeners: Array<LocationListener>? = null

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")

        locationListeners = arrayOf(LocationListener(LocationManager.GPS_PROVIDER, this), LocationListener(LocationManager.NETWORK_PROVIDER, this))

        initializeLocationManager()

        try {
            locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_INTERVAL.toLong(),
                    LOCATION_DISTANCE,
                    locationListeners!![1])
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }

        try {
            locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL.toLong(),
                    LOCATION_DISTANCE,
                    locationListeners!![0])
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "gps provider does not exist " + ex.message)
        }

    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()

        if (locationManager != null) {
            for (i in locationListeners!!.indices) {
                try {
                    locationManager!!.removeUpdates(locationListeners!![i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex)
                }

            }
        }
    }

    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager")
        if (locationManager == null) {
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    companion object {

        private val TAG = "LOCATION_UPDATE_SERVICE"

        private val LOCATION_INTERVAL = 10000 // Minimum interval in milliseconds between updates
        private val LOCATION_DISTANCE = 50f // Minimum interval in meters between updates
    }
}

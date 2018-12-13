package br.com.ufpe.cin.myfootprints

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log

import java.util.Date

class LocationListener(provider: String, ctx: Context) : android.location.LocationListener {
    private val previousLocation: Location
    private val dbInstance: LocationUpdateDAO?

    init {
        previousLocation = Location(provider)
        dbInstance = LocationUpdateDAO.getInstance(ctx)
    }

    fun currentTimestampSeconds(): Long {
        val date = Date()
        return date.time
    }

    override fun onLocationChanged(location: Location) {
        previousLocation.set(location)

        if (location.accuracy <= 150f) {
            val update = LocationUpdate(
                    previousLocation.latitude,
                    previousLocation.longitude,
                    (currentTimestampSeconds() / 1000).toInt()
            )

            dbInstance!!.insertLocationUpdate(update)
        }
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {

        private val TAG = "LOCATION_LISTENER"
    }
}

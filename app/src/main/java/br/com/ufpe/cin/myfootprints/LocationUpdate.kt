package br.com.ufpe.cin.myfootprints

import ch.hsr.geohash.*

import java.util.ArrayList

class LocationUpdate(var lat: Double, var lng: Double, var timestampSeconds: Int) {

    fun toGeohash(): String {
        val geohash = GeoHash.withCharacterPrecision(lat, lng, 9)
        return geohash.toBase32()
    }

    fun toString(withDifference: Boolean, prev: LocationUpdate?): String {

        if (withDifference) {
            val deltaTs = this.timestampSeconds - prev!!.timestampSeconds
            return String.format("%s%d,", this.toGeohash(), deltaTs)
        }

        return String.format("%s%d,", this.toGeohash(), this.timestampSeconds)

    }

    companion object {

        private val DISTANCE_THRESHOLD = 150.0
        private val TIME_THRESHOLD = (20 * 60).toDouble()
        private val EARTH_RADIUS = 6371

        fun fromGeohash(strGeohash: String): LocationUpdate {
            val geohash = GeoHash.fromGeohashString(strGeohash)
            val pt = geohash.boundingBoxCenterPoint
            return LocationUpdate(pt.latitude, pt.longitude, 0)
        }

        fun distance(lat1: Double, lat2: Double, lon1: Double, lon2: Double): Double {
            var lat1 = lat1
            var lat2 = lat2

            val dLat = Math.toRadians(lat2 - lat1)
            val dLong = Math.toRadians(lon2 - lon1)

            lat1 = Math.toRadians(lat1)
            lat2 = Math.toRadians(lat2)

            val hLat = Math.pow(Math.sin(dLat / 2), 2.0)
            val hLng = Math.pow(Math.sin(dLong / 2), 2.0)

            val a = hLat + Math.cos(lat1) * Math.cos(lat2) * hLng
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

            return EARTH_RADIUS.toDouble() * c * 1000.0

        }

        fun isAnotherVisit(cur: LocationUpdate, next: LocationUpdate): Boolean {
            val timeOffset = Math.abs(next.timestampSeconds - cur.timestampSeconds)
            val distanceOffset = Math.abs(distance(cur.lat, next.lat, cur.lng, next.lng))
            return timeOffset >= TIME_THRESHOLD && distanceOffset >= DISTANCE_THRESHOLD
        }

        fun filterRealVisits(`as`: List<LocationUpdate>): List<LocationUpdate> {
            val filtered = ArrayList<LocationUpdate>()
            if (`as`.size > 0) {
                filtered.add(`as`[0])
                var top = `as`[0]
                for (i in 1 until `as`.size) {
                    val current = `as`[i]
                    if (isAnotherVisit(current, top)) {
                        filtered.add(current)
                        top = current
                    }
                }
            }
            return filtered
        }
    }

}

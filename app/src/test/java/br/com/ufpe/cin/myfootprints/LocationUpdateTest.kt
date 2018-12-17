package br.com.ufpe.cin.myfootprints

import org.junit.Assert.*
import org.junit.Test

class LocationUpdateTest {

    @Test
    fun toGeohash() {
        val geohash = LocationUpdate(latFixture, lngFixture, timetampFixture).toGeohash()
        assertEquals(geohash, geohashFixture)
    }

    @Test
    fun fromGeohash(){
       val location = LocationUpdate.fromGeohash(geohashFixture)
        assertEquals(location.lat, latFixture, 0.001)
        assertEquals(location.lng, lngFixture, 0.001)
    }

    @Test
    fun distance(){
        val lat = -10.668983
        val lng = 12.329021
        val expectedDistance = 6797981.256104764
        val distance = LocationUpdate.distance(latFixture, lat, lngFixture, lng)
        assertEquals(expectedDistance, distance, 0.001)
    }

    @Test
    fun isAnotherVisitFalse(){
        val visit = LocationUpdate(latFixture, lngFixture, timetampFixture)
        val sameVisit = LocationUpdate(latFixture, lngFixture, timetampFixture)
        val isAnotherVisit = LocationUpdate.isAnotherVisit(visit, sameVisit)
        assertFalse(isAnotherVisit)
    }

    @Test
    fun isAnotherVisitTrue(){
        val visit = LocationUpdate(latFixture, lngFixture, timetampFixture)
        val otherVisit = LocationUpdate(-10.668983, 12.329021, 1345007797)
        val isAnotherVisit = LocationUpdate.isAnotherVisit(visit, otherVisit)
        assertTrue(isAnotherVisit)
    }

    @Test
    fun filterRealVisits(){
        val visitOne = LocationUpdate(latFixture, lngFixture, timetampFixture)
        val sameVisitOne = LocationUpdate(latFixture, lngFixture, timetampFixture)
        val visitTwo = LocationUpdate(-10.668983, 12.329021, 1345007797)
        val sameVisitTwo = LocationUpdate(-10.668983, 12.329021, 1345007797)
        val originalVisits = listOf(visitOne, sameVisitOne, visitTwo, sameVisitTwo)
        val visits = LocationUpdate.filterRealVisits(originalVisits)
        assertEquals(visits.count(), 2)
    }


    companion object {

        private val latFixture = 48.668983
        private val lngFixture = -4.329021
        private val timetampFixture = 1545007797
        private val geohashFixture = "gbsuv7ztq"
    }

}

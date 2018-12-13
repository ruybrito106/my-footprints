package br.com.ufpe.cin.myfootprints

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.util.ArrayList
import java.util.Date

class LocationUpdateDAO private constructor(internal var c: Context) : SQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_DB_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun insertLocationUpdate(update: LocationUpdate) {

        val values = ContentValues()
        values.put(LOCATION_UPDATE_LAT, update.lat)
        values.put(LOCATION_UPDATE_LNG, update.lng)
        values.put(LOCATION_UPDATE_TIMESTAMP, update.timestampSeconds)

        val status = writableDatabase.insertWithOnConflict(
                DATABASE_TABLE, null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        )

        Log.d("INSERT_LOCATION", java.lang.Long.toString(status))
    }

    fun getLocationUpdatesByDateRange(startDate: Date?, endDate: Date?): List<LocationUpdate> {

        var startTimestampMillis = 0
        var endTimestampMillis = 0

        if(startDate != null && endDate != null){
            startTimestampMillis = (startDate.time / 1000).toInt()
            endTimestampMillis = (endDate.time / 1000).toInt()
        }

        val modelQuery = "SELECT * FROM %s WHERE %s.%s BETWEEN %d AND %d ORDER BY %s.%s"
        val query = String.format(
                modelQuery,
                DATABASE_TABLE,
                DATABASE_TABLE,
                LOCATION_UPDATE_TIMESTAMP,
                startTimestampMillis,
                endTimestampMillis,
                DATABASE_TABLE,
                LOCATION_UPDATE_TIMESTAMP
        )

        val rawList = getLocationUpdates(query)
        return LocationUpdate.filterRealVisits(rawList)
    }

    private fun getLocationUpdates(query: String): List<LocationUpdate> {
        val result = ArrayList<LocationUpdate>()
        val cursor = readableDatabase.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val lat = cursor.getDouble(cursor.getColumnIndex(LOCATION_UPDATE_LAT))
                val lng = cursor.getDouble(cursor.getColumnIndex(LOCATION_UPDATE_LNG))
                val ts = cursor.getInt(cursor.getColumnIndex(LOCATION_UPDATE_TIMESTAMP))
                result.add(LocationUpdate(lat, lng, ts))
            }
        } finally {
            cursor.close()
        }

        return result
    }

    companion object {

        private val DATABASE_NAME = "locations"
        private val DATABASE_TABLE = "location_updates"
        private val DB_VERSION = 1

        private var db: LocationUpdateDAO? = null

        fun getInstance(c: Context): LocationUpdateDAO? {
            if (db == null) {
                db = LocationUpdateDAO(c.applicationContext)
            }
            return db
        }

        private val LOCATION_UPDATE_ID = "id"
        private val LOCATION_UPDATE_LAT = "lat"
        private val LOCATION_UPDATE_LNG = "lng"
        private val LOCATION_UPDATE_TIMESTAMP = "ts"

        val columns = arrayOf(LOCATION_UPDATE_ID, LOCATION_UPDATE_LAT, LOCATION_UPDATE_LNG, LOCATION_UPDATE_TIMESTAMP)

        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                LOCATION_UPDATE_ID + " integer primary key autoincrement, " +
                LOCATION_UPDATE_LAT + " real not null, " +
                LOCATION_UPDATE_LNG + " real not null, " +
                LOCATION_UPDATE_TIMESTAMP + " integer not null);"
    }
}
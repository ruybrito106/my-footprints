package br.com.ufpe.cin.myfootprints

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.util.ArrayList

class FriendSharedLocationDAO private constructor(internal var c: Context) : SQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {

    val friendSharedLocations: List<String>
        get() {

            val modelQuery = "SELECT * FROM %s ORDER BY %s.%s"
            val query = String.format(
                    modelQuery,
                    DATABASE_TABLE,
                    DATABASE_TABLE,
                    SHARED_LOCATION_UPDATE_PARSED_TEXT
            )

            return getFriendSharedLocationsByQuery(query)
        }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_DB_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun insertFriendSharedLocation(sharedLocationText: String) {

        val values = ContentValues()
        values.put(SHARED_LOCATION_UPDATE_PARSED_TEXT, sharedLocationText)

        val status = writableDatabase.insertWithOnConflict(
                DATABASE_TABLE, null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        )

        Log.d("INSERT_FRIEND_SHARED_LOCATION", java.lang.Long.toString(status))
    }

    private fun getFriendSharedLocationsByQuery(query: String): List<String> {
        val result = ArrayList<String>()
        val cursor = readableDatabase.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val sharedText = cursor.getString(cursor.getColumnIndex(SHARED_LOCATION_UPDATE_PARSED_TEXT))
                result.add(sharedText)
            }
        } finally {
            cursor.close()
        }

        return result
    }

    companion object {

        private val DATABASE_NAME = "shared_locations"
        private val DATABASE_TABLE = "shared_location_updates"
        private val DB_VERSION = 1

        private var db: FriendSharedLocationDAO? = null

        fun getInstance(c: Context): FriendSharedLocationDAO? {
            if (db == null) {
                db = FriendSharedLocationDAO(c.applicationContext)
            }
            return db
        }

        private val SHARED_LOCATION_UPDATE_ID = "id"
        private val SHARED_LOCATION_UPDATE_PARSED_TEXT = "parsed_text"

        val columns = arrayOf(SHARED_LOCATION_UPDATE_ID, SHARED_LOCATION_UPDATE_PARSED_TEXT)

        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                SHARED_LOCATION_UPDATE_ID + " integer primary key autoincrement, " +
                SHARED_LOCATION_UPDATE_PARSED_TEXT + " text not null);"
    }
}
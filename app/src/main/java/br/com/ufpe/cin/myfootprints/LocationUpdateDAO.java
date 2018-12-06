package br.com.ufpe.cin.myfootprints;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.ufpe.cin.myfootprints.LocationUpdate;

public class LocationUpdateDAO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locations";
    private static final String DATABASE_TABLE = "location_updates";
    private static final int DB_VERSION = 1;

    Context c;

    private LocationUpdateDAO(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        c = context;
    }

    private static LocationUpdateDAO db;

    public static LocationUpdateDAO getInstance(Context c) {
        if (db == null) {
            db = new LocationUpdateDAO(c.getApplicationContext());
        }
        return db;
    }

    private static final String LOCATION_UPDATE_ID = "id";
    private static final String LOCATION_UPDATE_LAT = "lat";
    private static final String LOCATION_UPDATE_LNG = "lng";
    private static final String LOCATION_UPDATE_TIMESTAMP = "ts";

    public final static String[] columns = {
            LOCATION_UPDATE_ID,
            LOCATION_UPDATE_LAT,
            LOCATION_UPDATE_LNG,
            LOCATION_UPDATE_TIMESTAMP,
    };

    private static final String CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
            LOCATION_UPDATE_ID +" integer primary key autoincrement, "+
            LOCATION_UPDATE_LAT + " real not null, " +
            LOCATION_UPDATE_LNG + " real not null, " +
            LOCATION_UPDATE_TIMESTAMP + " integer not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void insertLocationUpdate(LocationUpdate update) {

        LocationUpdate prevLocation = getLastLocationUpdate();
        if (prevLocation != null && !prevLocation.isNearby(update)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(LOCATION_UPDATE_LAT, update.getLat());
        values.put(LOCATION_UPDATE_LNG, update.getLng());
        values.put(LOCATION_UPDATE_TIMESTAMP, update.getTimestampSeconds());

        long status = getWritableDatabase().insertWithOnConflict(
                DATABASE_TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        Log.d("INSERT_LOCATION", Long.toString(status));
    }

    public List<LocationUpdate> getLocationUpdatesByDateRange(Date startDate, Date endDate) {

        int startTimestampMillis = (int)(startDate.getTime()/1000);
        int endTimestampMillis = (int)(endDate.getTime()/1000);

        String modelQuery = "SELECT * FROM %s WHERE %s.%s BETWEEN %d AND %d";
        String query = String.format(modelQuery, DATABASE_TABLE, DATABASE_TABLE, LOCATION_UPDATE_TIMESTAMP, startTimestampMillis, endTimestampMillis);

        return getLocationUpdates(query);
    }

    public LocationUpdate getLastLocationUpdate() {
        String modelQuery = "SELECT * FROM %s ORDER BY %s.%s DESC LIMIT 1";
        String query = String.format(modelQuery, DATABASE_TABLE, DATABASE_TABLE, LOCATION_UPDATE_TIMESTAMP);

        List<LocationUpdate> result = getLocationUpdates(query);
        if (result == null || result.size() == 0) {
            return null;
        }

        return result.get(0);
    }

    private List<LocationUpdate> getLocationUpdates(String query) {
        List<LocationUpdate> result = new ArrayList<LocationUpdate>();
        Cursor cursor = getReadableDatabase().rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(cursor.getColumnIndex(LOCATION_UPDATE_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(LOCATION_UPDATE_LNG));
                int ts = cursor.getInt(cursor.getColumnIndex(LOCATION_UPDATE_TIMESTAMP));
                result.add(new LocationUpdate(lat, lng, ts));
            }
        } finally {
            cursor.close();
        }

        return result;
    }
}
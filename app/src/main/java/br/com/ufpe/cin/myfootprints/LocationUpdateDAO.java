package br.com.ufpe.cin.myfootprints;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.ufpe.cin.myfootprints.LocationUpdate;

public class LocationUpdateDAO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locations";
    public static final String DATABASE_TABLE = "location_updates";
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

    public static final String LOCATION_UPDATE_ID = "id";
    public static final String LOCATION_UPDATE_LAT = "lat";
    public static final String LOCATION_UPDATE_LNG = "lng";
    public static final String LOCATION_UPDATE_TIMESTAMP = "ts";

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

}
package br.com.ufpe.cin.myfootprints;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FriendSharedLocationDAO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shared_locations";
    private static final String DATABASE_TABLE = "shared_location_updates";
    private static final int DB_VERSION = 1;

    Context c;

    private FriendSharedLocationDAO(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        c = context;
    }

    private static FriendSharedLocationDAO db;

    public static FriendSharedLocationDAO getInstance(Context c) {
        if (db == null) {
            db = new FriendSharedLocationDAO(c.getApplicationContext());
        }
        return db;
    }

    private static final String SHARED_LOCATION_UPDATE_ID = "id";
    private static final String SHARED_LOCATION_UPDATE_PARSED_TEXT = "parsed_text";

    public final static String[] columns = {
            SHARED_LOCATION_UPDATE_ID,
            SHARED_LOCATION_UPDATE_PARSED_TEXT,
    };

    private static final String CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
            SHARED_LOCATION_UPDATE_ID +" integer primary key autoincrement, "+
            SHARED_LOCATION_UPDATE_PARSED_TEXT + " text not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void insertFriendSharedLocation(String sharedLocationText) {

        ContentValues values = new ContentValues();
        values.put(SHARED_LOCATION_UPDATE_PARSED_TEXT, sharedLocationText);

        long status = getWritableDatabase().insertWithOnConflict(
                DATABASE_TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        Log.d("INSERT_FRIEND_SHARED_LOCATION", Long.toString(status));
    }

    public List<String> getFriendSharedLocations() {

        String modelQuery = "SELECT * FROM %s ORDER BY %s.%s";
        String query = String.format(
                modelQuery,
                DATABASE_TABLE,
                DATABASE_TABLE,
                SHARED_LOCATION_UPDATE_PARSED_TEXT
        );

        return getFriendSharedLocationsByQuery(query);
    }

    private List<String> getFriendSharedLocationsByQuery(String query) {
        List<String> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                String sharedText = cursor.getString(cursor.getColumnIndex(SHARED_LOCATION_UPDATE_PARSED_TEXT));
                result.add(sharedText);
            }
        } finally {
            cursor.close();
        }

        return result;
    }
}
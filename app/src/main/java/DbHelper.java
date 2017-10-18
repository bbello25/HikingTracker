import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.brano.hikingtracker.TrackContract.TrackEntry;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "geotracking.db";
    public static final int DATABASE_VERSION = 1;


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TrackEntry.TABLE_NAME + " (" +
                    TrackEntry._ID + " INTEGER PRIMARY KEY," +
                    TrackEntry.COLUMN_LAT + " DOUBLE," +
                    TrackEntry.COLUMN_LAT + " DOUBLE," +
                    TrackEntry.COLUMN_TIMESTAMP + " DATETIME)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

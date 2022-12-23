package work2.mobile_finalproject.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BookmarkDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "bookmark_db";
    public final static String TABLE_NAME = "bookmark_db_table";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_PHONE = "phone";
    public final static String COL_ADDRESS = "address";
    public final static String COL_PLACEID = "placeId";
    public final static String COL_LAT = "lat";
    public final static String COL_LNG = "lng";
    public final static String COL_KEYWORD = "keyword";

    public BookmarkDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_NAME + " TEXT, " + COL_PHONE + " TEXT, " + COL_ADDRESS + " TEXT, " + COL_PLACEID + " TEXT, " + COL_LAT + " TEXT, "
                + COL_LNG + " TEXT," + COL_KEYWORD + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

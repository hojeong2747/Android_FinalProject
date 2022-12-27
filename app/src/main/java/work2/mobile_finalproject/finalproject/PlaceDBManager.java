package work2.mobile_finalproject.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PlaceDBManager {

    PlaceDBHelper placeDBHelper = null;
    BookmarkDBHelper bookmarkDBHelper = null;
    Cursor cursor = null;

    public PlaceDBManager(Context context) {
        placeDBHelper = new PlaceDBHelper(context);
        bookmarkDBHelper = new BookmarkDBHelper(context);
    }

    public boolean addReview(PlaceDto newPlace) {
        SQLiteDatabase db = placeDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(PlaceDBHelper.COL_NAME, newPlace.getName());
        value.put(PlaceDBHelper.COL_PHONE, newPlace.getPhone());
        value.put(PlaceDBHelper.COL_ADDRESS, newPlace.getAddress());
        value.put(PlaceDBHelper.COL_DATE, newPlace.getDate());
        value.put(PlaceDBHelper.COL_PHOTOPATH, newPlace.getPhotoPath());
        value.put(PlaceDBHelper.COL_CONTENT, newPlace.getContent());
        value.put(PlaceDBHelper.COL_RATING, newPlace.getRating());

        long count = db.insert(PlaceDBHelper.TABLE_NAME, null, value);
        placeDBHelper.close();
        if (count > 0) return true;
        return false;
    }

    public boolean addBookmark(PlaceDto newPlace) {
        SQLiteDatabase db = bookmarkDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(BookmarkDBHelper.COL_NAME, newPlace.getName());
        value.put(BookmarkDBHelper.COL_PHONE, newPlace.getPhone());
        value.put(BookmarkDBHelper.COL_ADDRESS, newPlace.getAddress());
        value.put(BookmarkDBHelper.COL_PLACEID, newPlace.getPlaceId());
        value.put(BookmarkDBHelper.COL_LAT, newPlace.getLat());
        value.put(BookmarkDBHelper.COL_LNG, newPlace.getLng());
        value.put(BookmarkDBHelper.COL_KEYWORD, newPlace.getKeyWord());

        long count = db.insert(BookmarkDBHelper.TABLE_NAME, null, value);
        bookmarkDBHelper.close();
        if (count > 0) return true;
        return false;
    }

    public boolean updateReview(PlaceDto review) {
        SQLiteDatabase db = placeDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(PlaceDBHelper.COL_DATE, review.getDate());
        row.put(PlaceDBHelper.COL_PHOTOPATH, review.getPhotoPath());
        row.put(PlaceDBHelper.COL_CONTENT, review.getContent());
        row.put(PlaceDBHelper.COL_RATING, review.getRating());

        String whereClause = PlaceDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(review.getId()) };

        int result = db.update(PlaceDBHelper.TABLE_NAME, row, whereClause, whereArgs);
        placeDBHelper.close();

        if (result > 0) return true;
        return false;
    }

    public boolean deleteBookmark(long id) {
        SQLiteDatabase db = bookmarkDBHelper.getWritableDatabase();
        String whereClause = BookmarkDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = db.delete(BookmarkDBHelper.TABLE_NAME, whereClause,whereArgs);
        bookmarkDBHelper.close();

        if (result > 0) return true;
        return false;
    }

    public boolean deleteReview(long id) {
        SQLiteDatabase db = placeDBHelper.getWritableDatabase();
        String whereClause = PlaceDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = db.delete(PlaceDBHelper.TABLE_NAME, whereClause,whereArgs);
        placeDBHelper.close();

        if (result > 0) return true;
        return false;
    }

    public void close() {
        if (placeDBHelper != null) placeDBHelper.close();
        if (bookmarkDBHelper != null) bookmarkDBHelper.close();
        if (cursor != null) cursor.close();
    }
}

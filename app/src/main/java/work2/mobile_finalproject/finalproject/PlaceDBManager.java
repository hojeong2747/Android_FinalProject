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

    //    DB 에 새로운 store 추가
    public boolean addNewReview(PlaceDto newPlace) {
        SQLiteDatabase db = placeDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(PlaceDBHelper.COL_NAME, newPlace.getName());
        value.put(PlaceDBHelper.COL_PHONE, newPlace.getPhone());
        value.put(PlaceDBHelper.COL_ADDRESS, newPlace.getAddress());
        value.put(PlaceDBHelper.COL_DATE, newPlace.getDate());
        value.put(PlaceDBHelper.COL_PHOTOPATH, newPlace.getPhotoPath());
        value.put(PlaceDBHelper.COL_CONTENT, newPlace.getContent());
        value.put(PlaceDBHelper.COL_RATING, newPlace.getRating());

//      insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(PlaceDBHelper.TABLE_NAME, null, value);
        placeDBHelper.close();
        if (count > 0) return true;
        return false;
    }

    public boolean addNewBookmark(PlaceDto newPlace) {
        SQLiteDatabase db = bookmarkDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(BookmarkDBHelper.COL_NAME, newPlace.getName());
        value.put(BookmarkDBHelper.COL_PHONE, newPlace.getPhone());
        value.put(BookmarkDBHelper.COL_ADDRESS, newPlace.getAddress());
        value.put(BookmarkDBHelper.COL_PLACEID, newPlace.getPlaceId());
        value.put(BookmarkDBHelper.COL_LAT, newPlace.getLat());
        value.put(BookmarkDBHelper.COL_LNG, newPlace.getLng());
        value.put(BookmarkDBHelper.COL_KEYWORD, newPlace.getKeyWord());

//      insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(BookmarkDBHelper.TABLE_NAME, null, value);
        bookmarkDBHelper.close();
        if (count > 0) return true;
        return false;
    }

    //    _id 를 기준으로 review의 정보 변경
    public boolean modifyReview(PlaceDto review) {
        SQLiteDatabase sqLiteDatabase = placeDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(PlaceDBHelper.COL_DATE, review.getDate());
        row.put(PlaceDBHelper.COL_PHOTOPATH, review.getPhotoPath());
        row.put(PlaceDBHelper.COL_CONTENT, review.getContent());
        row.put(PlaceDBHelper.COL_RATING, review.getRating());

        String whereClause = PlaceDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(review.getId()) };

        int result = sqLiteDatabase.update(PlaceDBHelper.TABLE_NAME, row, whereClause, whereArgs);
        placeDBHelper.close();

        if (result > 0) return true;
        return false;
    }
    //
    //    _id 를 기준으로 DB에서 Bookmark삭제
    public boolean removeBookmark(long id) {
        SQLiteDatabase sqLiteDatabase = bookmarkDBHelper.getWritableDatabase();
        String whereClause = BookmarkDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = sqLiteDatabase.delete(BookmarkDBHelper.TABLE_NAME, whereClause,whereArgs);
        bookmarkDBHelper.close();

        if (result > 0) return true;
        return false;
    }

    //    _id 를 기준으로 DB에서 Bookmark삭제
    public boolean removeReview(long id) {
        SQLiteDatabase sqLiteDatabase = placeDBHelper.getWritableDatabase();
        String whereClause = PlaceDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = sqLiteDatabase.delete(PlaceDBHelper.TABLE_NAME, whereClause,whereArgs);
        placeDBHelper.close();

        if (result > 0) return true;
        return false;
    }


    //    close 수행
    public void close() {
        if (placeDBHelper != null) placeDBHelper.close();
        if (bookmarkDBHelper != null) bookmarkDBHelper.close();
        if (cursor != null) cursor.close();
    }
}

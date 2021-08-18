package my.edu.utar.person;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteAdapter extends SQLiteOpenHelper {
    private static final String MYDATABASE_NAME = "TimeNotificationList.db";
    private static final int MYDATABASE_VERSION = 1;

    private static final String MYDATABASE_TABLE = "notification_lists";
    private static final String N_ID = "n_id";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String HOUR = "hour";
    private static final String MIN = "min";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";


    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public SQLiteAdapter(Context context) {
        super(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SCRIPT_CREATE_TABLE = "create table "
                +  MYDATABASE_TABLE + " (" + N_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " text not null, "
                + CONTENT + " text not null, "
                + HOUR + " int, "
                + MIN + " int, "
                + YEAR + " int, "
                + MONTH + " int, "
                + DAY + " int);";
        db.execSQL(SCRIPT_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + MYDATABASE_TABLE;
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<TimeNotification> NotificationDataList() {
        String sql = "SELECT * FROM " + MYDATABASE_TABLE;
        ArrayList<TimeNotification> listDatas = new ArrayList<>();
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {

                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String content = cursor.getString(2);
                int hour = Integer.parseInt(cursor.getString(3));
                int min = Integer.parseInt(cursor.getString(4));
                int year = Integer.parseInt(cursor.getString(5));
                int month = Integer.parseInt(cursor.getString(6));
                int day = Integer.parseInt(cursor.getString(7));
                listDatas.add(new TimeNotification(id, title, content, hour, min, year, month, day));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return listDatas;

    }

    public void addNotificationList(TimeNotification timeNotification){
        ContentValues values = new ContentValues();
        values.put(TITLE, timeNotification.getTitle());
        values.put(CONTENT, timeNotification.getContent());
        values.put(HOUR, timeNotification.getHour());
        values.put(MIN, timeNotification.getMin());
        values.put(YEAR, timeNotification.getYear());
        values.put(MONTH, timeNotification.getMonth());
        values.put(DAY, timeNotification.getDay());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(MYDATABASE_TABLE, null, values);
    }

    public void deleteNotificationList(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MYDATABASE_TABLE, N_ID + " = ?", new String[] {String.valueOf(id)});
    }

    public void deleteAllNotification(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MYDATABASE_TABLE, null, null);
    }
}

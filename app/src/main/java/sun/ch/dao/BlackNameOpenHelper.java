package sun.ch.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by asus on 2016/12/9.
 */
public class BlackNameOpenHelper extends SQLiteOpenHelper {
    public BlackNameOpenHelper(Context context) {
        super(context, "black.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement," +
                "number varchar(20),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

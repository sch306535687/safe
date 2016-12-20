package sun.ch.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sunch on 2016/12/20.
 */
public class AppLockDao {

    private final AppLockOpenHelper openHelper;

    public AppLockDao(Context context) {
        openHelper = new AppLockOpenHelper(context);
    }
    /**
     * 添加数据
     *
     * @return
     */
    public boolean add(String packageName) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", packageName);
        long rowId = db.insert("applock", null, contentValues);
        if (rowId == -1) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }
    /**
     * 删除数据
     *
     * @return
     */
    public boolean delete(String packageName) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int blacknumber = db.delete("applock", "packagename=?", new String[]{packageName});
        if (blacknumber == 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }
    /**
     * 查找数据
     *
     * @return
     */
    public boolean select(String packageName) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        boolean flag = false;
        Cursor cursor = db.query("applock", null, "packagename=?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            flag = true;
        }
        db.close();
        cursor.close();
        return flag;
    }
}

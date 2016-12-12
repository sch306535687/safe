package sun.ch.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import sun.ch.bean.BlackNumberInfo;

/**
 * Created by asus on 2016/12/9.
 */
public class BlackNameDao {

    private final BlackNameOpenHelper openHelper;

    public BlackNameDao(Context context) {
        openHelper = new BlackNameOpenHelper(context);
    }

    /**
     * 添加数据
     *
     * @return
     */
    public boolean add(String number, String mode) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("mode", mode);
        long rowId = db.insert("blacknumber", null, contentValues);
        if (rowId == -1) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    /**
     * 通过电话号码删除数据
     *
     * @return
     */
    public boolean delete(String number) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int blacknumber = db.delete("blacknumber", "number=?", new String[]{number});
        if (blacknumber == 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    /**
     * 根据电话号码修改拦截模式
     *
     * @return
     */
    public boolean update(String number, String mode) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);
        int blacknumber = db.update("blacknumber", contentValues, "number=?", new String[]{number});
        if (blacknumber == 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    /**
     * 通过电话号码返回拦截模式
     *
     * @return
     */
    public String select(String number) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String mode = "";
        Cursor cursor = db.query("blacknumber", new String[]{"mode"},
                "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        db.close();
        cursor.close();
        return mode;
    }

    /**
     * 获取全部数据
     *
     * @return
     */
    public List<BlackNumberInfo> getAll() {
        List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackNumberInfo numberInfo = new BlackNumberInfo();
            numberInfo.setNumber(cursor.getString(0));
            numberInfo.setMode(cursor.getString(1));
            list.add(numberInfo);
        }
        db.close();
        cursor.close();
        SystemClock.sleep(3000);
        return list;
    }

    /**
     * 获取分页数据
     *
     * @param pageNumber 当前的页数
     * @param pageSize   每页加载的数据数量
     * @return
     */
    public List<BlackNumberInfo> getPageData(int pageNumber, int pageSize) {
        List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{pageSize + "", pageSize * pageNumber + ""});
        while (cursor.moveToNext()) {
            BlackNumberInfo numberInfo = new BlackNumberInfo();
            numberInfo.setNumber(cursor.getString(0));
            numberInfo.setMode(cursor.getString(1));
            list.add(numberInfo);
        }
        db.close();
        cursor.close();
        SystemClock.sleep(3000);
        return list;
    }

    public int getCount() {
        int count = 0;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        boolean b = cursor.moveToNext();
        if (b) {
            count = cursor.getInt(0);
        }
        return count;
    }

}

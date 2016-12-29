package sun.ch.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import sun.ch.bean.BlackNumberInfo;
import sun.ch.bean.Info;


/**
 * Created by sunch on 2016/12/20.
 */
public class AppLockDao {

    private final AppLockOpenHelper openHelper;
    private Context context;

    public AppLockDao(Context context) {
        openHelper = new AppLockOpenHelper(context);
        this.context = context;
    }
    /**
     * 添加数据
     *
     * @return
     */
    public boolean add(String packageName) {
        //内容观察者
        context.getContentResolver().notifyChange(Uri.parse("content://sun.ch.safe.change"),null);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packageName);
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
        //内容观察者
        context.getContentResolver().notifyChange(Uri.parse("content://sun.ch.safe.change"),null);
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
    public boolean search(String packageName) {
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
    /**
     * 获取全部数据
     *
     * @return
     */
    public List<String> findAll(){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
        List<String> packnames = new ArrayList<String>();
        while(cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}

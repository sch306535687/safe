package sun.ch.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作病毒数据库
 */
public class AntivirusDao {
    /**
     * 根据应用特征码查看是否是病毒
     * @param appMd
     * @return
     */
    public static String checkVirus(String appMd){
        String desc = null;
        String PATH = "data/data/sun.ch.safe/files/antivirus.db";
        // 获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{appMd});
        if(cursor.moveToNext()){
            desc = cursor.getString(0);
            System.out.println(desc);
        }
        return desc;
    }
}

package ir.transport_x.taxi.sqllite;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FindDownloadId {
    public static int execte(String url) {
        try (SQLiteDatabase db = LocalDBIO.getIO().getReadableDatabase()) {
            @SuppressLint("Recycle") Cursor res = db.rawQuery("select * from " + DownloadList.TABLE_NAME + " where " + DownloadList.URL + " = '" + url + "'", null);
            res.moveToFirst();
            if (res.isAfterLast())
                return -1;
            return res.getInt(res.getColumnIndex(DownloadList.DOWNLOADID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}

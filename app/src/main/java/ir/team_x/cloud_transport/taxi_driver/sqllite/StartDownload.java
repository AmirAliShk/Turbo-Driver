package ir.team_x.cloud_transport.taxi_driver.sqllite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class StartDownload {
    public static void execute(int id, String url, String filePath) {
        try (SQLiteDatabase db = LocalDBIO.getIO().getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadList.DOWNLOADID, id);
            contentValues.put(DownloadList.FILEPATH, filePath);
            contentValues.put(DownloadList.URL, url);
            db.insertWithOnConflict(DownloadList.TABLE_NAME, DownloadList.DOWNLOADID, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

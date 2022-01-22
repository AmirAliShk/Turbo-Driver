package ir.team_x.cloud_transport.taxi_driver.sqllite;

import android.database.sqlite.SQLiteDatabase;

public class FinishedDownload {
    public static void execute(String url) {
        try (SQLiteDatabase db = LocalDBIO.getIO().getWritableDatabase()) {
            db.delete(DownloadList.TABLE_NAME, DownloadList.URL + " = ?", new String[]{url});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package ir.transport_x.taxi.sqllite;

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

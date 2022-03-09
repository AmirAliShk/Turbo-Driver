package ir.transport_x.taxi.sqllite;

import android.database.sqlite.SQLiteDatabase;

public class DownloadList {
    private int id;
    private int downloadId;
    private String url;
    private String filePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static final String TABLE_NAME = "downloadList";
    public static final String ID = "id";
    public static final String DOWNLOADID = "downloadId";
    public static final String URL = "url";
    public static final String FILEPATH = "filePath";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME
                + "(" +
                ID + " integer primary key autoincrement not null, " +
                DOWNLOADID + " integer," +
                URL + " varchar(500)," +
                FILEPATH + " text" +
                ")");
    }


}

package ir.team_x.cloud_transport.taxi_driver.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ir.team_x.cloud_transport.taxi_driver.app.MyApplication;


public class LocalDBIO extends SQLiteOpenHelper {

    private static final int VERSION = 4;
    private static final String DB_NAME = "ttDB";

    private LocalDBIO(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        CreateCardNumber.execute(db);
        DownloadList.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + Cards.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DownloadList.TABLE_NAME);
        onCreate(db);
    }

    private static LocalDBIO io = null;

    /**
     * create instance of this class for use all of procedure
     *
     * @return
     */
    public static synchronized LocalDBIO getIO() {
        if (io == null) {
            io = new LocalDBIO(MyApplication.context);
        }
        return io;
    }

    public static String getText(String s) {
        return s;
    }


}

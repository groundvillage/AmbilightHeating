package de.htwg_konstanz.in.uc_lab.ambilightcontrol.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.profile.ProfileContract;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.server.ServerContract;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    public DbHelper(Context context){
        super(context, DbContract.DB_NAME, null, DbContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createProfileTable = "CREATE TABLE " + ProfileContract.ProfilesEntry.TABLE_NAME + " ( " +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_NAME + " TEXT PRIMARY KEY NOT NULL, " +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_1 + " INTEGER," +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_2 + " INTEGER," +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_3 + " INTEGER," +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_4 + " INTEGER," +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_5 + " INTEGER," +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_HEATINGTHRESHOLD + " REAL, " +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COOLINGTHRESHOLD + " REAL, " +
                ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_BRIGHTNESSTHRESHOLD + " INTEGER );";
        db.execSQL(createProfileTable);


        String createServerTable = "CREATE TABLE " + ServerContract.ServerEntry.TABLE_NAME + " ( " +
                ServerContract.ServerEntry.COLUMN_NAME_SERVER_HOST + " TEXT NOT NULL," +
                ServerContract.ServerEntry.COLUMN_NAME_SERVER_PORT + " TEXT NOT NULL," +
                ServerContract.ServerEntry.COLUMN_NAME_SERVER_ROW_ID + " INTEGER PRIMARY KEY" +
                ");";
        db.execSQL(createServerTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProfileContract.ProfilesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ServerContract.ServerEntry.TABLE_NAME);
        onCreate(db);
    }

}

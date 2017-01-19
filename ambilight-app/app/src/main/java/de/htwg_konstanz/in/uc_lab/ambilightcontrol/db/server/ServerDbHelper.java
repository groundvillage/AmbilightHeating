package de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.server.ServerDAO;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.DbHelper;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.server.ServerContract.ServerEntry;

public class ServerDbHelper extends DbHelper {

    private static final String NUMBER_OF_ENTRIES_FROM_DB = "5";
    private static final String TAG = "ServerDbHelper";

    public ServerDbHelper(Context context){
        super(context);
    }

    public ArrayList<ServerDAO> getPreviousServerConnections(){
        ArrayList<ServerDAO> received = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                ServerEntry.COLUMN_NAME_SERVER_HOST,
                ServerEntry.COLUMN_NAME_SERVER_PORT
        };

        Cursor c = db.query(ServerEntry.TABLE_NAME, projection, null, null, null, null,
                ServerEntry.COLUMN_NAME_SERVER_ROW_ID + " DESC", NUMBER_OF_ENTRIES_FROM_DB);

        int hostIndex = c.getColumnIndex(ServerEntry.COLUMN_NAME_SERVER_HOST);
        int portIndex = c.getColumnIndex(ServerEntry.COLUMN_NAME_SERVER_PORT);

        while(c.moveToNext()){
            received.add(new ServerDAO(c.getString(hostIndex), c.getString(portIndex)));
        }

        c.close();
        db.close();

        return received;
    }

    public void saveServerConnectionSettingsToDb(String host, String port){
        ServerDAO profile = new ServerDAO(host, port);
        ContentValues values = getDbMap(profile);
        SQLiteDatabase db = this.getWritableDatabase();

        db.insertWithOnConflict(ServerContract.ServerEntry.TABLE_NAME,
                null,
                values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    private ContentValues getDbMap(ServerDAO server) {
        ContentValues values = new ContentValues();
        values.put(ServerContract.ServerEntry.COLUMN_NAME_SERVER_HOST, server.getHost());
        values.put(ServerContract.ServerEntry.COLUMN_NAME_SERVER_PORT, server.getPort());
        return values;
    }
}

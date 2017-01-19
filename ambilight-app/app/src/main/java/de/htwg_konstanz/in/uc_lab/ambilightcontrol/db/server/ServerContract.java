package de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.server;

import android.provider.BaseColumns;

/**
 * If everything works right this class was
 * created by konraifen88 on 19.05.2016.
 * If it doesn't work I don't know who the hell wrote it.
 */
public final class ServerContract {

    public ServerContract(){}

    public static abstract class ServerEntry implements BaseColumns{
        public static final String TABLE_NAME = "server";
        public static final String COLUMN_NAME_SERVER_HOST = "host";
        public static final String COLUMN_NAME_SERVER_PORT = "port";
        public static final String COLUMN_NAME_SERVER_ROW_ID = "rowid";

    }
}

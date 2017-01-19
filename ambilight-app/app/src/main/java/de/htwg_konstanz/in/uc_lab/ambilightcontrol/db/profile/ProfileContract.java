package de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.profile;

import android.provider.BaseColumns;

public final class ProfileContract {

    public ProfileContract(){}

    public static abstract class ProfilesEntry implements BaseColumns{
        public static final String TABLE_NAME = "newProfile";
        public static final String COLUMN_NAME_PROFILE_NAME = "name";
        public static final String COLUMN_NAME_PROFILE_COLOR_1 = "color1";
        public static final String COLUMN_NAME_PROFILE_COLOR_2 = "color2";
        public static final String COLUMN_NAME_PROFILE_COLOR_3 = "color3";
        public static final String COLUMN_NAME_PROFILE_COLOR_4 = "color4";
        public static final String COLUMN_NAME_PROFILE_COLOR_5 = "color5";
        public static final String COLUMN_NAME_PROFILE_HEATINGTHRESHOLD = "heatingthreshold";
        public static final String COLUMN_NAME_PROFILE_COOLINGTHRESHOLD = "coolingthreshold";
        public static final String COLUMN_NAME_PROFILE_BRIGHTNESSTHRESHOLD = "brightnessthreshold";

    }
}

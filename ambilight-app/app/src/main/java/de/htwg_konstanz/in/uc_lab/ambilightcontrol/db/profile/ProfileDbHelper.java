package de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.DbContract;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.DbHelper;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile.ProfileDAO;

public class ProfileDbHelper extends DbHelper {

    private static final String TAG = "ProfileDbHelper";

    public ProfileDbHelper(Context context){
        super(context);
    }

    public ArrayList<ProfileDAO> getProfilesStoredInDB(){
        ArrayList<ProfileDAO> profileList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(ProfileContract.ProfilesEntry.TABLE_NAME, null, null, null, null, null, null);

        int nameIndex = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_NAME);
        int color1Index = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_1);
        int color2Index = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_2);
        int color3Index = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_3);
        int color4Index = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_4);
        int color5Index = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_5);
        int heatingIndex = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_HEATINGTHRESHOLD);
        int coolingIndex = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COOLINGTHRESHOLD);
        int brightnessIndex = c.getColumnIndex(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_BRIGHTNESSTHRESHOLD);

        while(c.moveToNext()){
            ArrayList<Integer> colorList = new ArrayList<>();
            colorList.add(c.getInt(color1Index));
            colorList.add(c.getInt(color2Index));
            colorList.add(c.getInt(color3Index));
            colorList.add(c.getInt(color4Index));
            colorList.add(c.getInt(color5Index));
            profileList.add(new ProfileDAO(c.getString(nameIndex), colorList, c.getDouble(heatingIndex), c.getDouble(coolingIndex), c.getInt(brightnessIndex)));
        }
        c.close();
        db.close();
        return profileList;
    }

    public void saveColorProfileToDB(String name, List<Integer> colors, double heating, double cooling, int brightness){
        ProfileDAO profile = new ProfileDAO(name, colors, heating, cooling, brightness);
        ContentValues values = getDbMap(profile);
        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(ProfileContract.ProfilesEntry.TABLE_NAME,
                null,
                values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    private ContentValues getDbMap(ProfileDAO profile) {
        ContentValues values = new ContentValues();
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_NAME, profile.getName());
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_1, profile.getColor(0));
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_2, profile.getColor(1));
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_3, profile.getColor(2));
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_4, profile.getColor(3));
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COLOR_5, profile.getColor(4));
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_HEATINGTHRESHOLD, profile.getHeatingThreshold());
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_COOLINGTHRESHOLD, profile.getCoolingThreshold());
        values.put(ProfileContract.ProfilesEntry.COLUMN_NAME_PROFILE_BRIGHTNESSTHRESHOLD, profile.getBrightnessThreshold());

        return values;
    }

}

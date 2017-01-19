package de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileDAO {

    public static final int MAX_COLORS = 5;

    private final String name;
    private final double heatingThreshold;
    private final double coolingThreshold;
    private final int brightnessThreshold;
    private final List<Integer> colors;

    private static final String TAG = "ProfileDAO";

    public ProfileDAO(String name, List<Integer> colors, double heating, double cooling, int brightness){
        this.name = name;
        this.colors = new ArrayList<>();
        this.heatingThreshold = heating;
        this.coolingThreshold = cooling;
        this.brightnessThreshold = brightness;

        for (int i: colors) {
            if (i != -1) {
                this.colors.add(i);
            }
        }
    }

    public ProfileDAO(String name, List<Integer> colors) {
        this(name, colors, 20.0, 20.0, 1);
    }

    public double getBrightnessThreshold() {
        return brightnessThreshold;
    }

    public double getHeatingThreshold() {
        return heatingThreshold;
    }

    public double getCoolingThreshold() {
        return coolingThreshold;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public int getColor(int i) {
        if (i > MAX_COLORS - 1) {
            throw new IndexOutOfBoundsException();
        }
        if (i < colors.size()) {
            return colors.get(i);
        } else {
            return -1;
        }
    }

    public String getName() {
        return name;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject profileJson = new JSONObject();
        JSONObject colorsJson = new JSONObject();
        colorsJson.put("count", this.colors.size());
        JSONArray colorsArrayJson = new JSONArray();
        for(int i = 0; i < colors.size(); i++) {
            int color = colors.get(i);
            JSONObject colorJson = new JSONObject();
            colorJson.put("r", Color.red(color));
            colorJson.put("g", Color.green(color));
            colorJson.put("b", Color.blue(color));
            colorsArrayJson.put(colorJson);
        }
        colorsJson.put("colors", colorsArrayJson);

        profileJson.put("heating", this.heatingThreshold);
        profileJson.put("cooling", this.coolingThreshold);
        profileJson.put("brightness", this.brightnessThreshold);
        profileJson.put("color", colorsJson);
        return profileJson;
    }
}

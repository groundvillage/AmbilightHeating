package de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.R;

public class ColorAdapter extends ArrayAdapter<Integer> {

    private static final String TAG = "ColorAdapter";


    public ColorAdapter(Context context, ArrayList<Integer> profiles) {
        super(context, 0, profiles);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        int color = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_color, parent, false);
        }

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layColorItem);
        layout.setBackgroundColor(color);

        return convertView;
    }


}

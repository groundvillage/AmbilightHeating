package de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.R;

public class ProfileAdapter extends ArrayAdapter<ProfileDAO> {

    private static final String TAG = "ProfileAdapater";


    public ProfileAdapter(Context context, ArrayList<ProfileDAO> profiles) {
        super(context, 0, profiles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ProfileDAO prof = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_profile, parent, false);
        }

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layProfileItem);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_profile_title);
        Button btnRemove = (Button) convertView.findViewById(R.id.btnProfileActivate);

        layout.setBackgroundColor(prof.getColor(0));
        tvName.setText(prof.getName());

        return convertView;
    }
}

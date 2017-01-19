package de.htwg_konstanz.in.uc_lab.ambilightcontrol.server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.R;


public class ServerAdapter extends ArrayAdapter<ServerDAO> {

    public ServerAdapter(Context context, ArrayList<ServerDAO> profiles) {
        super(context, 0, profiles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ServerDAO srv = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_found_server, parent, false);
        }

        TextView tvHost = (TextView) convertView.findViewById(R.id.tvServerHost);
        TextView tvPort = (TextView) convertView.findViewById(R.id.tvServerPort);

        tvHost.setText(srv.getHost());
        tvPort.setText(srv.getPort());

        return convertView;
    }
}

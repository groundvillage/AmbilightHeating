package de.htwg_konstanz.in.uc_lab.ambilightcontrol.server;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.net.InetAddresses;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.R;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.server.ServerDbHelper;


public class ServerActivity extends AppCompatActivity {

    private static final String TAG = "ServerActivity";
    private ServerDbHelper dbHelper;
    private ServerAdapter mAdapter;
    private EditText txtIp;
    private EditText txtPort;

    ArrayList<ServerDAO> servers;
    private View selectedServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        dbHelper = new ServerDbHelper(this);
        initButtons();
        txtIp = (EditText) findViewById(R.id.etServerHostName);
        txtPort = (EditText) findViewById(R.id.etServerPortName);
        updateUI();
        selectSelectedServer();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        selectSelectedServer();
    }

    private void updateUI() {
        servers = dbHelper.getPreviousServerConnections();

        if (mAdapter == null) {
            mAdapter = new ServerAdapter(this, servers);
            ListView listView = (ListView) findViewById(R.id.listFoundServer);
            if (listView != null) {
                listView.setAdapter(mAdapter);
            }
        } else {
            mAdapter.clear();
            mAdapter.addAll(servers);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initButtons() {
        initCancelButton();
        initLoginButton();
    }

    private void initCancelButton() {
        Button btnCancel = (Button) findViewById(R.id.btnServerCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent output = new Intent();
                    if (selectedServer != null) {
                        TextView deselectedHost = (TextView) selectedServer.findViewById(R.id.tvServerHost);
                        TextView deselectedPort = (TextView) selectedServer.findViewById(R.id.tvServerPort);

                        output.putExtra("host", deselectedHost.getText().toString());
                        output.putExtra("port", deselectedPort.getText().toString());
                        setResult(RESULT_OK, output);
                        finish();
                    }
                }
            });
        }
    }

    private void initLoginButton() {
        Button btnLogin = (Button) findViewById(R.id.btnServerLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                String ip = txtIp.getText().toString();
                String port = txtPort.getText().toString();
                if (!ip.isEmpty() && !port.isEmpty() && InetAddresses.isInetAddress(ip)) {
                    if (isAlreadyInUse(ip, port)) {
                        Toast.makeText(getApplicationContext(), R.string.txtServerAlreadyInUseMessage, Toast.LENGTH_LONG).show();
                    } else {
                        if (isNetworkAvailable()) {
                            Toast.makeText(getApplicationContext(), ip + ":" + port, Toast.LENGTH_LONG).show();

                            dbHelper.saveServerConnectionSettingsToDb(ip, port);
                            Intent output = new Intent();
                            output.putExtra("host", ip);
                            output.putExtra("port", port);
                            setResult(RESULT_OK, output);
                            finish();
                            //ServerConnection c = new ServerConnection();
                            //c.execute(ip, port);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.txtNetworkUnreachableMessage, Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), R.string.txtServerIpOrPortEmpty, Toast.LENGTH_LONG).show();
                }
                }
            });
        }
    }

    private boolean isAlreadyInUse(String ip, String port) {
        for(ServerDAO server: servers) {
            if (server.getHost().equals(ip) && server.getPort().equals(port))  {
                return true;
            }
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    public void chooseServer(View view) {
        View parent = (View) view.getParent();

        TextView serverHost = (TextView) parent.findViewById(R.id.tvServerHost);
        TextView serverPort = (TextView) parent.findViewById(R.id.tvServerPort);

        String host = serverHost.getText().toString();
        String port = serverPort.getText().toString();

        Toast.makeText(getApplicationContext(), "choosed: " + host + ":" + port, Toast.LENGTH_SHORT).show();

        if (selectedServer != null) {
            TextView deselectedHost = (TextView) selectedServer.findViewById(R.id.tvServerHost);
            TextView deselectedPort = (TextView) selectedServer.findViewById(R.id.tvServerPort);

            deselectedHost.setBackgroundColor(0);
            deselectedPort.setBackgroundColor(0);

            deselectedHost.setTextColor(0xFF000000);
            deselectedPort.setTextColor(0xFF000000);
        }

        serverHost.setBackgroundColor(0xFF666666);
        serverHost.setTextColor(0xFFFFFFFF);
        serverPort.setBackgroundColor(0xFF666666);
        serverPort.setTextColor(0xFFFFFFFF);

        this.selectedServer = parent;
        Intent output = new Intent();
        output.putExtra("host", host);
        output.putExtra("port", port);
        setResult(RESULT_OK, output);
        finish();
    }

    private void selectSelectedServer() {
        Intent intent = this.getIntent();
        if (intent.hasExtra("host") && intent.hasExtra("port")) {
            String host = intent.getStringExtra("host");
            String port = intent.getStringExtra("port");
            for(ServerDAO server: servers) {
                if (server.getHost().equals(host) && server.getPort().equals(port))  {
                    Log.d(TAG, "shoud select dies DAO");
                }
            }
        }
    }
}
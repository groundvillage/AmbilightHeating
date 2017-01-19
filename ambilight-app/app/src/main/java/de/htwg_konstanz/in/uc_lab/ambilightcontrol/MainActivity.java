package de.htwg_konstanz.in.uc_lab.ambilightcontrol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.profile.ProfileDbHelper;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile.ProfileAdapter;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile.ProfileCreatorActivity;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile.ProfileDAO;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.server.ServerActivity;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.server.ServerDAO;

import static de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.profile.ProfileContract.ProfilesEntry;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ProfileDbHelper dbHelper;
    private ProfileAdapter mAdapter;

    private ArrayList<ProfileDAO> profileList;
    private View activatedProfileView;
    private ProfileDAO activatedProfil;
    private ServerDAO activatedServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new ProfileDbHelper(this);
        updateUI();
        //colorTest();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateUI();
    }


    private void updateUI() {
        profileList = dbHelper.getProfilesStoredInDB();
        addPermanentProfilesToList();

        Collections.reverse(profileList);

        if(mAdapter == null){
            mAdapter = new ProfileAdapter(this, profileList);
            ListView listView = (ListView) findViewById(R.id.list_profile);
            if (listView != null) {
                listView.setAdapter(mAdapter);
            }
        } else {
            mAdapter.clear();
            mAdapter.addAll(profileList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addPermanentProfilesToList() {
        //TODO: Better possibility to add permanent profiles
        List<Integer> colorList = new ArrayList<>();
        colorList.add(0xFF00FF00);
        profileList.add(new ProfileDAO("Default", colorList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_add_task:
                intent = new Intent(getApplicationContext(), ProfileCreatorActivity.class);
                startActivity(intent);
                return true;
            case R.id.addServerTask:
                intent = new Intent(getApplicationContext(), ServerActivity.class);

                if (this.activatedServer != null) {
                    intent.putExtra("host", this.activatedServer.getHost());
                    intent.putExtra("port", this.activatedServer.getPort());
                }
                startActivityForResult(intent, 1);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {

            Bundle res = data.getExtras();
            String host = data.getStringExtra("host");
            String port = data.getStringExtra("port");

            activatedServer = new ServerDAO(host, port);
            // The user picked a contact.
            // The Intent's data Uri identifies which contact was selected.
            // Do something with the contact here (bigger example below)
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void deleteTask(View view){
        View parent = (View) view.getParent();
        TextView profileTextView = (TextView) parent.findViewById(R.id.tv_profile_title);
        String task = String.valueOf(profileTextView.getText());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ProfilesEntry.TABLE_NAME, ProfilesEntry.COLUMN_NAME_PROFILE_NAME + " = ?", new String[]{task});
        db.close();
        updateUI();
    }

    public void activateProfile(View view) {
        if (activatedServer == null) {
            Toast.makeText(getApplicationContext(), R.string.txtSelectServerWarning, Toast.LENGTH_SHORT).show();
            return;
        }

        View parent = (View) view.getParent();

        TextView profileTextView = (TextView) parent.findViewById(R.id.tv_profile_title);
        Button profileActivateButton = (Button) parent.findViewById(R.id.btnProfileActivate);

        Toast.makeText(getApplicationContext(), "activated: " + profileTextView.getText(), Toast.LENGTH_SHORT).show();

        profileActivateButton.setEnabled(false);
        profileActivateButton.setVisibility(View.INVISIBLE);

        if (activatedProfileView != null) {
            Button profileDeactivateProfileButton = (Button) activatedProfileView.findViewById(R.id.btnProfileActivate);
            profileDeactivateProfileButton.setEnabled(true);
            profileDeactivateProfileButton.setVisibility(View.VISIBLE);
        }

        activatedProfileView = parent;
        activatedProfil = getProfileDAO(profileTextView.getText().toString());

        ServerConnection c = new ServerConnection();
        c.execute(activatedServer.getHost(), activatedServer.getPort());
    }

    public void callColorChangeOnServer(View view){
        Log.d(TAG, "Color change called");
    }

    private ProfileDAO getProfileDAO(String name) {
        for (ProfileDAO prof: profileList) {
            if (prof.getName().equals(name)) {
                return prof;
            }
        }
        return null;
    }

    private void deselectActiveProfile() {
        if (activatedProfileView != null) {
            Button profileDeactivateProfileButton = (Button) activatedProfileView.findViewById(R.id.btnProfileActivate);
            profileDeactivateProfileButton.setEnabled(true);
            profileDeactivateProfileButton.setVisibility(View.VISIBLE);
        }
    }

    private class ServerConnection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String host = params[0];
            int port = Integer.parseInt(params[1]);

            try {

                InetAddress hostadd = InetAddress.getByName(host);

                Log.d(TAG, "doInBackground");

                Socket socket = new Socket(hostadd, port);
                Log.d(TAG, "sucessfully connected to" + host + ":" + port);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                dos.writeBytes(activatedProfil.toJSON().toString());

                Log.d(TAG, "send data to Host: " + host + " : " + port);

                /*BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new DataInputStream(socket.getInputStream())));
                */
                /*Log.d(TAG, "Socket closed");
                String str;
                /*while((str = br.readLine()) != null){
                    Log.d(TAG, str);
                }*/
                socket.close();
                //return br.toString();*/


            } catch (IOException e) {
                Log.d(TAG, "Connection to " + host + ":" + port + " failed! IOExcpetion");
                e.printStackTrace();
                Log.getStackTraceString(e);
                return "ERROR";
                //Toast.makeText(getApplicationContext(), R.string.txtProfileTransferErrorMessage, Toast.LENGTH_SHORT).show();
                //deselectActiveProfile();
            } catch (Exception e) {
                Log.d(TAG, "Connection to " + host + ":" + port + " failed! Exception");
                Log.getStackTraceString(e);
                e.printStackTrace();
            }
            Log.d(TAG, "time to return");
            return null;
        }


        protected void onPostExecute(String result) {
            Log.d(TAG, "Result: " + result);
            if (result.equals("ERROR")) {
                Toast.makeText(getApplicationContext(), R.string.txtProfileTransferErrorMessage, Toast.LENGTH_SHORT).show();
                deselectActiveProfile();
            }
        }

    }
}

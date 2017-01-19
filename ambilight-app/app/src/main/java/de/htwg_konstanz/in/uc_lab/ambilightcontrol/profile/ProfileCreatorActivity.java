package de.htwg_konstanz.in.uc_lab.ambilightcontrol.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.Collections;

import de.htwg_konstanz.in.uc_lab.ambilightcontrol.R;
import de.htwg_konstanz.in.uc_lab.ambilightcontrol.db.profile.ProfileDbHelper;

public class ProfileCreatorActivity extends AppCompatActivity {

    private static final String TAG = "ProfileCreatorActivity";
    private static final String[] BRIGHTNESS_VALUES = {"dim", "normal", "bright"};

    private ProfileDbHelper dbHelper;
    private EditText txtProfileName;
    private TextView txtThreasholdHigh;
    private TextView txtThreasholdLow;

    private static int DEFTHREASHOLDLOW = 20;
    private static int DEFTHREASHOLDHIGH = 25;

    private int threasholdHigh;
    private int threasholdLow;

    private Button btnDecreaseThreshLow;
    private Button btnIncreaseThreshLow;

    private Button btnDecreaseThreshHigh;
    private Button btnIncreaseThreshHigh;

    private SeekBar skbBrightnessBar;
    private TextView txtBrightnessThreashold;

    private Button btnAddColor;
    private ListView colorList;

    private ArrayList<Integer> colorArrayList;
    private ColorAdapter mAdapter;

    private int mSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dbHelper = new ProfileDbHelper(this);

        initThresholdLow();
        initThresholdHigh();
        initTxtProfileName();
        initBtnCancel();
        initBtnSave();
        initBrightness();
        initAddColor();
        initThreasholdListener();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
            // Make sure the request was successful
            Log.d(TAG, "ich emfpfange etwas!");
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
            }

    }*/

    public void addColor(int colorValue) {

        Color c = new Color();

        mAdapter.add(colorValue);
        mAdapter.notifyDataSetChanged();

        if (colorArrayList.size() ==5) {
            btnAddColor.setEnabled(false);
        }
    }

    private void initAddColor() {
        btnAddColor = (Button) findViewById(R.id.btnNewProfilAddColor);
        colorList = (ListView) findViewById(R.id.list_color);
        colorArrayList = new ArrayList<>();

        mAdapter = new ColorAdapter(this, colorArrayList);
        ListView listView = (ListView) findViewById(R.id.list_color);
        if (listView != null) {
            listView.setAdapter(mAdapter);
        }

        if (btnAddColor != null) {
            btnAddColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] mColors = getResources().getIntArray(R.array.ambiColors);

                    ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                            mColors,
                            mSelectedColor,
                            5, // Number of columns
                            ColorPickerDialog.SIZE_SMALL);

                    dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                        @Override
                        public void onColorSelected(int color) {
                            mSelectedColor = color;
                            addColor(mSelectedColor);
                        }

                    });

                    dialog.show(getFragmentManager(), "color_dialog_test");
                }
            });
        }
    }

    private void initBrightness() {
        skbBrightnessBar = (SeekBar) findViewById(R.id.skbNewProfileBrightnessSeekBar);
        txtBrightnessThreashold = (TextView) findViewById(R.id.txtNewProfileBrightnessField);

        txtBrightnessThreashold.setText(BRIGHTNESS_VALUES[skbBrightnessBar.getProgress()]);

        skbBrightnessBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        txtBrightnessThreashold.setText(BRIGHTNESS_VALUES[skbBrightnessBar.getProgress()]);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        txtBrightnessThreashold.setText(BRIGHTNESS_VALUES[skbBrightnessBar.getProgress()]);
                    }
                }
        );

    }

    private void initThresholdLow() {

        txtThreasholdLow = (TextView) findViewById(R.id.txtNewProfileThreshlow);
        threasholdLow = DEFTHREASHOLDLOW;
        txtThreasholdLow.setText(threasholdLow +  "°C");

        btnDecreaseThreshLow = (Button) findViewById(R.id.btnNewProfileDecreaseTreshLow);
        btnIncreaseThreshLow = (Button) findViewById(R.id.btnNewProfileIncreaseTreshLow);

    }

    private void initThresholdHigh() {

        txtThreasholdHigh = (TextView) findViewById(R.id.txtNewProfileThreshHigh);
        threasholdHigh = DEFTHREASHOLDHIGH;
        txtThreasholdHigh.setText(threasholdHigh +  "°C");

        btnDecreaseThreshHigh = (Button) findViewById(R.id.btnNewProfileDecreaseTreshHigh);
        btnIncreaseThreshHigh = (Button) findViewById(R.id.btnNewProfileIncreaseTreshHigh);

    }

    private void initThreasholdListener() {

        if(btnDecreaseThreshLow != null) {
            btnDecreaseThreshLow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (threasholdLow == threasholdHigh) {
                        btnDecreaseThreshHigh.setEnabled(true);
                        btnIncreaseThreshLow.setEnabled(true);
                    }
                    txtThreasholdLow.setText(--threasholdLow + "°C");
                    if (threasholdLow == 0) {
                        btnDecreaseThreshLow.setEnabled(false);
                    }

                }
            });
        }

        if(btnIncreaseThreshLow != null) {
            btnIncreaseThreshLow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (threasholdLow == 0) {
                        btnDecreaseThreshLow.setEnabled(true);
                    }
                    txtThreasholdLow.setText(++threasholdLow + "°C");
                    if(threasholdLow == threasholdHigh) {
                        btnIncreaseThreshLow.setEnabled(false);
                        btnDecreaseThreshHigh.setEnabled(false);
                    }
                }
            });
        }

        if(btnDecreaseThreshHigh != null) {
            btnDecreaseThreshHigh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)  {
                    if (threasholdHigh == 50) {
                        btnIncreaseThreshHigh.setEnabled(true);
                    }
                    txtThreasholdHigh.setText(--threasholdHigh + "°C");
                    if (threasholdLow == threasholdHigh) {
                        btnDecreaseThreshHigh.setEnabled(false);
                        btnIncreaseThreshLow.setEnabled(false);
                    }
                }
            });
        }

        if(btnIncreaseThreshHigh != null) {
            btnIncreaseThreshHigh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(threasholdLow == threasholdHigh) {
                        btnIncreaseThreshLow.setEnabled(true);
                        btnDecreaseThreshHigh.setEnabled(true);
                    }
                    txtThreasholdHigh.setText(++threasholdHigh + "°C");
                    if (threasholdHigh == 50) {
                        btnIncreaseThreshHigh.setEnabled(false);
                    }

                }
            });
        }
    }

    private void initTxtProfileName() {
        txtProfileName = (EditText) findViewById(R.id.txtNewProfileNameField);
    }

    private void initBtnCancel() {
        Button btnCancel = (Button) findViewById(R.id.btnCancelEditProfile);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initBtnSave() {
        Button btnSave = (Button) findViewById(R.id.btnSaveProfile);
        if (btnSave != null) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = txtProfileName.getText().toString();
                    if(name.isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.txtEmptyNameFieldMessage, Toast.LENGTH_LONG).show();
                    } else if (colorList.getCount() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.txtEmptyColorFieldMessage, Toast.LENGTH_LONG).show();
                    } else {
                        dbHelper.saveColorProfileToDB(name, colorArrayList, threasholdHigh, threasholdLow, skbBrightnessBar.getProgress());
                        finish();
                    }
                }
            });
        }
    }
}

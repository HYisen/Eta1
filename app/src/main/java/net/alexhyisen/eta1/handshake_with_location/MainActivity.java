package net.alexhyisen.eta1.handshake_with_location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import net.alexhyisen.eta1.other.MyCallback;
import net.alexhyisen.eta1.R;
import net.alexhyisen.eta1.setting.SettingsActivity;
import net.alexhyisen.eta1.telnet.TelnetActivity;
import net.alexhyisen.eta1.other.ToolbarOwner;
import net.alexhyisen.eta1.other.Utility;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements MyCallback<String>, ToolbarOwner {
    @Override
    public void accept(String data) {
        printfMsg("Server", data);
    }

    public void handleNowButtonAction(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("request permission " + Manifest.permission.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 17);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("request permission " + Manifest.permission.ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 17);
        }
        System.out.println("requested");
        lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, ll, null);
    }

    public void setLocationAutoUpdate(boolean value) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("request permission " + Manifest.permission.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 17);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("request permission " + Manifest.permission.ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 17);
        }

        if (value) {
            System.out.println("enable auto update");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, ll);
        } else {
            System.out.println("disable auto update");
            lm.removeUpdates(ll);
        }
    }

    private TextView msgTextView;
    private EditText inputEditText;

    //import com.google.android.gms.common.api.GoogleApiClient;
    private LocationManager lm;

    private void recordMsg(CharSequence msg) {
        printfMsg("Client", msg);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        AsyncTask<String, Void, String> task = new HandshakeTask(this,
                sp.getString("pref_host", "error_host"),
                Integer.valueOf(sp.getString("pref_port", "-1")));
        task.execute(msg.toString());
    }

    private void printfMsg(CharSequence user, CharSequence info) {
        String time = Calendar.getInstance().getTime().toString();
        //Once I switch to Android O, java.time will replace the f**king old Calender.
        msgTextView.setText(time + "\t" + user + "\n" +
                info + "\n" + msgTextView.getText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Utility.setupToolbar(this, false);

        msgTextView = (TextView) findViewById(R.id.msgTextView);
        inputEditText = (EditText) findViewById(R.id.inputEditText);
        Switch locationSwitch = (Switch) findViewById(R.id.locationSwitch);

        Utility.setupEditText(inputEditText, EditorInfo.IME_ACTION_SEND, new MyCallback<TextView>() {
            @Override
            public void accept(TextView data) {
                handleSendButtonAction(data);
            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setLocationAutoUpdate(isChecked);
            }
        });

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        System.out.println("init complete");

    }

    public void handleSendButtonAction(View view) {
        recordMsg(inputEditText.getText());
        inputEditText.setText(null);
    }

    private LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            System.out.println("received");
            updateLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (String permission : permissions) {
            System.out.println("allowed " + permission);
        }
    }

    private void updateLocation(Location l) {
        String s = "null location";
        if (l != null) {
            s = l.toString();
        }
        recordMsg(s);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_telnet:
                startActivity(new Intent(this, TelnetActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

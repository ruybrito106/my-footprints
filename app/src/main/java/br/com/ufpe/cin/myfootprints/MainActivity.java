package br.com.ufpe.cin.myfootprints;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 200;
    private static final String[] PERMISSIONS = {
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
    };

    private TextView mTextMessage;
    private String phoneNumber;
    private TimePickerDialog picker;
    private EditText startDate;
    private EditText endDate;

    private LocationUpdateDAO dbInstance;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
            }
            return false;
        }
    };

    private boolean canAccessLocation() {
        return this.checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            Intent locationUpdateServiceIntent = new Intent(this, br.com.ufpe.cin.myfootprints.LocationUpdateService.class);
            startService(locationUpdateServiceIntent);
        }
    }

    private List<LocationUpdate> getLocationUpdatesByTimeRange(Date beginDate, Date endDate) {
        return dbInstance.getLocationUpdatesByDateRange(beginDate, endDate);
    }

    private void populateView() {

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);

        startDate.setInputType(InputType.TYPE_NULL);
        endDate.setInputType(InputType.TYPE_NULL);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                picker = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                startDate.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                picker = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                endDate.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        Date current = new Date();
        DateHelper helper = new DateHelper();

        Date beginDate = helper.atStartOfDay(current);
        Date endDate = helper.atEndOfDay(current);

        List<LocationUpdate> path = getLocationUpdatesByTimeRange(beginDate, endDate);
        String pathStr = "";

        for (int i = 0; i < path.size(); i++) {
            LocationUpdate x = path.get(i);
            pathStr += Double.toString(x.getLat()) + " " + Double.toString(x.getLng()) + " -> " + Long.toString(x.getTimestampSeconds());
        }

        final EditText pathText = (EditText) findViewById(R.id.routeText);
        Button pathButton = (Button) findViewById(R.id.findRoute);

        final String finalPathStr = pathStr;
        pathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathText.setText(finalPathStr);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        if (!canAccessLocation()) {
            this.requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        } else {
            Intent locationUpdateServiceIntent = new Intent(this, br.com.ufpe.cin.myfootprints.LocationUpdateService.class);
            startService(locationUpdateServiceIntent);
        }

        dbInstance = LocationUpdateDAO.getInstance(this);
        populateView();
    }
}

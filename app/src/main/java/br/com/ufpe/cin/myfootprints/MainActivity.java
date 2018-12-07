package br.com.ufpe.cin.myfootprints;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity implements OnDateSetListener {

    private static final int PERMISSIONS_REQUEST_CODE = 200;
    private static final String[] PERMISSIONS = {
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
    };

    private TextView mTextMessage;
    private String phoneNumber;
    private EditText startDate;
    private EditText endDate;
    private DateFormat dateFormat;


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

    @Override
    public void onDateSet(String type, int year, int month, int day) {
        if(type.equals("START")){
            startDate.setText(dateFormat.format(new Date(year, month, day)));
        } else {
            endDate.setText(dateFormat.format(new Date(year, month, day)));
        }
    }

    private void populateView() {

        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);

        startDate.setText(dateFormat.format(Calendar.getInstance().getTime()));
        startDate.setInputType(InputType.TYPE_NULL);
        endDate.setText(dateFormat.format(Calendar.getInstance().getTime()));
        endDate.setInputType(InputType.TYPE_NULL);


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final TextView pathText = (TextView) findViewById(R.id.routeText);
        pathText.setMovementMethod(new ScrollingMovementMethod());

        Button pathButton = (Button) findViewById(R.id.findRoute);

        pathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date current = new Date();
                DateHelper helper = new DateHelper();

                List<LocationUpdate> path = getLocationUpdatesByTimeRange(helper.atStartOfDay(current), helper.atEndOfDay(current));
                String pathStr = "";

                for (int i = 0; i < path.size(); i++) {
                    pathStr += path.get(i).toString() + " ";
                }

                pathText.setText(pathStr);
            }
        });

    }

    public void showStartDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateSetListener(this, "START");
        newFragment.show(getSupportFragmentManager(), "startDatePicker");
    }

    public void showEndDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateSetListener(this, "END");
        newFragment.show(getSupportFragmentManager(), "startDatePicker");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat =android.text.format.DateFormat.getDateFormat(getApplicationContext());
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

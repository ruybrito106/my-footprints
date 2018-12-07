package br.com.ufpe.cin.myfootprints;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.SEND_SMS;


public class MainActivity extends AppCompatActivity implements OnDateSetListener, OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_CODE = 200;
    private static final String[] LOCATION_PERMISSIONS = {
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
    };
    private static final String[] SMS_PERMISSIONS = {
            READ_CONTACTS,
            SEND_SMS,
    };

    private String phoneNumber;
    private EditText startDateText;
    private EditText endDateText;
    private Date startDate;
    private Date endDate;
    private DateFormat dateFormat;
    private GoogleMap mMap;

    private boolean canSendSMS;
    private LocationUpdateDAO dbInstance;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
            }
            return false;
        }
    };

    private boolean canAccessLocation() {
        return this.checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasGrantedPermission(String permission, String[] permissions) {
        for (String p : permissions) {
            if (p == permission) {
                return true;
            }
        }
        return false;
    }

    private boolean canReadContactsAndSendSMS() {
        return this.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (hasGrantedPermission(ACCESS_FINE_LOCATION, permissions) || hasGrantedPermission(ACCESS_COARSE_LOCATION, permissions)) {
                Intent locationUpdateServiceIntent = new Intent(this, br.com.ufpe.cin.myfootprints.LocationUpdateService.class);
                startService(locationUpdateServiceIntent);
            } else if (hasGrantedPermission(SEND_SMS, permissions) || hasGrantedPermission(READ_CONTACTS, permissions)) {
                this.canSendSMS = true;
            }
        }
    }

    private List<LocationUpdate> getLocationUpdatesByTimeRange(Date beginDate, Date endDate) {
        return dbInstance.getLocationUpdatesByDateRange(beginDate, endDate);
    }

    @Override
    public void onDateSet(String type, int year, int month, int day) {
        Date date  = new Date(year, month, day);
        DateHelper helper = new DateHelper();
        if(type.equals("START")){
            startDateText.setText(dateFormat.format(date));
            startDate = helper.atStartOfDay(date);
        } else {
            endDateText.setText(dateFormat.format(date));
            endDate = helper.atEndOfDay(date);
        }
    }

    private void populateView() {

        startDateText = (EditText) findViewById(R.id.startDate);
        endDateText = (EditText) findViewById(R.id.endDate);

        startDateText.setText(dateFormat.format(Calendar.getInstance().getTime()));
        startDateText.setInputType(InputType.TYPE_NULL);
        endDateText.setText(dateFormat.format(Calendar.getInstance().getTime()));
        endDateText.setInputType(InputType.TYPE_NULL);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Button pathButton = (Button) findViewById(R.id.findRoute);

        pathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMap();

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
        newFragment.show(getSupportFragmentManager(), "endDatePicker");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        Date current = new Date();
        DateHelper helper = new DateHelper();

        startDate = helper.atStartOfDay(current);
        endDate = helper.atEndOfDay(current);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!canAccessLocation()) {
            this.requestPermissions(LOCATION_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        } else {
            Intent locationUpdateServiceIntent = new Intent(this, br.com.ufpe.cin.myfootprints.LocationUpdateService.class);
            startService(locationUpdateServiceIntent);
        }

        if (!canReadContactsAndSendSMS()) {
            this.requestPermissions(SMS_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        } else {
            canSendSMS = true;
        }

        dbInstance = LocationUpdateDAO.getInstance(this);
        populateView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
    }

    void updateMap(){
        mMap.clear();
        List<LocationUpdate> path = getLocationUpdatesByTimeRange(startDate, endDate);
        for(LocationUpdate location : path){
            LatLng latlng = new LatLng(location.getLat(), location.getLng());
            String label = dateFormat.format(new Date((long)location.getTimestampSeconds()*1000));
            mMap.addMarker(new MarkerOptions().position(latlng).title(label));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }
}

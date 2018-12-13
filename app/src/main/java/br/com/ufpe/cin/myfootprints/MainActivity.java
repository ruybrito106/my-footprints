package br.com.ufpe.cin.myfootprints;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.RECEIVE_SMS;


public class MainActivity extends AppCompatActivity implements OnDateSetListener, OnMapReadyCallback {

    private static final int HOME_MODE = 0;
    private static final int FRIENDS_MODE = 1;

    private static final int PERMISSIONS_REQUEST_CODE = 200;
    private static final int SELECT_CONTACT_REQUEST_CODE = 201;

    private static final String[] LOCATION_PERMISSIONS = { ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION };
    private static final String[] SMS_PERMISSIONS = { READ_CONTACTS, SEND_SMS, RECEIVE_SMS };

    private EditText startDateText;
    private EditText endDateText;
    private Date startDate;
    private Date endDate;
    private DateFormat dateFormat;
    private DateFormat labelDateFormat;
    private GoogleMap mMap;

    private boolean canSendSMS;
    private int currentMode;

    private LocationUpdateDAO dbInstance;

    private void hideHomeElements() {
        startDateText.setVisibility(View.INVISIBLE);
        endDateText.setVisibility(View.INVISIBLE);

        findViewById(R.id.myRectangleView).setVisibility(View.INVISIBLE);
        findViewById(R.id.editTextSeparator).setVisibility(View.INVISIBLE);

        getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.INVISIBLE);

        findViewById(R.id.findRoute).setVisibility(View.INVISIBLE);
        findViewById(R.id.shareLocation).setVisibility(View.INVISIBLE);
    }

    private void showHomeElements() {
        startDateText.setVisibility(View.VISIBLE);
        endDateText.setVisibility(View.VISIBLE);

        findViewById(R.id.myRectangleView).setVisibility(View.VISIBLE);
        findViewById(R.id.editTextSeparator).setVisibility(View.VISIBLE);
        getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.VISIBLE);

        findViewById(R.id.findRoute).setVisibility(View.VISIBLE);
        findViewById(R.id.shareLocation).setVisibility(View.VISIBLE);
    }

    private void inflateFriendsFragment(int state) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (state) {
            case HOME_MODE:
                FriendsFragment fragment = (FriendsFragment) getSupportFragmentManager().findFragmentById(R.id.friendsFragment);
                ft.remove(fragment).commit();
                showHomeElements();
                break;
            case FRIENDS_MODE:
                FriendsFragment f = new FriendsFragment();
                ft.add(R.id.friendsFragment, f).commit();
                hideHomeElements();
                break;
        }

    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        BroadcastReceiver receiver = new SMSListener();
        registerReceiver(receiver, intentFilter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            currentMode = (currentMode + 1) % 2;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    inflateFriendsFragment(currentMode);
                    return true;
                case R.id.navigation_dashboard:
                    inflateFriendsFragment(currentMode);
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
                this.checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (hasGrantedPermission(ACCESS_FINE_LOCATION, permissions) || hasGrantedPermission(ACCESS_COARSE_LOCATION, permissions)) {
                Intent locationUpdateServiceIntent = new Intent(this, br.com.ufpe.cin.myfootprints.LocationUpdateService.class);
                startService(locationUpdateServiceIntent);
            } else if (hasGrantedPermission(SEND_SMS, permissions) || hasGrantedPermission(READ_CONTACTS, permissions) || hasGrantedPermission(READ_SMS, permissions)) {
                this.canSendSMS = true;
                registerBroadcastReceiver();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CONTACT_REQUEST_CODE && resultCode == RESULT_OK) {
            ContactPicker picker = new ContactPicker();
            picker.setContactMobileNumber(this, data);

            final long MILLISECONDS_IN_A_DAY = 24 * 3600 * 1000;
            if (endDate.getTime() - startDate.getTime() > MILLISECONDS_IN_A_DAY) {
                Toast.makeText(this, "Unable to share data for multiple days!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<LocationUpdate> path = getLocationUpdatesByTimeRange(startDate, endDate);

            String date = dateFormat.format(startDate);
            String contactNumber = picker.getContactNumber();
            SMSHelper helper = new SMSHelper(contactNumber, date, path);

            switch (helper.sendSMS()) {
                case SMSHelper.ERROR_NOT_ENOUGH_VISITS:
                    Toast.makeText(this, "Not enough visits", Toast.LENGTH_SHORT).show();
                    break;
                case SMSHelper.ERROR_TOO_MANY_VISITS:
                    Toast.makeText(this, "SMS length too long", Toast.LENGTH_SHORT).show();
                    break;
                case SMSHelper.SUCCESS:
                    Toast.makeText(this, "Location shared", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private List<LocationUpdate> getLocationUpdatesByTimeRange(Date beginDate, Date endDate) {
        return dbInstance.getLocationUpdatesByDateRange(beginDate, endDate);
    }

    @Override
    public void onDateSet(String type, int year, int month, int day) {
        Calendar dateSet = Calendar.getInstance();
        dateSet.set(year, month, day, 0, 0, 0);
        Date date = dateSet.getTime();
        DateHelper helper = new DateHelper();
        if(type.equals("START")){
            startDateText.setText(dateFormat.format(date).substring(0,5));
            startDate = helper.atStartOfDay(date);
        } else {
            endDateText.setText(dateFormat.format(date).substring(0,5));
            endDate = helper.atEndOfDay(date);
        }
    }

    private void populateView() {

        startDateText = (EditText) findViewById(R.id.startDate);
        endDateText = (EditText) findViewById(R.id.endDate);

        startDateText.setText(dateFormat.format(Calendar.getInstance().getTime()).substring(0,5));
        startDateText.setInputType(InputType.TYPE_NULL);
        endDateText.setText(dateFormat.format(Calendar.getInstance().getTime()).substring(0,5));
        endDateText.setInputType(InputType.TYPE_NULL);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Button pathButton = (Button) findViewById(R.id.findRoute);
        Button shareButton = (Button) findViewById(R.id.shareLocation);

        pathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMap(false);
            }
        });

        final Context ctx = this;
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!canSendSMS) {
                    Toast.makeText(ctx, "Permission to send SMS revoked.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent actionPickIntent = new Intent(Intent.ACTION_PICK);
                actionPickIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(actionPickIntent, SELECT_CONTACT_REQUEST_CODE);
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

        currentMode = HOME_MODE;

        dbInstance = LocationUpdateDAO.getInstance(this);

        labelDateFormat = DateFormat.getInstance();
        dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        Date current = new Date();
        DateHelper helper = new DateHelper();

        startDate = helper.atStartOfDay(current);
        endDate = helper.atEndOfDay(current);

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
            registerBroadcastReceiver();
        }

        populateView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMap(true);
    }

    void updateMap(boolean firstTime){
        mMap.clear();
        List<LocationUpdate> path = getLocationUpdatesByTimeRange(startDate, endDate);
        for(LocationUpdate location : path){
            LatLng latlng = new LatLng(location.getLat(), location.getLng());
            String label = labelDateFormat.format(new Date((long)location.getTimestampSeconds()*1000));
            mMap.addMarker(new MarkerOptions().position(latlng).title(label));
            if(firstTime) mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

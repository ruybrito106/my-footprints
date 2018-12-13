package br.com.ufpe.cin.myfootprints

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

import java.text.DateFormat
import java.util.Calendar
import java.util.Date

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.READ_SMS
import android.Manifest.permission.SEND_SMS
import android.Manifest.permission.RECEIVE_SMS
import android.app.Activity


class MainActivity : AppCompatActivity(), OnDateSetListener, OnMapReadyCallback {

    private var startDateText: EditText? = null
    private var endDateText: EditText? = null
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var dateFormat: DateFormat? = null
    private var labelDateFormat: DateFormat? = null
    private var mMap: GoogleMap? = null

    private var canSendSMS: Boolean = false
    private var currentMode: Int = 0

    private var dbInstance: LocationUpdateDAO? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        currentMode = (currentMode + 1) % 2
        when (item.itemId) {
            R.id.navigation_home -> {
                inflateFriendsFragment(currentMode)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                inflateFriendsFragment(currentMode)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun hideHomeElements() {
        startDateText!!.visibility = View.INVISIBLE
        endDateText!!.visibility = View.INVISIBLE

        findViewById<View>(R.id.myRectangleView).visibility = View.INVISIBLE
        findViewById<View>(R.id.editTextSeparator).visibility = View.INVISIBLE

        supportFragmentManager.findFragmentById(R.id.map)!!.view!!.visibility = View.INVISIBLE

        findViewById<View>(R.id.findRoute).visibility = View.INVISIBLE
        findViewById<View>(R.id.shareLocation).visibility = View.INVISIBLE
    }

    private fun showHomeElements() {
        startDateText!!.visibility = View.VISIBLE
        endDateText!!.visibility = View.VISIBLE

        findViewById<View>(R.id.myRectangleView).visibility = View.VISIBLE
        findViewById<View>(R.id.editTextSeparator).visibility = View.VISIBLE
        supportFragmentManager.findFragmentById(R.id.map)!!.view!!.visibility = View.VISIBLE

        findViewById<View>(R.id.findRoute).visibility = View.VISIBLE
        findViewById<View>(R.id.shareLocation).visibility = View.VISIBLE
    }

    private fun inflateFriendsFragment(state: Int) {

        val ft = supportFragmentManager.beginTransaction()

        when (state) {
            HOME_MODE -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.friendsFragment) as FriendsFragment?
                ft.remove(fragment!!).commit()
                showHomeElements()
            }
            FRIENDS_MODE -> {
                val f = FriendsFragment()
                ft.add(R.id.friendsFragment, f).commit()
                hideHomeElements()
            }
        }

    }

    private fun registerBroadcastReceiver() {
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        val receiver = SMSListener()
        registerReceiver(receiver, intentFilter)
    }

    private fun canAccessLocation(): Boolean {
        return this.checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasGrantedPermission(permission: String, permissions: Array<String>): Boolean {
        for (p in permissions) {
            if (p === permission) {
                return true
            }
        }
        return false
    }

    private fun canReadContactsAndSendSMS(): Boolean {
        return this.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (hasGrantedPermission(ACCESS_FINE_LOCATION, permissions) || hasGrantedPermission(ACCESS_COARSE_LOCATION, permissions)) {
                val locationUpdateServiceIntent = Intent(this, br.com.ufpe.cin.myfootprints.LocationUpdateService::class.java)
                startService(locationUpdateServiceIntent)
            } else if (hasGrantedPermission(SEND_SMS, permissions) || hasGrantedPermission(READ_CONTACTS, permissions) || hasGrantedPermission(READ_SMS, permissions)) {
                this.canSendSMS = true
                registerBroadcastReceiver()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_CONTACT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val picker = ContactPicker()
            picker.setContactMobileNumber(this, data)

            val MILLISECONDS_IN_A_DAY = (24 * 3600 * 1000).toLong()
            if (endDate!!.time - startDate!!.time > MILLISECONDS_IN_A_DAY) {
                Toast.makeText(this, "Unable to share data for multiple days!", Toast.LENGTH_SHORT).show()
                return
            }

            val path = getLocationUpdatesByTimeRange(startDate, endDate)

            val date = dateFormat!!.format(startDate)
            val contactNumber = picker.contactNumber
            val helper = SMSHelper(contactNumber, date, path)

            when (helper.sendSMS()) {
                SMSHelper.ERROR_NOT_ENOUGH_VISITS -> Toast.makeText(this, "Not enough visits", Toast.LENGTH_SHORT).show()
                SMSHelper.ERROR_TOO_MANY_VISITS -> Toast.makeText(this, "SMS length too long", Toast.LENGTH_SHORT).show()
                SMSHelper.SUCCESS -> Toast.makeText(this, "Location shared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocationUpdatesByTimeRange(beginDate: Date?, endDate: Date?): List<LocationUpdate> {
        return dbInstance!!.getLocationUpdatesByDateRange(beginDate, endDate)
    }

    override fun onDateSet(type: String?, year: Int, month: Int, day: Int) {
        val dateSet = Calendar.getInstance()
        dateSet.set(year, month, day, 0, 0, 0)
        val date = dateSet.time
        val helper = DateHelper
        if (type == "START") {
            startDateText!!.setText(dateFormat!!.format(date).substring(0, 5))
            startDate = helper.atStartOfDay(date)
        } else {
            endDateText!!.setText(dateFormat!!.format(date).substring(0, 5))
            endDate = helper.atEndOfDay(date)
        }
    }

    private fun populateView() {

        startDateText = findViewById<View>(R.id.startDate) as EditText
        endDateText = findViewById<View>(R.id.endDate) as EditText

        startDateText!!.setText(dateFormat!!.format(Calendar.getInstance().time).substring(0, 5))
        startDateText!!.inputType = InputType.TYPE_NULL
        endDateText!!.setText(dateFormat!!.format(Calendar.getInstance().time).substring(0, 5))
        endDateText!!.inputType = InputType.TYPE_NULL

        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val pathButton = findViewById<View>(R.id.findRoute) as Button
        val shareButton = findViewById<View>(R.id.shareLocation) as Button

        pathButton.setOnClickListener { updateMap(false) }

        val ctx = this
        shareButton.setOnClickListener(View.OnClickListener {
            if (!canSendSMS) {
                Toast.makeText(ctx, "Permission to send SMS revoked.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            val actionPickIntent = Intent(Intent.ACTION_PICK)
            actionPickIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(actionPickIntent, SELECT_CONTACT_REQUEST_CODE)
        })

    }

    fun showStartDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.setOnDateSetListener(this, "START")
        newFragment.show(supportFragmentManager, "startDatePicker")
    }

    fun showEndDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.setOnDateSetListener(this, "END")
        newFragment.show(supportFragmentManager, "endDatePicker")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentMode = HOME_MODE

        dbInstance = LocationUpdateDAO.getInstance(this)

        labelDateFormat = DateFormat.getInstance()
        dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)

        val current = Date()
        val helper = DateHelper

        startDate = helper.atStartOfDay(current)
        endDate = helper.atEndOfDay(current)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        if (!canAccessLocation()) {
            this.requestPermissions(LOCATION_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        } else {
            val locationUpdateServiceIntent = Intent(this, br.com.ufpe.cin.myfootprints.LocationUpdateService::class.java)
            startService(locationUpdateServiceIntent)
        }

        if (!canReadContactsAndSendSMS()) {
            this.requestPermissions(SMS_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        } else {
            canSendSMS = true
            registerBroadcastReceiver()
        }

        populateView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        updateMap(true)
    }

    internal fun updateMap(firstTime: Boolean) {
        mMap!!.clear()
        val path = getLocationUpdatesByTimeRange(startDate, endDate)
        for (location in path) {
            val latlng = LatLng(location.lat, location.lng)
            val label = labelDateFormat!!.format(Date(location.timestampSeconds.toLong() * 1000))
            mMap!!.addMarker(MarkerOptions().position(latlng).title(label))
            if (firstTime) mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latlng))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val HOME_MODE = 0
        private val FRIENDS_MODE = 1

        private val PERMISSIONS_REQUEST_CODE = 200
        private val SELECT_CONTACT_REQUEST_CODE = 201

        private val LOCATION_PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        private val SMS_PERMISSIONS = arrayOf(READ_CONTACTS, SEND_SMS, RECEIVE_SMS)
    }
}

package br.com.ufpe.cin.myfootprints

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.util.Date


class MapFragment : DialogFragment(), OnMapReadyCallback {

    var friend: FriendSharedLocation? = null
    private var mMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = fragmentManager!!
                .findFragmentById(R.id.map2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val closeMapView = view.findViewById<Button>(R.id.dispatchMapButton)
        closeMapView.setOnClickListener { onDestroyView() }

        dialog.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onDestroyView() {

        val fm = fragmentManager

        val xmlFragment = fm!!.findFragmentById(R.id.map2)
        if (xmlFragment != null) {
            fm.beginTransaction().remove(xmlFragment).commit()
        }

        super.onDestroyView()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.clear()
        val path = friend?.path
        for (location in path!!) {
            val latlng = LatLng(location.lat, location.lng)
            mMap!!.addMarker(MarkerOptions().position(latlng))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latlng))
        }
    }

    companion object {
        private val view: View? = null

        fun newInstance(friend: FriendSharedLocation): MapFragment {
            val frag = MapFragment()
            frag.friend = friend
            return frag
        }
    }

}
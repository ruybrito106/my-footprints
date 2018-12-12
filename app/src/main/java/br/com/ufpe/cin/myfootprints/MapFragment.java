package br.com.ufpe.cin.myfootprints;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.List;

public class MapFragment extends DialogFragment implements OnMapReadyCallback {

    public FriendSharedLocation friend;
    private static View view;
    private GoogleMap mMap;



    public MapFragment() { }

    public static MapFragment newInstance(FriendSharedLocation friend) {
        MapFragment frag = new MapFragment();
        frag.friend = friend;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                    .findFragmentById(R.id.map2);
            mapFragment.getMapAsync(this);

            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    @Override
    public void onDestroyView() {

        FragmentManager fm = getFragmentManager();

        Fragment xmlFragment = fm.findFragmentById(R.id.map2);
        if (xmlFragment != null) {
            fm.beginTransaction().remove(xmlFragment).commit();
        }

        super.onDestroyView();
    }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.clear();
            List<LocationUpdate> path = friend.getPath();
            for(LocationUpdate location : path){
                LatLng latlng = new LatLng(location.getLat(), location.getLng());
                mMap.addMarker(new MarkerOptions().position(latlng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            }
        }

    }
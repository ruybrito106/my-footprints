package br.com.ufpe.cin.myfootprints;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.flags.impl.DataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsFragment extends ListFragment {

    private FriendSharedLocationDAO dbInstance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.friends_view, parent, false);
        dbInstance = FriendSharedLocationDAO.getInstance(parent.getContext());

        List<String> friends = dbInstance.getFriendSharedLocations();
        FriendSharedLocationParser parser = new FriendSharedLocationParser();

        ArrayList<String>  adapterData = new ArrayList<String>();
        for(String friend: friends){
            if(parser.locationUpdatesFromSMSText(friend).getPath().size() > 0){
                adapterData.add(friend);
            }
        }

        String[] arrayAdapterData = new String[adapterData.size()];
        arrayAdapterData = adapterData.toArray(arrayAdapterData);

        FriendAdapter adapter= new FriendAdapter(getActivity(),arrayAdapterData);
        // Bind adapter to the ListFragment
        setListAdapter(adapter);
        //  Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);
        setListAdapter(adapter);
        return v;
    }


}

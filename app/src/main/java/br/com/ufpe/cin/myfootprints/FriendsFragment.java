package br.com.ufpe.cin.myfootprints;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

public class FriendsFragment extends Fragment {

    private EditText sharedList;
    private FriendSharedLocationDAO dbInstance;

    public void showSharedLocations(List<String> sharedLocations) {
        String text = "";
        for(String x : sharedLocations) {
            text += x + "\n";
        }
        sharedList.setText(text);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.showSharedLocations(dbInstance.getFriendSharedLocations());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.friends_view, parent, false);
        sharedList = v.findViewById(R.id.friendsList);
        dbInstance = FriendSharedLocationDAO.getInstance(parent.getContext());
        return v;
    }

}

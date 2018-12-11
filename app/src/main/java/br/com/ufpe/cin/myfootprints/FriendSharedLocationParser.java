package br.com.ufpe.cin.myfootprints;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendSharedLocationParser {

    public static final String STATUS_NOT_ENOUGH_POINTS = "001";
    public static final String STATUS_TOO_MANY_POINTS = "002";

    public static String locationUpdatesToSMSText(List<LocationUpdate> as, String date, String mobilePhone) {

        if (as.size() == 0) {
            return STATUS_NOT_ENOUGH_POINTS;
        }

        String text = mobilePhone + ":" + date + ":";

        text += as.get(0).toString(false, null);
        for(int i = 1; i < as.size(); i++) {
            text += as.get(i).toString(true, as.get(i-1));
        }

        return (text.length() >= 160) ? STATUS_TOO_MANY_POINTS : text;

    }

    public static FriendSharedLocation locationUpdatesFromSMSText(String smsText) {

        try{
            String friendContactNumber;
            String sharedDate;
            List<LocationUpdate> friendPath = new ArrayList<>();

            String[] tmp = smsText.split(":");
            friendContactNumber = tmp[0];
            sharedDate = tmp[1];

            String[] tmp2 = tmp[2].split(",");
            for (String str : tmp2) {
                if (str.length() > 0) {
                    LocationUpdate visit = LocationUpdate.fromGeohash(str.substring(0, 9));
                    visit.setTimestampSeconds(Integer.parseInt(str.substring(9)));
                    if (friendPath.size() > 0) {
                        visit.setTimestampSeconds(visit.getTimestampSeconds() + friendPath.get(friendPath.size()-1).getTimestampSeconds());
                    }
                    friendPath.add(visit);
                }
            }

            return new FriendSharedLocation(friendContactNumber, sharedDate, friendPath);
        } catch(Exception err) {
            return new FriendSharedLocation("", "", new ArrayList<LocationUpdate>());
        }


    }

}

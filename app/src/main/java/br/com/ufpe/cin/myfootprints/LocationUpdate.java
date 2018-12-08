package br.com.ufpe.cin.myfootprints;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.hsr.geohash.*;

import java.util.ArrayList;
import java.util.List;

public class LocationUpdate {

    public static final String STATUS_NOT_ENOUGH_POINTS = "001";
    public static final String STATUS_TOO_MANY_POINTS = "002";

    private static final double DISTANCE_THRESHOLD = 150.0;
    private static final double TIME_THRESHOLD = 20 * 60;
    private static final int EARTH_RADIUS = 6371;

    public LocationUpdate(double lat, double lng, int timestampSeconds) {
        this.lat = lat;
        this.lng = lng;
        this.timestampSeconds = timestampSeconds;
    }

    private double lat;
    private double lng;
    private int timestampSeconds;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getTimestampSeconds() {
        return timestampSeconds;
    }

    public void setTimestampSeconds(int timestampSeconds) {
        this.timestampSeconds = timestampSeconds;
    }

    private String toGeohash() {
        GeoHash geohash = GeoHash.withCharacterPrecision(lat, lng, 9);
        return geohash.toBase32();
    }

    private static LocationUpdate fromGeohash(String strGeohash) {
        GeoHash geohash = GeoHash.fromGeohashString(strGeohash);
        WGS84Point pt = geohash.getBoundingBoxCenterPoint();
        return new LocationUpdate(pt.getLatitude(), pt.getLongitude(), 0);
    }

    public String toString(boolean withDifference, LocationUpdate prev) {

        if (withDifference) {
            Integer deltaTs = this.timestampSeconds - prev.getTimestampSeconds();
            return String.format("%s%d,", this.toGeohash(), deltaTs);
        }

        return String.format("%s%d,", this.toGeohash(), this.timestampSeconds);

    }

    private static double distance(double lat1, double lat2, double lon1, double lon2) {

        double dLat  = Math.toRadians((lat2 - lat1));
        double dLong = Math.toRadians((lon2 - lon1));

        lat1 = Math.toRadians(lat1);
        lat2   = Math.toRadians(lat2);

        double hLat = Math.pow(Math.sin(dLat / 2), 2);
        double hLng = Math.pow(Math.sin(dLong / 2), 2);

        double a = hLat + Math.cos(lat1) * Math.cos(lat2) * hLng;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c * 1000.0;

    }

    public static boolean isAnotherVisit(LocationUpdate cur, LocationUpdate next)  {
        int timeOffset = Math.abs(next.getTimestampSeconds() - cur.timestampSeconds);
        double distanceOffset = Math.abs(distance(cur.lat, next.lat, cur.lng, next.lng));
        return timeOffset >= TIME_THRESHOLD && distanceOffset >= DISTANCE_THRESHOLD;
    }

    public static List<LocationUpdate> filterRealVisits(List<LocationUpdate> as) {
        List<LocationUpdate> filtered = new ArrayList<>();
        if (as.size() > 0) {
            filtered.add(as.get(0));
            LocationUpdate top = as.get(0);
            for(int i = 1; i < as.size(); i++) {
                LocationUpdate current = as.get(i);
                if (isAnotherVisit(current, top)) {
                    filtered.add(current);
                    top = current;
                }
            }
        }
        return filtered;
    }

    public static String locationUpdatesToSMSText(List<LocationUpdate> as) {

        if (as.size() == 0) {
            return STATUS_NOT_ENOUGH_POINTS;
        }

        String userMobileNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String text = userMobileNumber + ":";

        text += as.get(0).toString(false, null);
        for(int i = 1; i < as.size(); i++) {
            text += as.get(i).toString(true, as.get(i-1));
        }

        return (text.length() >= 160) ? STATUS_TOO_MANY_POINTS : text;

    }

    public static FriendSharedLocation locationUpdatesFromSMSText(String smsText) {

        String friendContactNumber;
        List<LocationUpdate> friendPath = new ArrayList<>();

        String[] tmp = smsText.split(":");
        friendContactNumber = tmp[0];

        String[] tmp2 = tmp[1].split(",");
        for (String str : tmp2) {
            if (str.length() > 0) {
                LocationUpdate visit = fromGeohash(str.substring(0, 9));
                visit.setTimestampSeconds(Integer.parseInt(str.substring(9)));
                if (friendPath.size() > 0) {
                    visit.setTimestampSeconds(visit.getTimestampSeconds() + friendPath.get(friendPath.size()-1).getTimestampSeconds());
                }
                friendPath.add(visit);
            }
        }

        return new FriendSharedLocation(friendContactNumber, friendPath);

    }

}

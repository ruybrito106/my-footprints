package br.com.ufpe.cin.myfootprints;

public class LocationUpdate {

    private static final double DISTANCE_THRESHOLD = 150.0;
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

    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c * 1000;
    }

    public boolean isNearby(LocationUpdate next)  {
        return distance(this.lat, this.lng, next.lat, next.lng) <= DISTANCE_THRESHOLD;
    }

}

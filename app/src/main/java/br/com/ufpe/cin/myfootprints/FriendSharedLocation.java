package br.com.ufpe.cin.myfootprints;

import java.util.List;

public class FriendSharedLocation {

    private String friendContactNumber;
    private String date;
    private List<LocationUpdate> path;

    public FriendSharedLocation(String friendContactNumber, String date, List<LocationUpdate> path) {
        this.friendContactNumber = friendContactNumber;
        this.path = path;
        this.date = date;
    }

    public String getFriendContactNumber() {
        return friendContactNumber;
    }

    public void setFriendContactNumber(String friendContactNumber) {
        this.friendContactNumber = friendContactNumber;
    }

    public List<LocationUpdate> getPath() {
        return path;
    }

    public void setPath(List<LocationUpdate> path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}

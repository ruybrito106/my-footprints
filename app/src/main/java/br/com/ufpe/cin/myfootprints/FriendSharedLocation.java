package br.com.ufpe.cin.myfootprints;

import java.util.List;

public class FriendSharedLocation {

    private String friendContactNumber;
    private List<LocationUpdate> path;

    public FriendSharedLocation(String friendContactNumber, List<LocationUpdate> path) {
        this.friendContactNumber = friendContactNumber;
        this.path = path;
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

}

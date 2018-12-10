package br.com.ufpe.cin.myfootprints;

import android.telephony.SmsManager;

import java.util.Date;
import java.util.List;

public class SMSHelper {

    public static final int SUCCESS = 0;
    public static final int ERROR_NOT_ENOUGH_VISITS = 1;
    public static final int ERROR_TOO_MANY_VISITS = 2;

    private String destinationNumber;
    private String date;
    private List<LocationUpdate> path;

    public SMSHelper(String destinationNumber, String date, List<LocationUpdate> path) {
        this.destinationNumber = destinationNumber;
        this.date = date;
        this.path = path;
    }

    public int sendSMS() {

        String smsText = FriendSharedLocationParser.locationUpdatesToSMSText(this.path, this.date);

        switch (smsText) {
            case FriendSharedLocationParser.STATUS_NOT_ENOUGH_POINTS:
                return ERROR_NOT_ENOUGH_VISITS;
            case FriendSharedLocationParser.STATUS_TOO_MANY_POINTS:
                return ERROR_TOO_MANY_VISITS;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                this.destinationNumber,
                null,
                smsText,
                null,
                null
        );

        return SUCCESS;

    }
}

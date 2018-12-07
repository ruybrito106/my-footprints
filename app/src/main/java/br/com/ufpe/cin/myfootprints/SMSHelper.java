package br.com.ufpe.cin.myfootprints;

import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

public class SMSHelper {

    private String destinationNumber;
    private List<LocationUpdate> path;

    public SMSHelper(String destinationNumber, List<LocationUpdate> path) {
        this.destinationNumber = destinationNumber;
        this.path = path;
    }

    public void sendSMS() {
        String smsText = LocationUpdate.locationUpdatesToSMSText(this.path);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                this.destinationNumber,
                null,
                smsText,
                null,
                null
        );
    }
}

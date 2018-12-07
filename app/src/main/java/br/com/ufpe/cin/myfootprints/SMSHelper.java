package br.com.ufpe.cin.myfootprints;

import android.telephony.SmsManager;

public class SMSHelper {

    private String destinationNumber;

    public SMSHelper(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    public void sendSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                this.destinationNumber,
                null,
                "sms message",
                null,
                null
        );
    }
}

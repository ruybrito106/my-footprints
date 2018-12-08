package br.com.ufpe.cin.myfootprints;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SMSListener extends BroadcastReceiver {

    private static final String SMS_LISTENER_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getAction().equals(SMS_LISTENER_ACTION)) {
            return;
        }

        Bundle bundle = intent.getExtras();
        List<SmsMessage> msgs = new ArrayList<>();
        String messageBody = "";

        if (bundle != null){
            try {
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pdu : pdus) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[])pdu);
                    messageBody = msg.getMessageBody();
                }
            } catch (Exception e) {
                // handle exception
            }
        }

        if (messageBody.length() > 0) {
            FriendSharedLocation sharedLocation = LocationUpdate.locationUpdatesFromSMSText(messageBody);
            Log.d("CONTACT_NUMBER", sharedLocation.getFriendContactNumber());
            for (LocationUpdate x : sharedLocation.getPath()) {
                Log.d("VISIT", x.toString(false, null));
            }
        }
    }
}


package br.com.ufpe.cin.myfootprints;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSListener extends BroadcastReceiver {

    private static final String SMS_LISTENER_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private FriendSharedLocationDAO dbInstance;

    private void initDBInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = FriendSharedLocationDAO.getInstance(context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getAction().equals(SMS_LISTENER_ACTION)) {
            return;
        }

        initDBInstance(context);

        Bundle bundle = intent.getExtras();
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
            dbInstance.insertFriendSharedLocation(messageBody);
        }
    }
}


package br.com.ufpe.cin.myfootprints

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage

class SMSListener : BroadcastReceiver() {
    private var dbInstance: FriendSharedLocationDAO? = null

    private fun initDBInstance(context: Context) {
        if (dbInstance == null) {
            dbInstance = FriendSharedLocationDAO.getInstance(context)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != SMS_LISTENER_ACTION) {
            return
        }

        initDBInstance(context)

        val bundle = intent.extras
        var messageBody = ""

        if (bundle != null) {
            try {
                val pdus = bundle.get("pdus") as Array<Any>
                for (pdu in pdus) {
                    val msg = SmsMessage.createFromPdu(pdu as ByteArray)
                    messageBody = msg.messageBody
                }
            } catch (e: Exception) {
                // handle exception
            }

        }

        if (messageBody.length > 0) {
            dbInstance!!.insertFriendSharedLocation(messageBody)
        }
    }

    companion object {

        private val SMS_LISTENER_ACTION = "android.provider.Telephony.SMS_RECEIVED"
    }
}


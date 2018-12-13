package br.com.ufpe.cin.myfootprints

import android.telephony.SmsManager

import com.google.firebase.auth.FirebaseAuth

import java.util.Date

class SMSHelper(private val destinationNumber: String?, private val date: String, private val path: List<LocationUpdate>) {

    fun sendSMS(): Int {

        val userMobileNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val smsText = FriendSharedLocationParser.locationUpdatesToSMSText(this.path, this.date, userMobileNumber)

        when (smsText) {
            FriendSharedLocationParser.STATUS_NOT_ENOUGH_POINTS -> return ERROR_NOT_ENOUGH_VISITS
            FriendSharedLocationParser.STATUS_TOO_MANY_POINTS -> return ERROR_TOO_MANY_VISITS
        }

        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
                this.destinationNumber,
                null,
                smsText, null, null
        )

        return SUCCESS

    }

    companion object {

        val SUCCESS = 0
        val ERROR_NOT_ENOUGH_VISITS = 1
        val ERROR_TOO_MANY_VISITS = 2
    }
}

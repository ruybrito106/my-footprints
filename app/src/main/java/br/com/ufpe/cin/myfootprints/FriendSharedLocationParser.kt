package br.com.ufpe.cin.myfootprints

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import java.util.ArrayList

object FriendSharedLocationParser {

    val STATUS_NOT_ENOUGH_POINTS = "001"
    val STATUS_TOO_MANY_POINTS = "002"

    fun locationUpdatesToSMSText(`as`: List<LocationUpdate>, date: String, mobilePhone: String?): String {

        if (`as`.size == 0) {
            return STATUS_NOT_ENOUGH_POINTS
        }

        var text = "$mobilePhone:$date:"

        text += `as`[0].toString(false, null)
        for (i in 1 until `as`.size) {
            text += `as`[i].toString(true, `as`[i - 1])
        }

        return if (text.length >= 160) STATUS_TOO_MANY_POINTS else text

    }

    fun locationUpdatesFromSMSText(smsText: String): FriendSharedLocation {

        try {
            val friendContactNumber: String
            val sharedDate: String
            val friendPath = ArrayList<LocationUpdate>()

            val tmp = smsText.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            friendContactNumber = tmp[0]
            sharedDate = tmp[1]

            val tmp2 = tmp[2].split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            for (str in tmp2) {
                if (str.length > 0) {
                    val visit = LocationUpdate.fromGeohash(str.substring(0, 9))
                    visit.timestampSeconds = Integer.parseInt(str.substring(9))
                    if (friendPath.size > 0) {
                        visit.timestampSeconds = visit.timestampSeconds + friendPath[friendPath.size - 1].timestampSeconds
                    }
                    friendPath.add(visit)
                }
            }

            return FriendSharedLocation(friendContactNumber, sharedDate, friendPath)
        } catch (err: Exception) {
            return FriendSharedLocation("", "", ArrayList())
        }


    }

}

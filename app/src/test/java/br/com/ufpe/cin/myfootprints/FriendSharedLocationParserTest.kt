package br.com.ufpe.cin.myfootprints

import org.junit.Assert.*
import org.junit.Test

import java.util.ArrayList
import java.util.Arrays

class FriendSharedLocationParserTest {

    private val locationUpdatesFixtureTooShort = ArrayList<LocationUpdate>()

    private val locationUpdatesFixture = ArrayList(Arrays.asList(
            LocationUpdate(-1.0, -1.0, 1000),
            LocationUpdate(-1.0, -1.0, 1001),
            LocationUpdate(-1.0, -1.000001, 1002),
            LocationUpdate(-1.0, -1.1, 3000),
            LocationUpdate(-1.0, -1.100001, 5000),
            LocationUpdate(-1.2, -1.100001, 7000)
    ))

    private val locationUpdatesFixtureTooLong = ArrayList(Arrays.asList(
            LocationUpdate(-1.0, -1.0, 1000),
            LocationUpdate(-2.0, -1.0, 3000),
            LocationUpdate(-3.0, -1.0, 5000),
            LocationUpdate(-4.0, -1.0, 7000),
            LocationUpdate(-5.0, -1.0, 9000),
            LocationUpdate(-6.0, -1.0, 11000),
            LocationUpdate(-7.0, -1.0, 13000),
            LocationUpdate(-8.0, -1.0, 15000),
            LocationUpdate(-9.0, -1.0, 17000),
            LocationUpdate(-1.0, -2.0, 19000)
    ))

    @Test
    fun locationUpdatesToSMSTextSuccess() {
        val smsText = FriendSharedLocationParser.locationUpdatesToSMSText(
                locationUpdatesFixture,
                dateFixture,
                firebaseUserMobilePhone)

        assertEquals(smsTextFixture, smsText)
    }

    @Test
    fun locationUpdatesToSMSTextTooShort() {
        val smsText = FriendSharedLocationParser.locationUpdatesToSMSText(
                locationUpdatesFixtureTooShort,
                dateFixture,
                firebaseUserMobilePhone)

        assertEquals(FriendSharedLocationParser.STATUS_NOT_ENOUGH_POINTS, smsText)
    }

    @Test
    fun locationUpdatesToSMSTextTooLong() {
        val smsText = FriendSharedLocationParser.locationUpdatesToSMSText(
                locationUpdatesFixtureTooLong,
                dateFixture,
                firebaseUserMobilePhone)

        assertEquals(FriendSharedLocationParser.STATUS_TOO_MANY_POINTS, smsText)
    }

    @Test
    fun locationUpdatesFromSMSTextSuccess() {
        val sample = FriendSharedLocationParser.locationUpdatesFromSMSText(smsTextFixture)
        assertEquals(dateFixture, sample.date)
        assertEquals(firebaseUserMobilePhone, sample.friendContactNumber)
    }

    companion object {

        private val dateFixture = "08/12/2018"
        private val firebaseUserMobilePhone = "+5581999999999"
        private val smsTextFixture = String.format(
                "%s:%s:%s",
                firebaseUserMobilePhone,
                dateFixture,
                "7zz631zyd1000,7zz631zyd1,7zz631zyd1,7zz4qczn81998,7zz4qczn82000,7zz1nvx122000,"
        )
    }

}

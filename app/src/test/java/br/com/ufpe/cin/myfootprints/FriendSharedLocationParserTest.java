package br.com.ufpe.cin.myfootprints;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendSharedLocationParserTest {

    private List<LocationUpdate> locationUpdatesFixtureTooShort = new ArrayList<>();

    private List<LocationUpdate> locationUpdatesFixture = new ArrayList<LocationUpdate>(Arrays.asList(
        new LocationUpdate(-1.0, -1.0, 1000),
        new LocationUpdate(-1.0, -1.0, 1001),
        new LocationUpdate(-1.0, -1.000001, 1002),
        new LocationUpdate(-1.0, -1.1, 3000),
        new LocationUpdate(-1.0, -1.100001, 5000),
        new LocationUpdate(-1.2, -1.100001, 7000)
    ));

    private List<LocationUpdate> locationUpdatesFixtureTooLong = new ArrayList<>(Arrays.asList(
        new LocationUpdate(-1.0, -1.0, 1000),
        new LocationUpdate(-2.0, -1.0, 3000),
        new LocationUpdate(-3.0, -1.0, 5000),
        new LocationUpdate(-4.0, -1.0, 7000),
        new LocationUpdate(-5.0, -1.0, 9000),
        new LocationUpdate(-6.0, -1.0, 11000),
        new LocationUpdate(-7.0, -1.0, 13000),
        new LocationUpdate(-8.0, -1.0, 15000),
        new LocationUpdate(-9.0, -1.0, 17000),
        new LocationUpdate(-1.0, -2.0, 19000)
    ));

    private static final String dateFixture = "08/12/2018";
    private static final String firebaseUserMobilePhone = "+5581999999999";
    private static final String smsTextFixture = String.format(
        "%s:%s:%s",
        firebaseUserMobilePhone,
        dateFixture,
        "7zz631zyd1000,7zz631zyd1,7zz631zyd1,7zz4qczn81998,7zz4qczn82000,7zz1nvx122000,"
    );

    @Test
    public void locationUpdatesToSMSTextSuccess() {
        String smsText = FriendSharedLocationParser.locationUpdatesToSMSText(
            locationUpdatesFixture,
            dateFixture,
            firebaseUserMobilePhone);

        assertEquals(smsTextFixture, smsText);
    }

    @Test
    public void locationUpdatesToSMSTextTooShort() {
        String smsText = FriendSharedLocationParser.locationUpdatesToSMSText(
                locationUpdatesFixtureTooShort,
                dateFixture,
                firebaseUserMobilePhone);

        assertEquals(FriendSharedLocationParser.STATUS_NOT_ENOUGH_POINTS, smsText);
    }

    @Test
    public void locationUpdatesToSMSTextTooLong() {
        String smsText = FriendSharedLocationParser.locationUpdatesToSMSText(
                locationUpdatesFixtureTooLong,
                dateFixture,
                firebaseUserMobilePhone);

        assertEquals(FriendSharedLocationParser.STATUS_TOO_MANY_POINTS, smsText);
    }

    @Test
    public void locationUpdatesFromSMSTextSuccess() {
        FriendSharedLocation sample = FriendSharedLocationParser.locationUpdatesFromSMSText(smsTextFixture);
        assertEquals(dateFixture, sample.getDate());
        assertEquals(firebaseUserMobilePhone, sample.getFriendContactNumber());
    }

}

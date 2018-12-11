package br.com.ufpe.cin.myfootprints;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Date;

public class DateHelperTest {

    private static final Date dateFixture = new Date();
    private static final double expectedDiffMilliseconds = 24 * 3600 * 1000;

    @Test
    public void startDateToEndDateComparison() {
        Date begin = DateHelper.atStartOfDay(dateFixture);
        Date end = DateHelper.atEndOfDay(dateFixture);
        double diff = end.getTime() - begin.getTime();

        // Consider admissible 2s error (runtime imprecision)
        assertEquals(expectedDiffMilliseconds, diff, 2000);
    }

}

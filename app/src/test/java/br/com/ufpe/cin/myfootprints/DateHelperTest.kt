package br.com.ufpe.cin.myfootprints

import org.junit.Assert.*
import org.junit.Test

import java.util.Date

class DateHelperTest {

    @Test
    fun startDateToEndDateComparison() {
        val begin = DateHelper.atStartOfDay(dateFixture)
        val end = DateHelper.atEndOfDay(dateFixture)
        val diff = (end.time - begin.time).toDouble()

        // Consider admissible 2s error (runtime imprecision)
        assertEquals(expectedDiffMilliseconds, diff, 2000.0)
    }

    companion object {

        private val dateFixture = Date()
        private val expectedDiffMilliseconds = (24 * 3600 * 1000).toDouble()
    }

}

package br.com.ufpe.cin.myfootprints

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    internal var mCallback: OnDateSetListener? = null
    internal var type: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity!!, this, year, month, day)
    }

    fun setOnDateSetListener(activity: MainActivity, type: String) {
        mCallback = activity
        this.type = type
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        mCallback?.onDateSet(type, year, month, day)
    }
}
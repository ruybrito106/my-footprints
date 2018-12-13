package br.com.ufpe.cin.myfootprints

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class FriendAdapter(context: Context?, private val values: Array<String>?) : ArrayAdapter<String>(context, 0, values) {
    private val parser: FriendSharedLocationParser

    init {
        this.parser = FriendSharedLocationParser
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context!!
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.friend_layout, parent, false)

        val location = this.parser.locationUpdatesFromSMSText(values!![position])

        val textView = rowView.findViewById<View>(R.id.txtitem) as TextView
        textView.setText(location.friendContactNumber)

        val textView2 = rowView.findViewById<View>(R.id.txtitem2) as TextView
        textView2.setText(location.date)

        return rowView
    }

    override fun getCount(): Int {
        return values!!.size
    }

}
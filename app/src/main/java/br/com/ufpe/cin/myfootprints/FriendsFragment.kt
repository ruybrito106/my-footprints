package br.com.ufpe.cin.myfootprints

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

import com.google.android.gms.flags.impl.DataUtils

import java.util.ArrayList
import java.util.Arrays

class FriendsFragment : ListFragment() {

    private var dbInstance: FriendSharedLocationDAO? = null
    private var arrayAdapterData: Array<String>? = null

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.friends_view, parent, false)
        dbInstance = FriendSharedLocationDAO.getInstance(parent!!.context)

        val friends = dbInstance!!.friendSharedLocations
        val parser = FriendSharedLocationParser

        val adapterData = ArrayList<String>()
        for (friend in friends) {
            val hasLocationData = parser.locationUpdatesFromSMSText(friend).path!!.size > 0
            val isDifferentFromPrevious = adapterData.isEmpty() || friend != adapterData[adapterData.size - 1]
            if (hasLocationData && isDifferentFromPrevious)
                adapterData.add(friend)
        }


        val arrayData = arrayOfNulls<String>(adapterData.size)
        arrayAdapterData = adapterData.toArray(arrayData)
        val data = arrayAdapterData

        val adapter = FriendAdapter(activity, data)
        // Bind adapter to the ListFragment
        listAdapter = adapter
        //  Retain the ListFragment instance across Activity re-creation
        retainInstance = true
        listAdapter = adapter
        return v
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val ft = fragmentManager!!.beginTransaction()
        val parser = FriendSharedLocationParser

        val f = MapFragment.newInstance(parser.locationUpdatesFromSMSText(arrayAdapterData!![position]))
        f.show(ft, "test")

    }


}

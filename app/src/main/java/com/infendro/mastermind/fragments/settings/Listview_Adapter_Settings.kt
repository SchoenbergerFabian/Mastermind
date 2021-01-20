package com.infendro.mastermind.fragments.settings

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.infendro.mastermind.R
import kotlinx.android.synthetic.main.listviewitem_settings.view.*

class Listview_Adapter_Settings(data: List<Setting>, private val context: Activity) : ArrayAdapter<Setting>(context, R.layout.listviewitem_settings, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = context.layoutInflater.inflate(R.layout.listviewitem_settings,null,true)

        layout.textView_Setting_Name.text = getItem(position)?.name
        layout.textView_Setting_Value.text = getItem(position)?.value

        return layout
    }

}
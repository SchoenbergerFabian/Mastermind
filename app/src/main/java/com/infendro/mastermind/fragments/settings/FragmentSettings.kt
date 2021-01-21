package com.infendro.mastermind.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.infendro.mastermind.MainActivity
import com.infendro.mastermind.R
import kotlinx.android.synthetic.main.fragment_main.*

class FragmentSettings : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MainActivity.hideKeyboard()
        listView.adapter = Listview_Adapter_Settings(MainActivity.settings,requireActivity())
    }
}
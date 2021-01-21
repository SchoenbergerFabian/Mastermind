package com.infendro.mastermind.fragments

import android.inputmethodservice.Keyboard
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.infendro.mastermind.MainActivity
import com.infendro.mastermind.R
import kotlinx.android.synthetic.main.fragment_score.*

class FragmentScore : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MainActivity.hideKeyboard()
        listView.adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_expandable_list_item_1,MainActivity.scores)
    }
}
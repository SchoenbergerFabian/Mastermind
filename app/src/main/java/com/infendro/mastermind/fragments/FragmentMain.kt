package com.infendro.mastermind.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.infendro.mastermind.MainActivity
import com.infendro.mastermind.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class FragmentMain : Fragment() {

    lateinit var adapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,MainActivity.guesses)

        listView.adapter = adapter

        buttonLoad.setOnClickListener { load() }
        buttonSave.setOnClickListener { save() }
        buttonSettings.setOnClickListener { toSettings() }
        buttonScore.setOnClickListener { toScore() }
        buttonSubmit.setOnClickListener { submit() }
        buttonNewGame.setOnClickListener { newGame() }
    }

    fun newGame(){
        MainActivity.reset()
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(),"New Game has been started!",Toast.LENGTH_SHORT).show()
    }

    fun submit(){
        val guess = editTextNextGuess.text.toString()

        if(MainActivity.ended){
            Toast.makeText(requireContext(),"Start a new game!",Toast.LENGTH_SHORT).show()
        }else if(!MainActivity.isLongEnough(guess)){
            Toast.makeText(requireContext(),"Invalid number of characters!",Toast.LENGTH_SHORT).show()
        }else if(!MainActivity.hasValidChars(guess)){
            Toast.makeText(requireContext(),"Invalid characters!",Toast.LENGTH_SHORT).show()
        }else{
            editTextNextGuess.text.clear()
            when(MainActivity.addGuess(guess)){
                2 -> Toast.makeText(requireContext(),"You won!",Toast.LENGTH_SHORT).show() //TODO add score
                1 -> Toast.makeText(requireContext(),"You lost!",Toast.LENGTH_SHORT).show() //TODO add score
            }
            adapter.notifyDataSetChanged()
        }
    }

    fun load(){
        MainActivity.load()
        adapter.notifyDataSetChanged()
    }

    fun save(){
        MainActivity.save()
    }

    fun toScore(){
        nav.findNavController().navigate(R.id.action_fragmentMain_to_fragmentScore)
    }

    fun toSettings(){
        findNavController().navigate(R.id.action_fragmentMain_to_fragmentSettings)
    }
}
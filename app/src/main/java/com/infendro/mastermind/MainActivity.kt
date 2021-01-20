package com.infendro.mastermind

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.infendro.mastermind.fragments.settings.Setting
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var settings : List<Setting>

        lateinit var alphabet : List<Char>
        var codeLength = 0
        var doubleAllowed = false
        var guessRounds = 0
        var correctPositionSign = ' '
        var correctCodeElementSign = ' '

        var guesses = ArrayList<String>()
        var code = ""

        var ended = true

        fun setUp(){
            code = getNewCode()
            ended = false
        }

        fun reset(){
            setUp()
            guesses.clear()
        }

        fun getNewCode() : String {
            var tempCode=""
            for (index in 1..codeLength){
                var character = getRandomChar()
                while(!doubleAllowed&&tempCode.contains(character)){
                    character = getRandomChar()
                }
                tempCode+=character
            }
            println(tempCode)
            return tempCode
        }

        private fun getRandomChar() : Char {
            return alphabet[(Math.random()*alphabet.size).toInt()]
        }

        fun isLongEnough(guess:String) : Boolean{
            return guess.length==code.length
        }

        fun hasValidChars(guess:String) : Boolean{
            for(character in guess.toCharArray()){
                if(!alphabet.contains(character)){
                    return false
                }
            }
            return true
        }

        fun addGuess(guess: String) : Int {
            if(code.equals(guess)){
                guesses.add(guess+"|SOLVED")
                ended = true
                return 2
            }else if(guesses.size+1==guessRounds){
                guesses.add(guess+"|NOT SOLVED")
                ended = true
                return 1
            }else{
                guesses.add(guess+"|"+evaluateGuess(guess))
                return 0
            }
        }

        fun evaluateGuess(guess : String) : String{
            var output = ""
            var usedIndexes = ArrayList<Int>()

            //correct position
            for((index, character) in guess.toCharArray().withIndex()){
                if(code[index]==character){
                    output+= correctPositionSign
                    usedIndexes.add(index)
                }
            }

            //correct code element
            for((index_Guess, character_Guess) in guess.toCharArray().withIndex()){
                if(code[index_Guess]!=character_Guess
                    && code.contains(character_Guess)){
                    for((index_Code, character_Code) in code.toCharArray().withIndex()){
                        if(!usedIndexes.contains(index_Code)
                            && character_Code==character_Guess){
                            output+= correctCodeElementSign
                            usedIndexes.add(index_Code)
                            break
                        }
                    }
                }
            }

            return output
        }

        fun load(){

        }

        fun save(){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settings = loadSettings()
        setUp()
    }

    fun loadSettings() : List<Setting>{
        val list = ArrayList<Setting>()

        val reader = BufferedReader(InputStreamReader(assets.open("config.conf", Context.MODE_PRIVATE)))
        var line = reader.readLine()
        while(line!=null){
            var arguments = line.split("=")
            if(arguments.size==2){
                val argument0 = arguments[0].replace(" ","")
                val argument1 = arguments[1].replace(" ","")
                when(argument0){
                    "alphabet" -> {
                        list.add(Setting(argument0,argument1))
                        alphabet = argument1.split(",").map { character -> character[0] }
                    }
                    "codeLength" -> {
                        list.add(Setting(argument0,argument1))
                        codeLength = Integer.parseInt(argument1)
                    }
                    "doubleAllowed" -> {
                        list.add(Setting(argument0,argument1))
                        doubleAllowed = argument1.toBoolean()
                    }
                    "guessRounds" -> {
                        list.add(Setting(argument0,argument1))
                        guessRounds = Integer.parseInt(argument1)
                    }
                    "correctPositionSign" -> {
                        list.add(Setting(argument0,argument1))
                        correctPositionSign = argument1[0]
                    }
                    "correctCodeElementSign" -> {
                        list.add(Setting(argument0,argument1))
                        correctCodeElementSign = argument1[0]
                    }
                }
            }
            line = reader.readLine()
        }

        return list
    }

}
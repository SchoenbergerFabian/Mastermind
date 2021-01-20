package com.infendro.mastermind

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        var alphabet = charArrayOf('1','2','3','4','5','6')//ArrayList<Char>()
        var codeLength = 4
        var doubleAllowed = true
        var guessRounds = 12
        var correctPositionSign = '+'
        var correctCodeElementSign = '-'

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
        setUp()
    }

    fun loadSettings(){

    }

}
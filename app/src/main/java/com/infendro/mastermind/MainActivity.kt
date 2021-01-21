package com.infendro.mastermind

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.infendro.mastermind.fragments.settings.Setting
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var activity: MainActivity

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

        var start : Long = 0
        var end : Long = 0
        var scores = ArrayList<String>()

        fun setUp(){
            code = getNewCode()
            ended = false
            start = System.currentTimeMillis()
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

        fun addScore(guess : String, rounds : Int, duration : Long){
            val calendar = Calendar.getInstance()
            var daytime = ""+calendar.get(Calendar.DAY_OF_MONTH)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.YEAR)+" ("+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+")"
            var seconds = (duration/1000)%60
            var minutes = (duration/1000)/60
            scores.add(daytime+" | $rounds Rounds | $minutes min $seconds sec")
            saveScores()
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

        fun fromXML(xml : String){
            var code: String
            var duration: Long
            var guesses = ArrayList<String>()
            var ended = false

            val lines = xml.replace("\t","").split("\n")

            if(lines.size>=4){
                if("<savestate>" == lines.first() && "</savestate>" == lines.last()){
                    //code
                    code = lines[1]
                    if(code.contains("<code>")&&code.contains("</code>")){
                        code = code.substring(code.indexOf("<code>")+6,code.indexOf("</code>")).replace(" ","")
                    }else{
                        throw IllegalArgumentException("code")
                    }

                    //duration
                    var durationString = lines[2]
                    if(durationString.contains("<duration>")&&durationString.contains("</duration>")){
                        durationString = durationString.substring(durationString.indexOf("<duration>")+10,durationString.indexOf("</duration>"))
                        duration = durationString.toLong()
                    }else{
                        throw IllegalArgumentException("duration")
                    }

                    //guesses
                    if(lines.size>4){
                        var guessLines=lines.subList(3,lines.size-1)
                        var index = 1
                        while(guessLines.isNotEmpty()){
                            if(guessLines.size >= 4 && guessLines[0] == "<guess$index>" && guessLines[3] == "</guess$index>"){

                                //guess
                                var userInput = guessLines[1]
                                var result = guessLines[2]
                                if(userInput.contains("<userInput>")&&userInput.contains("</userInput>")){
                                    userInput = userInput.substring(userInput.indexOf("<userInput>")+11,userInput.indexOf("</userInput>")).replace(", ","")
                                }else{
                                    throw IllegalArgumentException("userInput")
                                }
                                if(result.contains("<result>")&&result.contains("</result>")){
                                    result = result.substring(result.indexOf("<result>")+8,result.indexOf("</result>")).replace(", ","")
                                }else{
                                    throw IllegalArgumentException("result")
                                }

                                if(result=="SOLVED"||result=="NOT SOLVED"){
                                    ended = true
                                }

                                guesses.add("$userInput|$result")

                                //last guess
                                if(guessLines.size == 4){
                                    guessLines = ArrayList()
                                }else{
                                    guessLines = guessLines.subList(4,guessLines.size)
                                }

                            //incorrect number of lines
                            }else{
                                throw IllegalArgumentException("wrong number of lines")
                            }
                            index++
                        }
                    }
                }else{
                    throw IllegalArgumentException("start and/or end is incorrect")
                }

                this.code = code
                this.start = System.currentTimeMillis()-duration
                this.guesses.clear()
                this.guesses.addAll(guesses)
                this.ended = ended
            }else{
                throw IllegalArgumentException("not long enough")
            }

        }

        fun load(){
            try{
                val reader = BufferedReader(InputStreamReader(activity.openFileInput("savestate.sav")))

                var xml = ""

                var line = reader.readLine()
                while(line!=null){
                    xml+=line+"\n"
                    line = reader.readLine()
                }
                reader.close()

                xml = xml.substring(0,xml.length-1)

                fromXML(xml)
            }catch(exception : FileNotFoundException){
                Toast.makeText(activity,"There is no available savestate!",Toast.LENGTH_SHORT).show()
            }catch(exception : IllegalArgumentException){
                Toast.makeText(activity,"Invalid savestate",Toast.LENGTH_SHORT).show()
                println(exception.message)
            }
        }

        fun convertToXML() : String{
            var output = "<savestate>\n"

            //code
            output += "\t<code>" + code + "</code>\n"

            //duration
            output += "\t<duration>" + (System.currentTimeMillis()-start) + "</duration>\n"

            //guesses
            for((index,guess) in guesses.withIndex()){
                //guess
                var append = "\t<guess"+(index+1)+">\n"

                val arguments = guess.split("|")

                //userInput
                append += "\t\t<userInput>"
                var userInput = ""
                for(element in arguments[0].split("")){
                    if(!"".equals(element)){
                        userInput+=element+", "
                    }
                }
                userInput = userInput.substring(0,userInput.length-2)
                append+=userInput+"</userInput>\n"

                //result
                append += "\t\t<result>"
                var result = ""
                if("SOLVED".equals(arguments[1])||"NOT SOLVED".equals(arguments[1])){
                    result += arguments[1]
                }else{
                    for(element in arguments[1].split("")){
                        if(!"".equals(element)){
                            result+=element+", "
                        }
                    }
                    if(result.length>=3){
                        result = result.substring(0,result.length-2)
                    }
                }
                append+=result+"</result>\n"

                append+="\t</guess"+(index+1)+">\n"

                output+=append
            }

            return output + "</savestate>"
        }

        fun save(){
            val out = PrintWriter(OutputStreamWriter(activity.openFileOutput("savestate.sav",Context.MODE_PRIVATE)))
            out.write(convertToXML())
            out.flush()
            out.close()
        }

        fun loadSettings() : List<Setting>{
            val list = ArrayList<Setting>()

            val reader = BufferedReader(InputStreamReader(activity.assets.open("config.conf", Context.MODE_PRIVATE)))
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

        fun saveScores(){
            val out = PrintWriter(OutputStreamWriter(activity.openFileOutput("scores.sav",Context.MODE_PRIVATE)))
            for(score in scores){
                out.write(score+"\n")
                out.flush()
            }
            out.close()
        }

        fun loadScores(){
            try{
                val reader = BufferedReader(InputStreamReader(activity.openFileInput("scores.sav")))

                var line = reader.readLine()
                while(line!=null){
                    scores.add(line)
                    line = reader.readLine()
                }
                reader.close()
            }catch(exception : FileNotFoundException){

            }
        }

        fun hideKeyboard(){
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity);
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyboard(){
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity);
            }
            imm.showSoftInput(view, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity=this
        settings = loadSettings()
        loadScores()
        setUp()
    }

}
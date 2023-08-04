package com.example.sumar


import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
//import java.util.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.launch as launch1


class AdditionActivity : AppCompatActivity() {

    private lateinit var tvNumber1: TextView
    private lateinit var tvNumber2: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvCorrectSolution: TextView
    private lateinit var etSolution: EditText
    private lateinit var btnDone: Button
    private lateinit var btnMenu: Button
    private lateinit var btnBegin: Button
    private lateinit var tvQualification: TextView
    //private lateinit var tvOperation: TextView
    private lateinit var tvAddOperation: TextView
    private lateinit var switchAddOper: Switch

    private  var countDownTimer: CountDownTimer?=null
    private  var isTimerRunning = false

    private var mediaPlayer: MediaPlayer? = null
    private var beepNew:Int=0
    private var beepError:Int = 0



    private val start: Int = 1      //11
    private val end: Int = 9        //99
    private var num1: Int = 1
    private var num2: Int = 1
    private var randomNum: Int=1
    private val tag= "AddTimer"


    //private var resultMult: Int = 0
    private var resultCorrect: Int = 0
    private var result0:Int=0

    private var numSequence:Int=6       //total number of add
    private var delayInSeconds:Long=3
    private var timeInSeconds:Long=9
    private var introDelay:Boolean=false



    private var secondNum:Int=0         //second number of sequence
    private var delaySum:Int=8          // seconds
    private var visualDelay:Int=2    // seconds
    private var correctSum:Int=0     // total correct sum
    private var resultSum:Int=0
    private var currSequence:Int =0

    private var resultCorSum:String?=null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addition)

        initComponents()
        initListeners()
        etSolution.setText("")
        beepNew = resources.getIdentifier("beep_02", "raw", packageName)
        beepError = resources.getIdentifier("beep_10", "raw",packageName)


        // habilitar teclado numerico con - . ,
        etSolution.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)

    }

    private fun initListeners() {

        // XXXXXXXXXXXXXXX   BUTTON BEGIN  XX

        btnBegin.setOnClickListener {
            currSequence=0
            etSolution.setText("")
            tvCorrectSolution.setVisibility(View.INVISIBLE)
            tvAddOperation.setTextColor(ContextCompat.getColor(this, R.color.green_700))
            tvQualification.text="V"

            tvQualification.setTextColor(ContextCompat.getColor(this, R.color.background_app)) // it's invisible
            beginNumSequence()
        }

        //XXXXXXXXXXXXXXXXXXXXXXXXXX  BUTTON MENU XXXXXXXXXXXXXXXXXXXXXXXXX
        btnMenu.setOnClickListener {
            tvAddOperation.setTextColor(ContextCompat.getColor(this, R.color.background_tv))
            tvQualification.setTextColor(ContextCompat.getColor(this, R.color.background_app))

            tvQualification.text="V"

            onBackPressed()
        }




        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  BUTTON DONE  XXXXXXXXXXXXXXXXXXXXXXXXXX

        btnDone.setOnClickListener {

            tvCorrectSolution.setVisibility(View.VISIBLE)

            val result1:String = etSolution.getText().toString()

            //Log.d(tag,"result1=$result1 tvCorrectSolution=$tvCorrectSolution")
            Log.d(tag,"result1=$result1 resultCorSum=$resultCorSum")


            if (resultCorSum?.trim()==result1?.trim()) {

                //Toast.makeText(applicationContext, "¡Hecho!", Toast.LENGTH_LONG).show()
                //playNew()
                playBeep(beepNew)
                Toast.makeText(applicationContext,"¡Correcto!",Toast.LENGTH_LONG).show()


                tvQualification.setTextColor(ContextCompat.getColor(this, R.color.green_700))

                tvQualification.text="V"
            }
            else {
                //playError()
                playBeep(beepError)
                //Toast.makeText(applicationContext, "¡NO es Correcto!", Toast.LENGTH_LONG).show()
                tvQualification.text="X"
                tvQualification.setTextColor(ContextCompat.getColor(this, R.color.red_700))

            }


        }


    }


    private fun playBeep(beep:Int) {
        // Play the sound.mp3 file
        //val beepError = resources.getIdentifier("beep_10", "raw",packageName)
        mediaPlayer = MediaPlayer.create(this, beep)
        mediaPlayer?.start()

        // Stop playing the sound after 3 seconds (optional)
        Handler().postDelayed({
            mediaPlayer?.release()
            mediaPlayer = null
        }, 1000)
    }


    private fun continueNumSequence() {
        initSecondNumber()
        tvNumber2.setTextColor(ContextCompat.getColor(this, R.color.green_700))
        startTimer(timeInSeconds*1000)
        Log.d(tag,"isTimerRunning=${isTimerRunning.toString()}")
    }

    // XXXXXXXXXXXXXXXXX BEGIN SEQUENCE  XXXXXXXX
    private fun beginNumSequence() {
        initFirstNumber()
        initSecondNumber()
        tvNumber1.setTextColor(ContextCompat.getColor(this, R.color.green_700))
        tvNumber2.setTextColor(ContextCompat.getColor(this, R.color.green_700))
        startTimer(timeInSeconds*1000)
        Toast.makeText(this,"Ok!",Toast.LENGTH_SHORT).show()


    }


    private fun initFirstNumber() {
        num1 = getRandom()
        correctSum=num1

        tvNumber1.text = num1.toString()
    }


    private fun initSecondNumber() {
        num2 = getRandom()
        tvNumber2.text = num2.toString()
    }

    //  xxxxxxxxxxxxxxxxxxxxxxx  Random Number  xxxxxxxxxxxxxxxxxxxxxxx
    private fun getRandom(): Int {                  // RANDOM FROM -99 TO 99 (EXCLUDING -11-+11)
        var randomNumber: Int

        do {
            randomNumber = (Math.random() * 199).toInt() - 99 // Genera números aleatorios entre -99 y +99
            //}  while (randomNumber == -1 || randomNumber == 0 || randomNumber == 1)
        }  while (invalidRange(randomNumber))

        return randomNumber
    }


    private fun invalidRange(randNumber:Int):Boolean {    // EXCLUDING  from -11 to +11
        if (randNumber > -11 && randNumber < 12) {
            return true
        } else {
            return false
        }
    }

    //  xxxxxxxxxxxxxxxxxxxxxxx  END Random Number  xxxxxxxxxxxxxxxxxxxxxxx

    //  XXXXXXXXXXXXXXX   TIMER - TEMPORIZADOR  XXXXXXXXXXXXXXX
    private fun startTimer(durationInMillis: Long) {

        if (isTimerRunning) {
            // Timer is already running, no need to start a new one
            // Таймер уже запущен, новый запускать не нужно
            //etSolution.setText("Runnig")
            return
        }
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                //tvCountDown.setText(seconds.toString())
                //tvCountDelay.setText(seconds.toString())
                tvTimer.setVisibility(View.VISIBLE)

                if (seconds>=delayInSeconds) {
                    tvTimer.setText(seconds.toString())



                    //tvTimer.setText("Delay Finished")
                    //tvCountDown.isEnabled=false
                    introDelay=true
                }else{
                    tvTimer.setText(seconds.toString())
                    showDelay()
                    introDelay=false
                }

            }

            override fun onFinish() {
                //tvNumber1.setText("Timer Finished")
                //tvTimer.setText("Timer Finished")
                playBeep(beepNew)
                isTimerRunning = false
                Log.d(tag,"currSequence0 = $currSequence")
                num1=num1+num2
                num2=0
                //tvNumber1.text=num1.toString()
                //tvNumber1.text="sum"
                resultCorSum=num1.toString()
                currSequence++
                if (currSequence>0 && currSequence <= numSequence)  {
                    Log.d(tag,"currSequence1 = $currSequence")
                    continueNumSequence()

                }else if (currSequence > numSequence){
                    //isTimerRunning = false
                    tvCorrectSolution.text=num1.toString()

                    //tvCorrectSolution.setVisibility(View.VISIBLE)
                    Log.d(tag,"tvCorrectSolution=$tvCorrectSolution")
                    Log.d(tag,"isTimerRunning=${isTimerRunning.toString()}")
                    Log.d(tag,"num1=${num1.toString()}")
                    Log.d(tag,"resutCorSum=${resultCorSum.toString()}")


                }

            }
        }//.start()
        countDownTimer?.start()
        isTimerRunning = true
    }

    private fun showDelay() {
        tvNumber1.text="sum"
        tvNumber1.setTextColor(ContextCompat.getColor(this, R.color.text))
        tvNumber2.setTextColor(ContextCompat.getColor(this, R.color.text))

        //playBeep(beepNew)
    }

    private fun initComponents() {
        tvNumber1 = findViewById(R.id.tvNumber1)
        tvNumber2 = findViewById(R.id.tvNumber2)
        tvTimer = findViewById(R.id.tvTimer)
        tvCorrectSolution = findViewById(R.id.tvCorrectSolution)
        etSolution = findViewById(R.id.etSolution)
        btnBegin = findViewById(R.id.btnBegin)
        btnDone = findViewById(R.id.btnDone)
        btnMenu = findViewById(R.id.btnMenu)
        tvQualification=findViewById(R.id.tvQualification)
        //tvOperation=findViewById(R.id.tvOperation)
        tvAddOperation=findViewById(R.id.tvAddOperation)
        switchAddOper=findViewById(R.id.switchAddOper)
    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        //tvCountDown.isEnabled=true
    }
}
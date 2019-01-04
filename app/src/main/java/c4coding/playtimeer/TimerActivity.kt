package c4coding.playtimeer

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import c4coding.playtimeer.prefutils.PrefUtils
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*

class TimerActivity : AppCompatActivity() {

    private lateinit var mTimer: CountDownTimer
    private var mTimerState = TimerState.Stoped
    private var mTimeLengthSec = 0L
    private var mSecondRemainingLength = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)

        played.setOnClickListener {
            mTimerState = TimerState.Running
            onStartTimer()
            onUpdateUI()
        }
        pause.setOnClickListener {
            mTimerState = TimerState.Paused
            mTimer.cancel()
            onUpdateUI()
        }
        stoped.setOnClickListener {
            mTimer.cancel()
            onTimerFinish()
        }
    }

    override fun onResume() {
        super.onResume()

        onInitTimer()
    }

    override fun onPause() {
        super.onPause()

        if (mTimer == TimerState.Running) {
            mTimer.cancel()
            //Start bg timer show notification
        } else if (mTimer == TimerState.Stoped) {
            //Show Notification
        }
        PrefUtils.setPreviousTimerLength(this, mTimeLengthSec)
        PrefUtils.setTImerState(mTimerState, this)
        PrefUtils.setSecondRemainingLength(mSecondRemainingLength, this)
    }

    private fun onInitTimer() {
        mTimerState = PrefUtils.getTimerState(this)

        if (mTimerState == TimerState.Stoped)
            setNewTimerLength()
        else
            setPreviousTimer()

        mSecondRemainingLength = if (mTimerState == TimerState.Running || mTimerState == TimerState.Paused)
            PrefUtils.getSecondRemainingLength(this)
        else
            mTimeLengthSec

        //Change secondRemaining according to where the bg  timer stoped

        if (mTimerState == TimerState.Running)
            onStartTimer()
        onUpdateUI()
        onUpdateCountDownUI()
    }

    private fun onStartTimer() {
        mTimerState = TimerState.Running
        Log.e("mSecondRemainingLength","value of :$mSecondRemainingLength")
        mTimer = object : CountDownTimer(mSecondRemainingLength * 1000, 1000) {
            override fun onFinish() = onTimerFinish()

            override fun onTick(millisUntilFinished: Long) {
                mSecondRemainingLength = millisUntilFinished / 1000
                onUpdateUI()
                onUpdateCountDownUI()
            }
        }.start()
    }

    private fun onTimerFinish() {
        //FinishTimer
        mTimerState = TimerState.Stoped
        setNewTimerLength()
        progress_countdown.progress = 0

        PrefUtils.setSecondRemainingLength(mTimeLengthSec, this)
        mSecondRemainingLength = mTimeLengthSec

        onUpdateUI()
        onUpdateCountDownUI()
    }

    private fun setNewTimerLength() {
        val lenMinutes = PrefUtils.getTimerLength(this)
        mTimeLengthSec = (lenMinutes * 60L)

        progress_countdown.max = mTimeLengthSec.toInt()
        //set new Timer
    }

    private fun onUpdateCountDownUI() {
        val minuteUntilFinish = mSecondRemainingLength / 60
        val secoundTominuteUntilFinsh = mSecondRemainingLength - minuteUntilFinish * 60

        val len_Secondstr = secoundTominuteUntilFinsh.toString()
        timer_counter.text = "$minuteUntilFinish:${
        if (len_Secondstr.length == 2) len_Secondstr
        else "0 $len_Secondstr"}"
        progress_countdown.progress = (mTimeLengthSec - mSecondRemainingLength).toInt()
    }

    private fun setPreviousTimer() {
        mSecondRemainingLength = PrefUtils.getSecondRemainingLength(this)
        progress_countdown.max = mSecondRemainingLength.toInt()
        //setPrevious TImer
    }

    private fun onUpdateUI() {
        when (mTimerState) {
            TimerState.Running -> {
                played.isEnabled = false
                pause.isEnabled = true
                stoped.isEnabled = true
            }
            TimerState.Stoped -> {
                played.isEnabled = true
                pause.isEnabled = false
                stoped.isEnabled = false
            }
            TimerState.Paused -> {
                played.isEnabled = true
                pause.isEnabled = false
                stoped.isEnabled = true
            }
        }
    }
}

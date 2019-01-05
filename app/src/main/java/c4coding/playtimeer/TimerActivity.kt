package c4coding.playtimeer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity;
import c4coding.playtimeer.prefutils.PrefUtils
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {
    companion object {
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun setAlarm(context: Context, newSec: Long, remainingSec: Long): Long {
            val milleSec = (newSec + remainingSec) * 1000
            val intent = Intent(context, AlarmExpireReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 12, intent, 0)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.setExact(AlarmManager.RTC_WAKEUP, milleSec, pendingIntent)
            PrefUtils.setAlarmTimeSecound(newSec,context)
            return newSec
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, AlarmExpireReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 12, intent, 0)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(pendingIntent)
            PrefUtils.setAlarmTimeSecound(0,context)
        }
        private val mNowSec: Long
            get() = Calendar.getInstance().timeInMillis /1000
    }


    private lateinit var mTimer: CountDownTimer
    private var mTimerState = TimerState.Stoped
    private var mTimeLengthSec = 0L
    private var mSecondRemaining = 0L

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
        removeAlarm(this)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        super.onPause()

        if (mTimer == TimerState.Running) {
            mTimer.cancel()
            val nowSec = setAlarm(this, mNowSec,mSecondRemaining)
            //Start bg timer show notification
        } else if (mTimer == TimerState.Stoped) {
            //Show Notification
        }
        PrefUtils.setPreviousTimerLength(this, mTimeLengthSec)
        PrefUtils.setTImerState(mTimerState, this)
        PrefUtils.setSecondRemainingLength(mSecondRemaining, this)
    }

    private fun onInitTimer() {
        mTimerState = PrefUtils.getTimerState(this)

        if (mTimerState == TimerState.Stoped)
            setNewTimerLength()
        else
            setPreviousTimer()

        mSecondRemaining = if (mTimerState == TimerState.Running || mTimerState == TimerState.Paused)
            PrefUtils.getSecondRemainingLength(this)
        else
            mTimeLengthSec

        //Change secondRemaining according to where the bg  timer stoped

        val alarmSetTime=PrefUtils.getAlarmTimeSecoundla(this)
        if (alarmSetTime>0) {
            mSecondRemaining -= mNowSec - alarmSetTime
        }
        if (mSecondRemaining<=0)
            onTimerFinish()
        else if (mTimerState == TimerState.Running)
            onStartTimer()

        onUpdateUI()
        onUpdateCountDownUI()
    }

    private fun onStartTimer() {
        mTimerState = TimerState.Running
        mTimer = object : CountDownTimer(mSecondRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinish()

            override fun onTick(millisUntilFinished: Long) {
                mSecondRemaining = millisUntilFinished / 1000
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
        mSecondRemaining = mTimeLengthSec

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
        val minuteUntilFinish = mSecondRemaining / 60
        val secoundTominuteUntilFinsh = mSecondRemaining - minuteUntilFinish * 60

        val len_Secondstr = secoundTominuteUntilFinsh.toString()
        timer_counter.text = "$minuteUntilFinish:${
        if (len_Secondstr.length == 2) len_Secondstr
        else "0 $len_Secondstr"}"
        progress_countdown.progress = (mTimeLengthSec - mSecondRemaining).toInt()
    }

    private fun setPreviousTimer() {
        mTimeLengthSec = PrefUtils.getPreviousTimerLength(this)
        progress_countdown.max = mTimeLengthSec.toInt()
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

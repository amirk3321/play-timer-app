package c4coding.playtimeer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import c4coding.playtimeer.prefutils.PrefUtils

class AlarmExpireReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
       //Notification and sound music set

        PrefUtils.setTImerState(TimerState.Stoped,context)
        PrefUtils.setAlarmTimeSecound(0,context)
    }
}

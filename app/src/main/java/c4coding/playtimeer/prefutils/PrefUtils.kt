package c4coding.playtimeer.prefutils

import android.content.Context
import android.preference.PreferenceManager
import c4coding.playtimeer.TimerState

class PrefUtils {
    companion object {
        fun getTimerLength(context: Context): Int {
            return 1
        }

        private val PREVIOUS_TIME_LENGTH_ID = "com.c4coding_PreviousTimerLength"
        fun getPreviousTimerLength(context: Context): Long {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(PREVIOUS_TIME_LENGTH_ID, 0)
        }

        fun setPreviousTimerLength(context: Context, timeLengthSecund: Long) {
            val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
            preference.putLong(PREVIOUS_TIME_LENGTH_ID, timeLengthSecund)
            preference.apply()
        }

        private val TIMER_STATE_ID = "com.c4coding_TimerState"
        fun getTimerState(context: Context): TimerState {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preference.getInt(TIMER_STATE_ID, 0)
            return TimerState.values()[ordinal]
        }

        fun setTImerState(timerState: TimerState, context: Context) {
            val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = timerState.ordinal
            preference.putInt(TIMER_STATE_ID, ordinal)
            preference.apply()
        }

        private val SEC0UND_REMANING_ID = "com.c4coding_SecondsRemaining"
        fun getSecondRemainingLength(context: Context): Long {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(SEC0UND_REMANING_ID, 0)
        }

        fun setSecondRemainingLength(second: Long, context: Context) {
            val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
            preference.putLong(SEC0UND_REMANING_ID, second)
            preference.apply()
        }

        private val ALARM_TIME_SECOUND_ID = "com.c4ding.alarm_sec"
        fun setAlarmTimeSecound(newSec: Long, context: Context) {
            val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
            preference.putLong(ALARM_TIME_SECOUND_ID, newSec)
            preference.apply()
        }

        fun getAlarmTimeSecoundla(context: Context): Long {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(ALARM_TIME_SECOUND_ID, 0)
        }
    }
}
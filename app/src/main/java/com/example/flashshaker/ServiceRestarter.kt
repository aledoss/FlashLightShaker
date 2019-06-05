package com.example.flashshaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.flashshaker.SharedPrefConstants.Companion.IS_SERVICE_ACTIVE

class ServiceRestarter : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val pref = context.getSharedPreferences(SharedPrefConstants.DEFAULT_SHARED_PREF_NAME, Context.MODE_PRIVATE)
        if (pref.getBoolean(IS_SERVICE_ACTIVE, true)) {
            startShakeDetectorService(context)
        }
    }

    private fun startShakeDetectorService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, ShakeListenerService::class.java))
        } else {
            context.startService(Intent(context, ShakeListenerService::class.java))
        }
    }

}
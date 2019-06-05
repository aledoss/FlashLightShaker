package com.example.flashshaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.flashshaker.SharedPrefConstants.Companion.DEFAULT_SHARED_PREF_NAME
import com.example.flashshaker.SharedPrefConstants.Companion.IS_SERVICE_ACTIVE


class MainActivity : AppCompatActivity() {

    private lateinit var switchServiceState: SwitchCompat
    private var isServiceActive: Boolean = true
    private lateinit var pref: SharedPreferences
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeVariables()
        preloadUserConfig()
        setupUI()
    }

    private fun initializeVariables() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        pref = getSharedPreferences(DEFAULT_SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun preloadUserConfig() {
        isServiceActive = pref.getBoolean(IS_SERVICE_ACTIVE, true)
    }

    private fun setupUI() {
        initializeSwitchServiceState()
    }

    private fun initializeSwitchServiceState() {
        switchServiceState = findViewById(R.id.switchServiceState)
        switchServiceState.setOnCheckedChangeListener { _, isChecked ->
            updateServiceStateConfig(isChecked)
            if (!isChecked) stopShakeListenerService() else startShakeDetector()
        }
        switchServiceState.isChecked = isServiceActive
    }

    private fun updateServiceStateConfig(checked: Boolean) {
        pref.edit()
                .putBoolean(IS_SERVICE_ACTIVE, checked)
                .apply()
    }

    private fun stopShakeListenerService() {
        localBroadcastManager.sendBroadcast(Intent("STOP_SERVICE"))
    }

    private fun startShakeDetector() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ShakeListenerService::class.java))
        } else {
            startService(Intent(this, ShakeListenerService::class.java))
        }
        localBroadcastManager.sendBroadcast(Intent("START_SERVICE"))
    }

}

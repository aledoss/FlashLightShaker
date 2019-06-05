package com.example.flashshaker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startShakeDetector()
    }


    private fun startShakeDetector() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ShakeListenerService::class.java))
        } else {
            startService(Intent(this, ShakeListenerService::class.java))
        }
    }

}

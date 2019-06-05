package com.example.flashshaker

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class ShakeListenerService : Service(), SensorEventListener {

    private lateinit var flashLightManager: FlashLightManager
    private lateinit var mCameraManager: CameraManager
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private var mAccel: Float = 0F // acceleration apart from gravity
    private var mAccelCurrent: Float = 0F // current acceleration including gravity
    private var mAccelLast: Float = 0F// last acceleration including gravity

    private lateinit var localBroadcastManager: LocalBroadcastManager
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //TODO validar que sea el intent correcto
            if (intent?.action == "STOP_SERVICE") {
                mSensorManager.unregisterListener(this@ShakeListenerService)
            } else {
                mSensorManager.registerListener(
                        this@ShakeListenerService, mAccelerometer, SensorManager.SENSOR_DELAY_UI, Handler()
                )
            }
        }

    }


    override fun onCreate() {
        super.onCreate()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(receiver, IntentFilter("START_SERVICE"))
        localBroadcastManager.registerReceiver(receiver, IntentFilter("STOP_SERVICE"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initializeVariables()
        initializeShakeListener()

        return START_STICKY_COMPATIBILITY
    }

    private fun initializeVariables() {
        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        flashLightManager = FlashLightManager(mCameraManager)
    }

    private fun initializeShakeListener() {
        mSensorManager.registerListener(
                this, mAccelerometer, SensorManager.SENSOR_DELAY_UI, Handler()
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        mAccelLast = mAccelCurrent
        mAccelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = mAccelCurrent - mAccelLast
        mAccel = mAccel * 0.9f + delta // perform low-cut filter

        if (mAccel > 11) {
            onShake()
            Log.i("ShakeListenerService", "Velocidad superada")
        }
    }

    private fun onShake() {
        mSensorManager.unregisterListener(this)
        flashLightManager.toggleFlashLight()
        Thread.sleep(300)
        mSensorManager.registerListener(
                this, mAccelerometer, SensorManager.SENSOR_DELAY_UI, Handler()
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        restartService()

        super.onDestroy()
    }

    private fun restartService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, ServiceRestarter::class.java)
        sendBroadcast(broadcastIntent)
    }

}
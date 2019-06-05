package com.example.flashshaker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.IBinder
import android.util.Log


class ShakeListenerService : Service(), SensorEventListener {

    private lateinit var mCameraManager: CameraManager
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private var mAccel: Float = 0F // acceleration apart from gravity
    private var mAccelCurrent: Float = 0F // current acceleration including gravity
    private var mAccelLast: Float = 0F// last acceleration including gravity

    private lateinit var flashLightManager: FlashLightManager

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
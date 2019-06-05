package com.example.flashshaker

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.seismic.ShakeDetector

class MainActivity : AppCompatActivity()/*, MyShakeDetector.OnShakeListener*/ {
    private lateinit var sensorManager: SensorManager

    private lateinit var shakeDetector: ShakeDetector
    private lateinit var cameraManager: CameraManager
    private lateinit var flashLightManager: FlashLightManager
    lateinit var myShakeDetector: MyShakeDetector

    private lateinit var mAccelerometer: Sensor
    private lateinit var mShakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO https://stackoverflow.com/questions/30525784/android-keep-service-running-when-app-is-killed

        initializeVariables()
        /*initializeShakeDetector()*/
        startShakeDetector()
//        sensorManager.registerListener(myShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    private fun initializeVariables() {
        /*cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        flashLightManager = FlashLightManager(cameraManager)

        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        myShakeDetector = MyShakeDetector()
        myShakeDetector.setOnShakeListener(this)

        startShakeDetector()*/
    }

    override fun onResume() {
        super.onResume()
//        sensorManager.registerListener(myShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
//        sensorManager.unregisterListener(myShakeDetector)
        super.onPause()
    }

    /*override fun onShake(count: Int) {
        sensorManager.unregisterListener(myShakeDetector)
        flashLightManager.toggleFlashLight()
        Thread.sleep(300)
        sensorManager.registerListener(myShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }*/

    /*private fun initializeShakeDetector() {
//        shakeDetector = MyShakeDetector { flashLightManager.toggleFlashLight() }
//        shakeDetector.setSensitivity(MyShakeDetector.SENSITIVITY_HARD)
//        shakeDetector.start(sensorManager)
    }*/

    private fun startShakeDetector() {
        /*val intent = Intent(this, ShakeListenerService::class.java)
        startService(intent)*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ShakeListenerService::class.java))
        } else {
            startService(Intent(this, ShakeListenerService::class.java))
        }
    }

}

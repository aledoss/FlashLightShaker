package com.example.flashshaker

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log


class MyShakeDetector : SensorEventListener {

    private var mListener: OnShakeListener? = null
    var mShakeTimestamp: Long = 0
    var mShakeCount: Int = 0

    companion object {
        val SHAKE_THRESHOLD_GRAVITY = 2.7
        val SHAKE_SLOP_TIME_MS = 500
        val SHAKE_COUNT_RESET_TIME_MS = 3000
    }

    fun setOnShakeListener(listener: OnShakeListener) {
        mListener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        mListener.let {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = (x / SensorManager.GRAVITY_EARTH).toDouble()
            val gY = (y / SensorManager.GRAVITY_EARTH).toDouble()
            val gZ = (z / SensorManager.GRAVITY_EARTH).toDouble()

            // gForce will be close to 1 when there is no movement.
            val gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ)
//            Log.i("onSensorChanged", "Fuerza: $gForce");

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                Log.i("onSensorChanged", "Fuerza superada");
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return; }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now
                mShakeCount++;
                Log.i("onSensorChanged", "Conteo: $mShakeCount");

                //flashLightManager.toggleFlashLight()
                mListener?.onShake(mShakeCount)

                if (mShakeCount == 2) {
                    Log.i("onSensorChanged", "Reseteo conteo");
                    mShakeCount = 0
                }
            }
        }
    }
}
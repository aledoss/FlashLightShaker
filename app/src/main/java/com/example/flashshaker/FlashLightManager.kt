package com.example.flashshaker

import android.annotation.TargetApi
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class FlashLightManager(private val cameraManager: CameraManager) {

    private var isFlashLightOn = false
    private val cameraId = cameraManager.cameraIdList[0]

    fun toggleFlashLight() {
        if (isFlashLightOn) {
            turnOffFlashLight()
        } else {
            turnOnFlashLight()
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun turnOnFlashLight() {
        try {
            cameraManager.setTorchMode(cameraId, true)
            isFlashLightOn = true
            Log.i("Linterna", "Apagada");
        } catch (e: CameraAccessException) {
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun turnOffFlashLight() {
        try {
            cameraManager.setTorchMode(cameraId, false)
            isFlashLightOn = false
            Log.i("Linterna", "Encendida");
        } catch (e: CameraAccessException) {
        }
    }
}
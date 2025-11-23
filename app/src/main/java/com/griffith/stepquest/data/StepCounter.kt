package com.griffith.stepquest.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// step counter sensor class (checks sensor availabilty, start counting stop counting and detect change)
class StepCounter(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    var currentSteps: Int = 0
        private set

    // check if the mobile has step counter sensor
    fun hasStepCounterSensor(): Boolean {
        if (sensorManager == null) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        return sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
    }
    
    // detect change and store the change
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            currentSteps = event.values[0].toInt()
        }
    }

    // detect accuracy change
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


}

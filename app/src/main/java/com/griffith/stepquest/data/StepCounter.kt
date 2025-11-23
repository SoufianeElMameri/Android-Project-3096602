package com.griffith.stepquest.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

// step counter sensor class (checks sensor availabilty, start counting stop counting and detect change)
class StepCounter(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    var currentSteps: Int = 0
        private set

    private val prefs = context.getSharedPreferences("steps_prefs", Context.MODE_PRIVATE)

    private fun getToday(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    // check if the mobile has step counter sensor
    fun hasStepCounterSensor(): Boolean {
        if (sensorManager == null) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        return sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
    }

    // to start reading steps
    fun start() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // to stop listening to steps
    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    // detect change and store the change
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor == null) {
            return
        }

        if (event.sensor!!.type != Sensor.TYPE_STEP_COUNTER) {
            return
        }


        val rawSteps = event.values[0].toInt()
        val today = getToday()
        val savedDay = prefs.getString("saved_day", null)

        // first time app runs → save today's date and baseline
        if (savedDay == null) {
            prefs.edit { putString("saved_day", today) }
            prefs.edit { putInt("baseline_steps", rawSteps) }
        }

        // if day changed → reset baseline
        if (savedDay != today) {
            prefs.edit { putString("saved_day", today) }
            prefs.edit { putInt("baseline_steps", rawSteps) }
        }

        val baseline = prefs.getInt("baseline_steps", rawSteps)

        // calculate daily
        val daily = rawSteps - baseline
        if (daily < 0) {
            currentSteps = 0
        } else {
            currentSteps = daily
        }

        // save for persistence
        prefs.edit { putInt("daily_steps", currentSteps) }
    }

    // detect accuracy change
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


}

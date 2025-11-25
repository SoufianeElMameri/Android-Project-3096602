package com.griffith.stepquest.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

// step counter sensor class (checks sensor availabilty, start counting stop counting and detect change)
class StepCounter(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var baselineSteps = 0
    private var savedDay = ""
    private var initialized = false
    var currentSteps: Int = 0
        private set

    // local storage variable
    private val prefs = context.getSharedPreferences("steps_prefs", Context.MODE_PRIVATE)

    // function to get today's date
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

    // initialize the data at the start of the app
    private fun initializeData(rawSteps: Int) {

        Log.d("StepCounter", "initializing Data")
        // check if it has been already initialized (app running already)
        if (initialized) {
            Log.d("StepCounter", "Already initialized Data")
            return
        }

        savedDay = prefs.getString("saved_day", null) ?: ""
        Log.d("StepCounter", "Checking saved day = $savedDay")
        val today = getToday()
        Log.d("StepCounter", "Getting today = $today")

        // if no date has been saved before we create new saves (date + baseline )
        if (savedDay == "") {
            savedDay = today

            prefs.edit {
                putString("saved_day", today)
            }

            baselineSteps = rawSteps
            prefs.edit {
                putInt("baseline_steps", baselineSteps)
            }
        // if current date is not todayu's date( new date save it and save new baseline)
        } else if (savedDay != today) {
            savedDay = today

            prefs.edit {
                putString("saved_day", today)
            }

            // reset baseline for today
            baselineSteps = rawSteps
            prefs.edit {
                putInt("baseline_steps", baselineSteps)
                Log.d("StepCounter", "Updated baseline steps = $baselineSteps")
            }
        } else {
            baselineSteps = prefs.getInt("baseline_steps", 0)
            Log.d("StepCounter", "Getting baseline steps = $baselineSteps")
        }

        initialized = true
    }

    // detect change and store the change
    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return
        if (event.sensor == null) return
        if (event.sensor!!.type != Sensor.TYPE_STEP_COUNTER) return



        val rawSteps = event.values[0].toInt()
        initializeData(rawSteps)
        // first step after opening the app we initialize baseline
        if (!prefs.contains("baseline_steps")) {
            baselineSteps = rawSteps

            prefs.edit {
                putInt("baseline_steps", baselineSteps)
            }
        }

        val daily = rawSteps - baselineSteps

        if (daily < 0) {
            currentSteps = 0
        } else {
            currentSteps = daily
        }
        Log.d("StepCounter", "raw=$rawSteps baseline=$baselineSteps daily=$currentSteps")
    }

    // detect accuracy change
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


}

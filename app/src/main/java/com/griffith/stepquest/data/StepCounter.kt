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
import com.griffith.stepquest.ui.viewmodels.StepsViewModel
import com.griffith.stepquest.ui.viewmodels.UserViewModel


// step counter sensor class (checks sensor availabilty, start counting stop counting and detect change)
class StepCounter(private val context: Context, private val stepViewModel: StepsViewModel, private val userViewModel: UserViewModel) : SensorEventListener {

    private val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)


    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var baselineSteps = 0
    private var savedDay = ""
    private var initialized = false
    var currentSteps: Int = 0
        private set

    // function to get today's date
    private fun getToday(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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
        savedDay = prefs.getString("savedDay", "") ?: ""
        baselineSteps = prefs.getInt("baselineSteps", 0)
        initialized = prefs.getBoolean("initialized", false)

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
        val today = getToday()

        // return if already initalized for today
        if (initialized && savedDay == today) {
            return
        }

        if (savedDay == "") {
            savedDay = today
            baselineSteps = rawSteps

            prefs.edit().putString("savedDay", savedDay).apply()
            prefs.edit().putInt("baselineSteps", baselineSteps).apply()
            prefs.edit().putBoolean("initialized", true).apply()

            initialized = true
            return
        } else if (savedDay != today) {

            stepViewModel.saveDailySteps(savedDay, currentSteps)
            val dailyGoal = stepViewModel.dailyGoal
            userViewModel.updateStreak(currentSteps, dailyGoal)

            savedDay = today
            baselineSteps = rawSteps

            prefs.edit().putString("savedDay", savedDay).apply()
            prefs.edit().putInt("baselineSteps", baselineSteps).apply()
            prefs.edit().putBoolean("initialized", true).apply()

            initialized = true
            return
        }

        initialized = true
    }

    // function to update steps on sensore change
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor == null) return
        if (event.sensor!!.type != Sensor.TYPE_STEP_COUNTER) return

        val rawSteps = event.values[0].toInt()

        initializeData(rawSteps)

        val daily = rawSteps - baselineSteps

        if (daily < 0) {
            currentSteps = 0
        } else {
            currentSteps = daily
        }

        stepViewModel.updateSteps(currentSteps)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


}

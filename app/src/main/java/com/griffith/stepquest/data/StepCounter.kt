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
import com.google.firebase.auth.FirebaseAuth
import com.griffith.stepquest.ui.viewmodels.RankViewModel
import com.griffith.stepquest.ui.viewmodels.StepsViewModel
import com.griffith.stepquest.ui.viewmodels.UserViewModel
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.sqrt


// step counter sensor class (checks sensor availabilty, start counting stop counting and detect change)
class StepCounter(private val context: Context, private val stepViewModel: StepsViewModel, private val userViewModel: UserViewModel, private val rankViewModel: RankViewModel) : SensorEventListener {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
    private val prefs = context.getSharedPreferences("step_prefs_$userId", Context.MODE_PRIVATE)



    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var linearAccelSensor: Sensor? = null
    private var accelSensor: Sensor? = null
    private var gyroSensor: Sensor? = null



    private var savedDay = ""
    private var initialized = false
//    private var baselineSteps = 0
    private var offset = 0
    private var lastSensorValue = 0
    private var dailySteps = 0
    private var lastLinearMagnitude = 0f
    private var hasLinearAccel = false
    private var lastAccelMagnitude = 0f
    private var hasAccelerometer = false
    private var hasGyro = false



    private var lastAcceptedStepTime  = 0L

    private val gyroWindow = FloatArray(30)
    private var gyroIndex = 0
    private var gyroCount = 0
    private var lastGyroTimestamp = 0L

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
//        baselineSteps = prefs.getInt("baselineSteps", 0)
        initialized     = prefs.getBoolean("initialized", false)
        lastSensorValue = prefs.getInt("lastSensorValue", 0)
        offset          = prefs.getInt("offset", 0)
        dailySteps      = prefs.getInt("dailySteps", 0)

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stepSensor          = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        linearAccelSensor   = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroSensor          = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        hasLinearAccel = linearAccelSensor != null
        hasAccelerometer = accelSensor != null
        hasGyro = gyroSensor != null

        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        linearAccelSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

//        updateNewDaySteps()
    }

    // to stop listening to steps
    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    // initialize the data at the start of the app
    private fun initializeData(rawSteps: Int) {
        val today = getToday()
        stepViewModel.loadWeeklyHistory(saveToDb = true, rankViewModel, userViewModel)
        // return if already initalized for today
        if (initialized && savedDay == today) {
            return
        }
//        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//
//        val date = sdf.parse(savedDay)
//        val cal = Calendar.getInstance()
//        if (date != null) {
//            cal.time = date
//        }
//        cal.add(Calendar.DAY_OF_YEAR, -1)
//
//        savedDay = sdf.format(cal.time)

        if (savedDay == "") {
            savedDay = today
            offset = rawSteps
            dailySteps = 0
//            baselineSteps = rawSteps

            prefs.edit {
                putString("savedDay", savedDay)
                putInt("offset", offset)
                putInt("dailySteps", dailySteps)
                putBoolean("initialized", true)
            }
//            prefs.edit { putString("savedDay", savedDay) }
//            prefs.edit { putInt("baselineSteps", baselineSteps) }
//            prefs.edit { putBoolean("initialized", true) }

            initialized = true
            return
        } else if (savedDay != today) {

            stepViewModel.saveDailySteps(savedDay, currentSteps)
            stepViewModel.loadWeeklyHistory(saveToDb = true, rankViewModel, userViewModel)
            val dailyGoal = stepViewModel.dailyGoal
            userViewModel.updateStreak(currentSteps, dailyGoal)

            // Reset for new day
            savedDay = today
            offset = rawSteps
            dailySteps = 0

            prefs.edit {
                putString("savedDay", savedDay)
                putInt("offset", offset)
                putInt("dailySteps", dailySteps)
                putBoolean("initialized", true)
            }
            initialized = true
            return
        }

        initialized = true
    }


//    fun updateNewDaySteps(){
//        val today = getToday()
//        if (savedDay != today) {
//            currentSteps = 0
//            lastAcceptedStepTime  = 0L
//        }
//
//    }
    // function to update steps on sensore change
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor == null) return

        if (hasLinearAccel &&  event.sensor!!.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastLinearMagnitude = sqrt(x * x + y * y + z * z)
        }

        if (hasGyro && event.sensor!!.type == Sensor.TYPE_GYROSCOPE) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val rotMag = sqrt(x * x + y * y + z * z)

            gyroWindow[gyroIndex] = rotMag
            gyroIndex = (gyroIndex + 1) % gyroWindow.size

            if (gyroCount < gyroWindow.size) {
                gyroCount++
            }

            lastGyroTimestamp = System.currentTimeMillis()
        }
        if (hasAccelerometer && event.sensor!!.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            lastAccelMagnitude = sqrt(x * x + y * y + z * z)
        }

        if (event.sensor!!.type == Sensor.TYPE_STEP_COUNTER) {
            val rawSteps = event.values[0].toInt()

            if (lastSensorValue == 0) {
                lastSensorValue = rawSteps
                offset = rawSteps - dailySteps
                prefs.edit {
                    putInt("lastSensorValue", lastSensorValue)
                    putInt("offset", offset)
                }
            }

            if (rawSteps < lastSensorValue) {
                offset = rawSteps - dailySteps
                prefs.edit { putInt("offset", offset) }
            }

            Log.d("STEPSSENSOR_DEBUG"," offset =$offset")

            val computedSteps   = rawSteps - offset
            val validatedSteps  = validateStep(computedSteps)

            if (validatedSteps != computedSteps) {
                Log.d("STEPSSENSOR_DEBUG"," validatedSteps =$validatedSteps  computedSteps = $computedSteps")
                offset = rawSteps - validatedSteps
                Log.d("STEPSSENSOR_DEBUG"," offset =$offset")
            }

            dailySteps      = validatedSteps
            currentSteps    = validatedSteps


//            dailySteps = rawSteps - offset
//            dailySteps = validateStep(dailySteps)
//            currentSteps = dailySteps


            lastSensorValue = rawSteps
            prefs.edit {
                putInt("dailySteps", dailySteps)
                putInt("lastSensorValue", lastSensorValue)
            }

            initializeData(rawSteps)
            stepViewModel.updateSteps(currentSteps)
        }



    }


    fun forceReadSensor(done: ((Int) -> Unit)? = null) {
        if (stepSensor == null) {
            done?.invoke(currentSteps)
            return
        }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent?) {

                if (event == null) return

                if (hasLinearAccel && event.sensor!!.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val mag = sqrt(x * x + y * y + z * z)
                    lastLinearMagnitude = mag
                }

                if (hasGyro && event.sensor!!.type == Sensor.TYPE_GYROSCOPE) {

                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val rotMag = sqrt(x * x + y * y + z * z)

                    gyroWindow[gyroIndex] = rotMag
                    gyroIndex = (gyroIndex + 1) % gyroWindow.size

                    if (gyroCount < gyroWindow.size) {
                        gyroCount++
                    }

                    lastGyroTimestamp = System.currentTimeMillis()
                }
                if (hasAccelerometer && event.sensor!!.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    lastAccelMagnitude = sqrt(x * x + y * y + z * z)
                }
                val rawSteps = event.values[0].toInt()

                if (lastSensorValue == 0) {
                    lastSensorValue = rawSteps
                    offset = rawSteps - dailySteps
                }

                if (rawSteps < lastSensorValue) {
                    offset = rawSteps - dailySteps
                }

                val computedSteps = rawSteps - offset
                val validatedSteps = validateStep(computedSteps, true)

                if (validatedSteps != computedSteps) {
                    offset = rawSteps - validatedSteps
                }

                dailySteps      = validatedSteps
                currentSteps    = validatedSteps

                lastSensorValue = rawSteps

                prefs.edit {
                    putInt("dailySteps", dailySteps)
                    putInt("lastSensorValue", lastSensorValue)
                    putInt("offset", offset)
                }
                initializeData(rawSteps)
                stepViewModel.updateSteps(currentSteps)

                sensorManager.unregisterListener(this)

                done?.invoke(currentSteps)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            listener,
            stepSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    private fun validateStep(steps: Int, skip: Boolean = false): Int {

        val now = System.currentTimeMillis()
        if(steps < currentSteps){
            return currentSteps
        }
        if (skip) {
            lastAcceptedStepTime = now
            return steps
        }

        if (lastAcceptedStepTime != 0L) {
            Log.d("STEPSSENSOR_DEBUG"," lastAcceptedStepTime =$lastAcceptedStepTime ")
            if (now - lastAcceptedStepTime < 500) {
                return currentSteps
            }
        }

        if (hasAccelerometer) {
            if (lastAccelMagnitude > 20f) {
                Log.d("STEPSSENSOR_DEBUG"," lastAccelMagnitude =$lastAccelMagnitude > 20f")

                return currentSteps
            }
        }

        if (hasLinearAccel) {
            if (lastLinearMagnitude > 3f) {
                Log.d("STEPSSENSOR_DEBUG"," lastLinearMagnitude =$lastLinearMagnitude > 3f")

                return currentSteps
            }
        }
        if (hasGyro) {
            if (gyroCount >= gyroWindow.size) {

                var energy = 0f
                var peaks = 0

                for (i in gyroWindow.indices) {
                    energy += gyroWindow[i]
                    if (gyroWindow[i] > 4.5f) {
                        peaks++
                    }
                }

                val avgEnergy = energy / gyroWindow.size

                if (
                    avgEnergy > 3.2f &&
                    peaks > gyroWindow.size / 4 &&
                    now - lastGyroTimestamp < 300
                ) {
                    Log.d("STEPSSENSOR_DEBUG", " avgEnergy = $avgEnergy peaks = $peaks")
                    return currentSteps
                }
            }
        }

        lastAcceptedStepTime = now
        return steps
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


}

package com.example.mdi2_111_fitnesstracker

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlin.math.sqrt
import com.github.mikephil.charting.components.XAxis

@SuppressLint("setTextI18n")
class MainActivity : AppCompatActivity(), SensorEventListener {
    // UI Views
    private lateinit var tvMotion: TextView
    private lateinit var tvGyro: TextView
    private lateinit var tvDirection: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var btnCalibrate: Button
    private lateinit var btnRefreshChart: Button
    private lateinit var barChart: BarChart

    // Sensors
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null

    // Sensor fusion - needed for compass
    private var accelValues = FloatArray(3)
    private var magnetValues = FloatArray(3)

    // Calibration baseline
    private var baselineX = 0f
    private var baselineY = 0f
    private var baselineZ = 9.8f

    private var isCalibrating = false
    private val calibrationSamples = mutableListOf<FloatArray>()

    // Sample data for chart
    private val hourlySteps = floatArrayOf(1000f, 1200f, 0f, 800f)

    companion object {
        private const val STATIONARY_THRESHOLD = 2.0f
        private const val WALKING_THRESHOLD = 12.0f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvMotion = findViewById(R.id.tvMotion)
        tvGyro = findViewById(R.id.tvGyro)
        tvDirection = findViewById(R.id.tvDirection)
        tvAccuracy = findViewById(R.id.tvAccuracy)
        btnCalibrate = findViewById(R.id.btnCalibrate)
        btnRefreshChart = findViewById(R.id.btnRefreshChart)
        barChart = findViewById(R.id.barChart)

        initSensors()
        setupChart()
        btnCalibrate.setOnClickListener { startCalibration() }
        btnRefreshChart.setOnClickListener { setupChart() }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST) }
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST) }
        magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // FINAL: add onStop() override + call unregisterListener(this)

    private fun initSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    // Assignment 1: add the 3 sensor cases
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            // TODO Case 1: TYPE_ACCELEROMETER: store accelValues, call handleAccelerometer()
            Sensor.TYPE_GYROSCOPE -> {
                accelValues = event.values.clone()
                handleAccelerometer(accelValues)
            }
            // TODO Case 2: TYPE_GYROSCOPE: store gyroValues, call handleGyroscope()
            // TODO Case 3: TYPE_MAGNETIC_FIELD: store magnetValues, call updateCompass()
        }
    }

    // pre-built: handleAccelerometer()
    private fun handleAccelerometer(values: FloatArray) {
        val x = values[0] - baselineX
        val y = values[1] - baselineY
        val z = values[2] - baselineZ
        val magnitude = sqrt(x * x + y * y + z * z)
        tvMotion.text = "Motion: ${classifyMotion(magnitude)}"

        if (isCalibrating) {
            calibrationSamples.add(floatArrayOf(values[0], values[1], values[2]))
            if (calibrationSamples.size > 20) finishCalibration()
        }
    }

    // Assignment: implement classifyMotion
    // Return "Stationary", "Walking", or "Jogging" based on magnitude (m/s^2)
    private fun classifyMotion(magnitude: Float): String {
        return when {
            magnitude < STATIONARY_THRESHOLD -> "Stationary"
            magnitude < WALKING_THRESHOLD -> "Walking"
            else -> "Jogging"
        }
        // TODO: replace with when block using the threshold above
    }

    // Assignment: Implement handleGyroscope()
    // values[0] = pitch, values[1] = roll, values[2] = yaw
    // tvGyro.text = "Rotation (Pitch, Roll, Yaw): $.2f"
    private fun handleGyroscope(values: FloatArray) {

    }

    // Assignment
    // Hint: SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelValues, magnetValues)
    private fun updateCompass() {

    }

    // Assignment 2
    // Hint: val entries = hourlySteps.mapIndexed { i, v -> barEntry(i.toFloat(), v) }
    // color = 0xFF80DEEA, toInt(), dataset label = "Steps", Description = "Step count per hour"
    private fun setupChart() {
        val entries = hourlySteps.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }
        val dataset = BarDataSet(entries, "Steps")
        barChart.data = BarData(dataset)
        barChart.invalidate()
    }

    // Final Report:
    // Show tvAccuracy warning if accuracy == SENSOR_STATUS_UNRELIABLE or ACCURACY_LOW
    override fun onAccuracyChanged(Sensor: Sensor, accuracy: Int) {

    }

    // Final Report:
    // Clear samples, set isCalibration = true, disable, Toast "hold the device steady"
    private fun startCalibration() {

    }

    // Final Report:
    // Average calibrationSamples for each axis -> set baselineX, baselineY, baselineZ
    private fun finishCalibration() {

    }
}
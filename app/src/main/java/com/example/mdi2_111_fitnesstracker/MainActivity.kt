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
}
package com.example.calculator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Debug
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.min

class CircleView(act: Context) : View(act) {
    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val rotation = windowManager.defaultDisplay.rotation
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    private val paint = Paint()
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private val radius: Float = 30f

    init {
        paint.color = Color.RED
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    fun updatePosition(x: Float, y: Float) {

        val scaleFactor = min(width.toFloat() / 100f, height.toFloat() / 100f)

        if(rotation == Surface.ROTATION_0) {
            centerX -= x * scaleFactor
            centerY += y * scaleFactor
        }
        if(rotation == Surface.ROTATION_90) {
            centerY += x * scaleFactor
            centerX += y * scaleFactor
        }
        if(rotation == Surface.ROTATION_270) {
            centerY -= x * scaleFactor
            centerX -= y * scaleFactor
        }

        val maxWidth = width.toFloat()
        val maxHeight = height.toFloat()
        centerX = centerX.coerceIn(radius, maxWidth - radius)
        centerY = centerY.coerceIn(radius, maxHeight - radius)
        invalidate()
    }

    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            updatePosition(event.values[0], event.values[1])
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    fun getPoint(): Point {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return Point((centerX).toInt(), (centerY + result).toInt())
    }

    fun startGame(): Boolean {
        centerY = height.toFloat() / 2f
        centerX = width.toFloat() / 2f

        sensorManager = this.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager.registerListener(
                accelerometerListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        } else {
            return false
        }
        return true
    }

    fun stopGame() {
        sensorManager.unregisterListener(accelerometerListener)
    }
}

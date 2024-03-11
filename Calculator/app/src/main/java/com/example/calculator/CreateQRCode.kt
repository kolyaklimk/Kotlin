package com.example.calculator

import android.content.Context.WINDOW_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.view.Display
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity


class CreateQRCode {
    fun generateQRCode(text: String, act: AppCompatActivity): Bitmap? {
        val windowManager: WindowManager = act.getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay

        val point: Point = Point()
        display.getSize(point)

        val width = point.x
        val height = point.y

        var dimen = if (width < height) width else height
        dimen = dimen * 8 / 10

        val qrgEncoder = QRGEncoder(text, null, QRGContents.Type.TEXT, dimen)
        qrgEncoder.colorBlack = act.getColor(R.color.Button4_text)
        qrgEncoder.colorWhite = act.getColor(R.color.Button4)
        return qrgEncoder.bitmap
    }

    fun changeBrightness(act: AppCompatActivity, brightness: Float) {
        val window = act.window
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams
    }
}
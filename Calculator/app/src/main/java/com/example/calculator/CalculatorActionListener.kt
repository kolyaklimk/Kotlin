package com.example.calculator

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class CalculatorActionListener(act: AppCompatActivity) {

    private var act = act
    private val text_main = act.findViewById<EditText>(R.id.text_main)
    private val text_solve = act.findViewById<TextView>(R.id.text_solve)
    private val linear_additionally = act.findViewById<LinearLayout>(R.id.linear_additionally)
    private val linear_all_additionally =
        act.findViewById<LinearLayout>(R.id.linear_all_additionally)
    private var INV = true
    private var IsGame = false
    private var IsGameTouch = false
    private var circleView = CircleView(act)


    fun SubscribeButtons() {
        text_main.showSoftInputOnFocus = false
        text_solve.showSoftInputOnFocus = false


        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        (act.findViewById<FrameLayout>(android.R.id.content)).addView(circleView, layoutParams)
        circleView.visibility = View.INVISIBLE


        for (i in 0..9) SubscribeButtonByString("b_$i")

        for (i in 1..9) SubscribeButtonByString("b_ad$i")

        act.findViewById<Button>(R.id.b_ac).setOnClickListener {
            if (!IsGame || (IsGame && IsGameTouch)) {
                text_main.text.clear()
                text_solve.text = ""

                IsGameTouch = false
            }
        }

        act.findViewById<Button>(R.id.b_INV).setOnClickListener {
            if (!IsGame || (IsGame && IsGameTouch)) {
                if (INV) {
                    act.findViewById<Button>(R.id.b_ad7).setText("asin")
                    act.findViewById<Button>(R.id.b_ad8).setText("acos")
                    act.findViewById<Button>(R.id.b_ad9).setText("atan")
                } else {
                    act.findViewById<Button>(R.id.b_ad7).setText("sin")
                    act.findViewById<Button>(R.id.b_ad8).setText("cos")
                    act.findViewById<Button>(R.id.b_ad9).setText("tan")
                }
                INV = !INV

                IsGameTouch = false
            }
        }

        act.findViewById<Button>(R.id.b_additionally).setOnClickListener {
            if (!IsGame || (IsGame && IsGameTouch)) {
                val layoutParams = linear_all_additionally.layoutParams as LinearLayout.LayoutParams

                if (linear_additionally.visibility == View.GONE) {
                    linear_additionally.visibility = View.VISIBLE
                    layoutParams.weight = 0.8f
                    it.rotation = 0f
                } else {
                    linear_additionally.visibility = View.GONE
                    layoutParams.weight = 1f
                    it.rotation = 180f
                }
                linear_all_additionally.layoutParams = layoutParams

                IsGameTouch = false
            }
        }

        act.findViewById<Button>(R.id.b_solve).setOnClickListener {
            if (!IsGame || (IsGame && IsGameTouch)) {
                if (text_solve.text != "Error") {
                    text_main.setText(text_solve.text)
                } else {
                    text_main.text.clear()
                }
                text_solve.text = ""
                text_main.setSelection(text_main.length())

                IsGameTouch = false
            }
        }

        act.findViewById<Button>(R.id.b_del).setOnClickListener {
            if (!IsGame || (IsGame && IsGameTouch)) {
                val selectionStart = text_main.selectionStart

                if (selectionStart > 0 && text_main.text.isNotEmpty()) {
                    text_main.text.delete(selectionStart - 1, selectionStart)
                }
                text_solve.text = StringCalculating.Calculating(text_main.text.toString())

                IsGameTouch = false
            }
        }

        SubscribeButtonById(R.id.b_o_brackets)
        SubscribeButtonById(R.id.b_c_brackets)
        SubscribeButtonById(R.id.b_dot)
        SubscribeButtonById(R.id.b_div)
        SubscribeButtonById(R.id.b_dot)
        SubscribeButtonById(R.id.b_minus)
        SubscribeButtonById(R.id.b_mult)
        SubscribeButtonById(R.id.b_plus)
        SubscribeButtonById(R.id.b_procent)

        SetUpMenu()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                val scanResult = data?.getStringExtra("SCAN_RESULT")
                InputSymbolToMain(scanResult.toString())
            }
        }
    }

    private fun SubscribeButtonByString(name: String) {
        val buttonId = act.resources.getIdentifier(name, "id", act.packageName)
        val button = act.findViewById<Button>(buttonId)
        button.setOnClickListener {
            InputSymbolToMain(button.text.toString())
        }
    }

    private fun SubscribeButtonById(name: Int) {
        val button = act.findViewById<Button>(name)
        button.setOnClickListener {
            InputSymbolToMain(button.text.toString())
        }
    }

    private fun InputSymbolToMain(string: String) {
        if (!IsGame || (IsGame && IsGameTouch)) {
            val cursorPosition = text_main.selectionStart

            val newText = text_main.text.substring(0, cursorPosition) + string +
                    text_main.text.substring(cursorPosition)

            text_main.setText(newText)
            text_main.setSelection(cursorPosition + string.length)

            text_solve.text = StringCalculating.Calculating(text_main.text.toString())

            IsGameTouch = false
        }
    }

    private fun SetUpMenu() {
        val b_menu = act.findViewById<Button>(R.id.b_menu)
        b_menu.setOnClickListener {
            if (!IsGameTouch) {
                val popupMenu = PopupMenu(this.act, b_menu)
                popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.Snan_QR -> {
                            if (!IsGame || (IsGame && IsGameTouch)) {
                                act.startActivityForResult(
                                    Intent(
                                        act,
                                        ScanQRActivity::class.java
                                    ), 100
                                )

                                IsGameTouch = false
                            }
                        }

                        R.id.Create_QR -> {
                            if (!IsGame || (IsGame && IsGameTouch)) {
                                try {
                                    if (text_main.text.isNotEmpty()) {
                                        val inflater =
                                            act.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                        val popupView =
                                            inflater.inflate(R.layout.create_qr_code, null)

                                        val qrImageView: ImageView =
                                            popupView.findViewById(R.id.image_qr)

                                        val qrCodeBitmap =
                                            CreateQRCode().generateQRCode(
                                                text_main.text.toString(),
                                                act
                                            )

                                        qrImageView.setImageBitmap(qrCodeBitmap)

                                        val popupWindow = PopupWindow(
                                            popupView,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            true
                                        )

                                        popupWindow.animationStyle = R.style.PopupAnimation

                                        CreateQRCode().changeBrightness(act, 1.0f)
                                        popupWindow.setOnDismissListener {
                                            CreateQRCode().changeBrightness(
                                                act,
                                                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                                            )
                                        }

                                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
                                    } else {
                                        Toast.makeText(
                                            this.act,
                                            "Main text is empty",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (ex: Exception) {
                                    Toast.makeText(
                                        this.act,
                                        "QR code creation ERROR",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                IsGameTouch = false
                            }
                        }

                        R.id.Gravity_Game -> {
                            if (IsGame) {
                                circleView.stopGame()
                                circleView.visibility = View.INVISIBLE
                                IsGame = !IsGame
                            } else {
                                if (circleView.startGame()) {
                                    circleView.visibility = View.VISIBLE
                                    IsGame = !IsGame
                                }
                            }
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }

    fun TouchButtons() {
        var point = circleView.getPoint()
        var time = System.currentTimeMillis()
        val downEvent = MotionEvent.obtain(
            time,
            time,
            MotionEvent.ACTION_DOWN,
            point.x.toFloat(),
            point.y.toFloat(),
            0
        )

        val upEvent = MotionEvent.obtain(
            time,
            time,
            MotionEvent.ACTION_UP,
            point.x.toFloat(),
            point.y.toFloat(),
            0
        )

        IsGameTouch = true

        act.dispatchTouchEvent(downEvent)
        act.dispatchTouchEvent(upEvent)

        downEvent.recycle()
        upEvent.recycle()
    }
}
package com.example.calculator

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import android.widget.EditText
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


    fun SubscribeButtons() {

        text_main.showSoftInputOnFocus = false
        text_solve.showSoftInputOnFocus = false


        for (i in 0..9) SubscribeButtonByString("b_$i")

        for (i in 1..9) SubscribeButtonByString("b_ad$i")

        act.findViewById<Button>(R.id.b_ac).setOnClickListener {
            text_main.text.clear()
            text_solve.text = ""
        }


        act.findViewById<Button>(R.id.b_INV).setOnClickListener {
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
        }

        act.findViewById<Button>(R.id.b_additionally).setOnClickListener {
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
        }

        act.findViewById<Button>(R.id.b_solve).setOnClickListener {
            if (text_solve.text != "Error") {
                text_main.setText(text_solve.text)
            } else {
                text_main.text.clear()
            }
            text_solve.text = ""
            text_main.setSelection(text_main.length())
        }

        act.findViewById<Button>(R.id.b_del).setOnClickListener {
            val selectionStart = text_main.selectionStart

            if (selectionStart > 0 && text_main.text.isNotEmpty()) {
                text_main.text.delete(selectionStart - 1, selectionStart)
            }
            text_solve.text = StringCalculating.Calculating(text_main.text.toString())
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

        val b_menu = act.findViewById<Button>(R.id.b_menu)
        b_menu.setOnClickListener {
            val popupMenu = PopupMenu(this.act, b_menu)
            popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.Snan_QR -> {
                        act.startActivityForResult(Intent(act, ScanQRActivity::class.java), 100)
                    }

                    R.id.Create_QR -> {
                        try {
                            if (text_main.text.isNotEmpty()) {
                                val inflater =
                                    act.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                val popupView = inflater.inflate(R.layout.create_qr_code, null)

                                val qrImageView: ImageView = popupView.findViewById(R.id.image_qr)

                                val qrCodeBitmap =
                                    CreateQRCode().generateQRCode(text_main.text.toString(), act)

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
                                    CreateQRCode().changeBrightness(act, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
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
                    }
                }
                true
            })
            popupMenu.show()
        }
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
        val cursorPosition = text_main.selectionStart

        val newText = text_main.text.substring(0, cursorPosition) + string +
                text_main.text.substring(cursorPosition)

        text_main.setText(newText)
        text_main.setSelection(cursorPosition + string.length)

        text_solve.text = StringCalculating.Calculating(text_main.text.toString())
    }

}
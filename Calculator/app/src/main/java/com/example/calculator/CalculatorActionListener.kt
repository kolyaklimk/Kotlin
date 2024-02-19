package com.example.calculator

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalculatorActionListener(act: AppCompatActivity) {

    private var act = act
    private val text_main = act.findViewById<EditText>(R.id.text_main)
    private val text_solve = act.findViewById<TextView>(R.id.text_solve)
    private val linear_additionally = act.findViewById<LinearLayout>(R.id.linear_additionally)
    private val linear_all_additionally =
        act.findViewById<LinearLayout>(R.id.linear_all_additionally)

    fun SubscribeButtons() {

        for (i in 0..9) SubscribeButtonByString("b_$i")

        for (i in 1..10) SubscribeButtonByString("b_ad$i")

        act.findViewById<Button>(R.id.b_ac).setOnClickListener {
            text_main.text.clear()
            text_solve.text = ""
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

            // промежуточное решение
        }

        act.findViewById<Button>(R.id.b_solve).setOnClickListener {
            text_solve.text = ""
            // Логика Решения примера
        }

        act.findViewById<Button>(R.id.b_del).setOnClickListener {
            val selectionStart = text_main.selectionStart

            if (selectionStart > 0 && text_main.text.isNotEmpty()) {
                text_main.text.delete(selectionStart - 1, selectionStart)
            }
            // промежуточное решение
        }

        SubscribeButtonById(R.id.b_o_brackets)
        SubscribeButtonById(R.id.b_c_brackets)
        SubscribeButtonById(R.id.b_dot)
        SubscribeButtonById(R.id.b_div)
        SubscribeButtonById(R.id.b_dot)
        SubscribeButtonById(R.id.b_menu)
        SubscribeButtonById(R.id.b_minus)
        SubscribeButtonById(R.id.b_mult)
        SubscribeButtonById(R.id.b_plus)
        SubscribeButtonById(R.id.b_procent)
    }

    private fun SubscribeButtonByString(name: String) {
        val buttonId = act.resources.getIdentifier(name, "id", act.packageName)
        val button = act.findViewById<Button>(buttonId)
        button.setOnClickListener {
            InputSymbolToMain(button.text.toString())
        }
        // промежуточное решение
    }

    private fun SubscribeButtonById(name: Int) {
        val button = act.findViewById<Button>(name)
        button.setOnClickListener {
            InputSymbolToMain(button.text.toString())
        }
        // промежуточное решение
    }

    private fun InputSymbolToMain(string: String) {
        val cursorPosition = text_main.selectionStart

        val newText =
            text_main.text.substring(0, cursorPosition) + string + text_main.text.substring(
                cursorPosition
            )

        text_main.setText(newText)
        text_main.setSelection(cursorPosition + 1)
    }
}
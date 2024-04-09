package com.example.calculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth)

        val firebase = CalculatorActionListener.MyDataManager.firebase

        findViewById<Button>(R.id.b_reg).setOnClickListener {
            firebase.register(
                findViewById<EditText>(R.id.editTextEmail).text.toString(),
                findViewById<EditText>(R.id.editTextPassword).text.toString(),
                this
            )
        }

        findViewById<Button>(R.id.b_log).setOnClickListener {
            firebase.logIn(
                findViewById<EditText>(R.id.editTextEmail).text.toString(),
                findViewById<EditText>(R.id.editTextPassword).text.toString(),
                this
            )
            { success ->
                if (success) {
                    finish()
                }
            }
        }
    }
}
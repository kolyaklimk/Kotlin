package com.example.calculator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (CalculatorActionListener.MyDataManager.firebase.isAuth()) {
                            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Authentication of biometric information")
                                .setSubtitle("Install additional protection")
                                .setNegativeButtonText("No")
                                .build()

                            val biometricPrompt =
                                BiometricPrompt(
                                    this,
                                    object : BiometricPrompt.AuthenticationCallback() {
                                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                            val sharedPrefs =
                                                applicationContext.getSharedPreferences(
                                                    "IsBio",
                                                    Context.MODE_PRIVATE
                                                )
                                            sharedPrefs.edit().putBoolean("IsBio", true).apply()
                                            finish()
                                        }

                                        override fun onAuthenticationFailed() {
                                            val sharedPrefs =
                                                applicationContext.getSharedPreferences(
                                                    "IsBio",
                                                    Context.MODE_PRIVATE
                                                )
                                            sharedPrefs.edit().putBoolean("IsBio", false).apply()
                                        }

                                        override fun onAuthenticationError(
                                            errorCode: Int,
                                            errString: CharSequence
                                        ) {
                                            super.onAuthenticationError(errorCode, errString)
                                            val sharedPrefs =
                                                applicationContext.getSharedPreferences(
                                                    "IsBio",
                                                    Context.MODE_PRIVATE
                                                )
                                            sharedPrefs.edit().putBoolean("IsBio", false).apply()
                                            finish()
                                        }
                                    })
                            biometricPrompt.authenticate(promptInfo)
                        }
                    }
                }
            }
        }
    }
}
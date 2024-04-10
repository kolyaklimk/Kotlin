package com.example.calculator

import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.Manifest
import android.content.Context
import androidx.biometric.BiometricPrompt

object CheckSingl {
    var isFirst = true
}

class MainActivity : AppCompatActivity() {
    private var calculatorListener: CalculatorActionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        calculatorListener = CalculatorActionListener(this)
        calculatorListener?.SubscribeButtons()


        val sharedPrefs = applicationContext.getSharedPreferences("IsBio", Context.MODE_PRIVATE)


        if (CheckSingl.isFirst && sharedPrefs.getBoolean("IsBio",false)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (CalculatorActionListener.MyDataManager.firebase.isAuth()) {
                    showBiometricPrompt()
                }
            } else {
                requestBiometricPermission()
            }
            CheckSingl.isFirst = !CheckSingl.isFirst
        }
    }

    //////////////////////////////

    private fun requestBiometricPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.USE_BIOMETRIC),
            1001
        )
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication of biometric information")
            .setSubtitle("Use your fingerprint to logIn")
            .setNegativeButtonText("Password")
            .build()

        val biometricPrompt =
            BiometricPrompt(this, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                }

                override fun onAuthenticationFailed() {
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        startActivity(Intent(this@MainActivity, LogInActivity::class.java))
                    }
                    CalculatorActionListener.MyDataManager.firebase.logOut()
                    findViewById<Button>(R.id.b_history).visibility = View.GONE
                    findViewById<Button>(R.id.b_user).text = "LogIn"
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (CheckSingl.isFirst) {
                    showBiometricPrompt()
                    CheckSingl.isFirst = !CheckSingl.isFirst
                }
            } else {
                CalculatorActionListener.MyDataManager.firebase.logOut()
                findViewById<Button>(R.id.b_history).visibility = View.GONE
                findViewById<Button>(R.id.b_user).text = "LogIn"
            }
        }
    }

    ///////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        calculatorListener?.handleActivityResult(requestCode, resultCode, data)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            calculatorListener?.TouchButtons()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()
        if (CalculatorActionListener.MyDataManager.firebase.isAuth()) {
            findViewById<Button>(R.id.b_user).text = "LogOut"
            findViewById<Button>(R.id.b_history).visibility = View.VISIBLE
        } else {
            findViewById<Button>(R.id.b_history).visibility = View.GONE
        }
    }
}
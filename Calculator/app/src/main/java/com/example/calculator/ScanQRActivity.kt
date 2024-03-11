package com.example.calculator

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*

class ScanQRActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qractivity)

        if (!hasCameraPermission()) {
            requestCameraPermission()
        }

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                codeScanner.scanMode = ScanMode.PREVIEW
                val text_it = it.text
                val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .create()
                val view = layoutInflater.inflate(R.layout.custom_alert, null)
                val button1 = view.findViewById<Button>(R.id.yes_button)
                val button2 = view.findViewById<Button>(R.id.no_button)
                view.findViewById<TextView>(R.id.res_alert).setText(text_it)
                builder.setView(view)
                button1.setOnClickListener {
                    builder.dismiss()
                    codeScanner.scanMode = ScanMode.CONTINUOUS

                    val resultIntent = Intent()
                    resultIntent.putExtra("SCAN_RESULT", text_it)
                    setResult(RESULT_OK, resultIntent)

                    finish()
                }
                button2.setOnClickListener {
                    builder.dismiss()
                    codeScanner.scanMode = ScanMode.CONTINUOUS
                }
                builder.setOnDismissListener {
                    codeScanner.scanMode = ScanMode.CONTINUOUS
                }
                builder.show()
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this, "Camera error: ${it.message}", Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            123
        )
    }
}
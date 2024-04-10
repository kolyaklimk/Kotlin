package com.example.calculator

import android.content.res.Configuration
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.api.ResourceDescriptor.History
import com.google.firebase.Firebase
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await

class FirebaseHelper : AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    private var tryingEmail = ""
    private var tryingcount = 0


    fun isAuth(): Boolean {
        return auth.currentUser != null
    }

    fun logIn(
        email: String,
        password: String,
        act: AppCompatActivity,
        callback: (Boolean) -> Unit
    ) {
        var res = false

        if (email == tryingEmail) {
            tryingcount++
        } else {
            tryingEmail = email
            tryingcount = 0
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(act) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    tryingcount = 0
                    if (user != null && user.isEmailVerified) {
                        res = true
                        Toast.makeText(
                            act,
                            "LogIn success!",
                            Toast.LENGTH_SHORT
                        ).show()

                        getTheme(act)
                    } else {
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    Toast.makeText(
                                        act,
                                        "Please verify your email address! Check your Email!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        act,
                                        "Verification email not sent. Check your Email!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(
                        act,
                        "Invalid Email or password!",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (tryingcount >= 3) {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { resetPasswordTask ->
                                if (resetPasswordTask.isSuccessful) {
                                    Toast.makeText(
                                        act,
                                        "Password reset email sent.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        act,
                                        "Password reset email not sent. Please wait few minutes",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
                callback.invoke(res)
            }
    }


    fun logOut() {
        auth.signOut()
    }

    fun register(email: String, password: String, act: AppCompatActivity) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(act) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    act,
                                    "Register success! Verification email sent.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    act,
                                    "Verification email not sent, but register success!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    auth.signOut()
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    if (errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                        Toast.makeText(
                            act,
                            "Email already exist!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Toast.makeText(
                        act,
                        "Register error!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun getPeriodOfHistory(startAt: Int, endAt: Int, act: AppCompatActivity): List<String> {
        val stringList = mutableListOf<String>()
        if (isAuth()) {
            db.collection("User").document(auth.currentUser?.uid.toString())
                .collection("History").startAt(startAt).endAt(endAt).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val value = document.getString("Text")
                            value?.let { stringList.add(it) }
                        }
                        // Обработка полученного списка строк
                        // Здесь вы можете вызвать функцию или передать список дальше для обработки
                    } else {
                        // Обработка ошибки
                        val exception = task.exception
                        // Обработка исключения, если требуется
                    }
                }
            return stringList
        } else {
            // fail
        }
        return stringList
    }

    fun sendHistory(Text: String, act: AppCompatActivity) {
        if (isAuth()) {
            val historyData = hashMapOf(
                "Text" to Text
            )
            db.collection("Users").document(auth.currentUser?.uid.toString())
                .collection("History").add(historyData)
                .addOnCompleteListener(act) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            act,
                            "History updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            act,
                            "History not updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun updateTheme(theme: String, act: AppCompatActivity) {
        if (isAuth()) {
            val newData = hashMapOf(
                "Theme" to theme
            )
            db.collection("Users").document(auth.currentUser?.uid.toString())
                .set(newData)
                .addOnSuccessListener(act) {
                    Toast.makeText(
                        act,
                        "Theme updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        act,
                        "Theme not updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun getTheme(act: AppCompatActivity) {
        if (isAuth()) {
            val userDocument = db.collection("Users").document(auth.currentUser?.uid.toString())
            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val theme = documentSnapshot.getString("Theme")
                        if (theme != null) {
                            if (theme == "light") {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            } else {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                        } else {
                            val currentNightMode =
                                act.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

                            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                                updateTheme("dark", act)
                            } else {
                                updateTheme("light", act)
                            }
                        }
                    } else {
                        Toast.makeText(
                            act,
                            "User theme not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}
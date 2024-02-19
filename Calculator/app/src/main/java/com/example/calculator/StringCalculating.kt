package com.example.calculator

import android.util.Log
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class StringCalculating {
    companion object {
        fun Calculating(string: String): String {
            try {
                val ex = ExpressionBuilder(string).build()
                val res = ex.evaluate()

                if (res == res.toLong().toDouble()) {
                    return res.toLong().toString()
                }
                return res.toString()
            } catch (ex: Exception) {
                return "Error"
            }
        }
    }
}
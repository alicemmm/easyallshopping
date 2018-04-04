package com.lomoasia.easyallshopping.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.lomoasia.easyallshopping.R

class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)


    }

    private fun test(a: Int, b: Int): Int {
        return a + b
    }

    fun sum(a: Int, b: Int) = a + b

    fun nonSum(a: Int, b: Int): Unit {
        Log.d("tag", "" + (a + b))
    }

    fun varTest(vararg a: Int) {
        for (vt in a) {
            Log.d("tag", "" + a)
        }
    }
}
package com.anirudh.logutilexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anirudh.logutil.LogDebug

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LogDebug.d("My custom log")
    }
}
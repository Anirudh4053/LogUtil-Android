package com.anirudh.logutil

import android.util.Log

class LogDebug {
    companion object {
        val TAG = "SUPER_AWESOME_APP"
        fun d(message:String) {
            Log.d(TAG,message)
        }
    }
}
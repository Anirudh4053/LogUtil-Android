package com.anirudh.errorutil

import android.util.Log

class ErrorDebug {
    companion object {
        val TAG = "SUPER_AWESOME_APP"
        fun e(message:String) {
            Log.e(TAG,message)
        }
    }
}
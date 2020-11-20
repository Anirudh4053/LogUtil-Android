package com.anirudh.locationpermissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class LocationRunTimePermission {
    companion object {
        const val REQUEST_PERMISSION_LOCATION = 10
        const val REQUEST_PERMISSION_SETTING = 103
        fun with(activity: Activity): Builder {
            return Builder(activity)
        }
        class Builder(activity: Activity) {
            private var activity = activity
            fun checkPermission() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    } else {
                        // Show the permission request
                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_PERMISSION_LOCATION)
                    }
                }
            }
        }
    }
}
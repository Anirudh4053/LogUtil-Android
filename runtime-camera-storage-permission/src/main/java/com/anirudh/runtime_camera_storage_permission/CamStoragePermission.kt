package com.anirudh.runtime_camera_storage_permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class CamStoragePermission {
    companion object {
        const val PERMISSION_CALLBACK_CONSTANT = 102
        const val REQUEST_PERMISSION_SETTING = 103
        const val PERMISSION_REQUIRED = Manifest.permission.WRITE_EXTERNAL_STORAGE
        var sentToSettings = false
        fun setSettingFlag(flag:Boolean) {
            sentToSettings = flag
        }
        val getSettingFlag get(): Boolean {
            return sentToSettings
        }
        //private var activity = context
        private var permissionsRequired = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private val PREF_NAME = "image_picker"
        fun with(activity: Activity): Builder {
            return Builder(activity)
        }
        class Builder(activity: Activity) {
            private var activity = activity
            fun checkPermission():Boolean {
                if (ActivityCompat.checkSelfPermission(activity, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsRequired[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsRequired[2])
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.requestPermissions(
                                permissionsRequired,
                                PERMISSION_CALLBACK_CONSTANT
                            )

                        }
                    } else if (getPermissionRequest(activity)) {
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Permission Denied")
                        builder.setMessage("This app need permissions to use the feature")
                        builder.setPositiveButton("Grant") { dialog, which ->
                            dialog.cancel()
                            setSettingFlag(true)
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", activity.packageName, null)
                            intent.data = uri
                            activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                        }
                        builder.setNegativeButton("Cancel") { dialog, which ->
                            run {
                                //finish()
                                dialog.cancel()
                            }
                        }
                        builder.show()
                    } else {
                        //just request the permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.requestPermissions( permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
                        }
                    }
                    setPermissionRequest(activity,true)
                } else {
                    return true
                }
                return false
            }
            private fun setPermissionRequest(context: Context, value: Boolean) {
                val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean(PERMISSION_REQUIRED, value)
                editor.apply()
            }

            private fun getPermissionRequest(context: Context): Boolean {
                val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                return prefs.getBoolean(PERMISSION_REQUIRED, false)
            }
        }

    }
}
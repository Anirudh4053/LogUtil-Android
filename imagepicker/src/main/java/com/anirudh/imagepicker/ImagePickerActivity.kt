package com.anirudh.imagepicker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_image_picker.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*

//gallery
const val GALLERY_REQUEST = 21
const val VIDEO_REQUEST = 34
const val CROP_REQUEST = 20
const val PERMISSION_CALLBACK_CONSTANT = 102
const val PERMISSION_CALLBACK_CONSTANT1 = 104
const val REQUEST_PERMISSION_SETTING = 103
const val PERMISSION_REQUIRED = Manifest.permission.WRITE_EXTERNAL_STORAGE

class ImagePickerActivity : AppCompatActivity() {
    //check permission
    private var sentToSettings = false
    private var permissionsRequired = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val PREF_NAME = "image_picker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        println("Image picker called")
        checkPermission()

        getImageBtn.setOnClickListener {
            checkPermission()
        }
        includeToolbar.apply {
            this.backBtn.setOnClickListener {
                onBackPressed()
            }
        }
    }
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        permissionsRequired,
                        PERMISSION_CALLBACK_CONSTANT
                    )

                }
            } else if (getPermissionRequest(this)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Permission Denied")
                builder.setMessage("This app needs permissions to use the features")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    //Toast.makeText(this, "Go to Permissions to Grant Camera and Read-Write", Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    run {
                        finish()
                        dialog.cancel()
                    }
                }
                builder.show()
            } else {
                //just request the permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions( permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
                }
            }
            setPermissionRequest(this,true)
        } else {
            proceedAfterPermission()
        }
    }
    /*override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
                proceedAfterPermission()
            } else {
                checkPermission()
            }
        }
    }*/
    private fun proceedAfterPermission() {
        Toast.makeText(this,"open gallery",Toast.LENGTH_SHORT).show()
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }

            if (allgranted) {
                //openGallery()
                Toast.makeText(this,"open gallery",Toast.LENGTH_SHORT).show()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
            ) {
                //txtPermissions.setText("Permissions Required");
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs Camera and Read-Write permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                            permissionsRequired,
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                checkPermission()
                //Toast.makeText(this, "Unable to get Permission", Toast.LENGTH_LONG).show()
            }
        }

    }
}
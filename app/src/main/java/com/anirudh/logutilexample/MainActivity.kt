package com.anirudh.logutilexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.anirudh.errorutil.ErrorDebug
import com.anirudh.imagepicker.ImagePickerActivity
import com.anirudh.imagepicker.PERMISSION_CALLBACK_CONSTANT
import com.anirudh.imagepicker.RunTimePermissions
import com.anirudh.imagepicker.RunTimePermissions.Companion.getSettingFlag
import com.anirudh.imagepicker.RunTimePermissions.Companion.setSettingFlag
import com.anirudh.locationpermissions.LocationRunTimePermission
import com.anirudh.locationpermissions.LocationRunTimePermission.Companion.REQUEST_PERMISSION_SETTING
import com.anirudh.logutil.LogDebug
import com.anirudh.runtime_camera_storage_permission.CamStoragePermission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var permissionsRequired = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    //var sentToSettings = false


    //location
    var sentToSettingsLocation = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LogDebug.d("My custom log")
        ErrorDebug.e("My custom error log")

        selectBtn.setOnClickListener {
            /*val i = Intent(this,ImagePickerActivity::class.java)
            startActivity(i)*/
            CamStoragePermission.with(this).checkPermission()
            /*if(RunTimePermissions(this).checkPermission()) {
                proceedAfterPermission()
            }*/

        }

        permission.setOnClickListener {
            LocationRunTimePermission.with(this).checkPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CamStoragePermission.PERMISSION_CALLBACK_CONSTANT) {
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
                proceedAfterPermission()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
            ) {
                //txtPermissions.setText("Permissions Required");
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This needs Camera and Read-Write permissions.")
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
                CamStoragePermission.with(this).checkPermission()
            }
        }
        if (requestCode == CamStoragePermission.PERMISSION_CALLBACK_SUCCESS) {
            proceedAfterPermission()
        }

        if (requestCode == LocationRunTimePermission.REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //success
                Toast.makeText(this,"Location created successfully",Toast.LENGTH_SHORT).show()
            } else {
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Location Permissions")
                builder.setMessage("This app needs location permissions.")
                builder.setCancelable(false)
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettingsLocation = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    //showToast("Go to Permissions to Grant Camera and Read-Write", Toast.LENGTH_LONG)
                }
                builder.show()
            }
        }

    }

    private fun proceedAfterPermission() {
        //open camera
        Toast.makeText(this,"open gallery runtime permission", Toast.LENGTH_SHORT).show()
    }
    override fun onPostResume() {
        super.onPostResume()
        if (getSettingFlag) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
                setSettingFlag(false)
                proceedAfterPermission()
            } else {
                CamStoragePermission.with(this).checkPermission()
            }
        }
        if (sentToSettingsLocation) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                println("start location")
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Location Permissions")
                builder.setMessage("This app needs location permissions.")
                builder.setCancelable(false)
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettingsLocation = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(this,"Go to Permissions to Grant Location", Toast.LENGTH_SHORT).show()
                }
                //builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()

                return
            }
            else{
                Toast.makeText(this,"location granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
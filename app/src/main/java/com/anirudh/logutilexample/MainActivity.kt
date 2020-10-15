package com.anirudh.logutilexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.anirudh.errorutil.ErrorDebug
import com.anirudh.imagepicker.ImagePickerActivity
import com.anirudh.imagepicker.PERMISSION_CALLBACK_CONSTANT
import com.anirudh.imagepicker.RunTimePermissions
import com.anirudh.imagepicker.RunTimePermissions.Companion.getSettingFlag
import com.anirudh.imagepicker.RunTimePermissions.Companion.setSettingFlag
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
                if(RunTimePermissions(this).checkPermission()) {
                    proceedAfterPermission()
                }
            }
        }

    }

    private fun proceedAfterPermission() {
        //open camera
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
                if(RunTimePermissions(this).checkPermission()) {
                    proceedAfterPermission()
                }
            }
        }
    }
}
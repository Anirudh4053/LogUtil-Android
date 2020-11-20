package com.anirudh.logutilexample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var locationGps:Location

    lateinit var locationManager: LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LogDebug.d("My custom log")
        ErrorDebug.e("My custom error log")
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        selectBtn.setOnClickListener {
            /*val i = Intent(this,ImagePickerActivity::class.java)
            startActivity(i)*/
            CamStoragePermission.with(this).checkPermission()
            /*if(RunTimePermissions(this).checkPermission()) {
                proceedAfterPermission()
            }*/

        }
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
        permission.setOnClickListener {
            if(LocationRunTimePermission.with(this).checkPermission()) {
                onSuccessLocation()
            }
        }
    }
    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()


    }

    private fun onSuccessLocation() {
        /*val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {
            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationGps = p0
                            println("Longitude", locationGps!!.longitude,"Latitude", locationGps!!.latitude)
                        }
                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationNetwork = p0
                            if (uid != null) {
                                Firebase.firestore.collection("Drivers").document(uid).update("Longitude",
                                    locationNetwork!!.longitude,"Latitude", locationNetwork!!.latitude)
                                    .addOnSuccessListener {
                                        Snackbar.make(takeabreak, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Snackbar.make(takeabreak, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    if (uid != null) {
                        Firebase.firestore.collection("Drivers").document(uid).update("Longitude",
                            locationGps!!.longitude,"Latitude", locationGps!!.latitude)
                            .addOnSuccessListener {
                                Snackbar.make(takeabreak, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Snackbar.make(takeabreak, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                            }
                    }
                }else{
                    if (uid != null) {
                        Firebase.firestore.collection("Drivers").document(uid).update("Longitude",
                            locationNetwork!!.longitude,"Latitude", locationNetwork!!.latitude)
                            .addOnSuccessListener {
                                Snackbar.make(takeabreak, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Snackbar.make(takeabreak, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                            }
                    }
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }*/




        try {
            // Request location updates
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 0f, locationListener)
            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 0f, locationListener)
            }

        } catch(ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0L,0L,locationListener)
    }
    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mainTV.text = ("" + location.longitude + ":" + location.latitude)
            locationManager.removeUpdates(this)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
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
                onSuccessLocation()
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
                onSuccessLocation()
                Toast.makeText(this,"location granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
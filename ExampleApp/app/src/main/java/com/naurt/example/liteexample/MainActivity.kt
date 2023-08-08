package com.naurt.example.liteexample

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naurt.sdk.NaurtLite
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private var naurtLite: NaurtLite? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (!this.hasLocationPermission()) {
            this.requestLocationPermission()
        } else{
            val textView = findViewById<TextView>(R.id.locationTextView)
            textView.text = "Location access provided"
            instantiateNaurt()
        }
    }


    private fun instantiateNaurt(){
        this.naurtLite = NaurtLite(
            BuildConfig.API_KEY,
            applicationContext,
            JSONObject(mapOf("example_app" to true))
        )

        val textView = findViewById<TextView>(R.id.naurtTextView)


        // Check after a delay to ensure the Naurt key has been validated.
        Handler(Looper.getMainLooper()).postDelayed({
            if (this.naurtLite?.getIsValidated()?.isValid() == true){
                textView.text = "Naurt has been successfully validated"
            }
        }, 2000)


        // A button which updates the metadata associated with Naurt!
        // Use this to link delivery addresses and other data with parking spots and building entrances
        // generated from Naurt.
        val metaButton = findViewById<Button>(R.id.metaButton);
        metaButton.setOnClickListener {
            val newMetadata = JSONObject()
            newMetadata.put("example_app", "true")
            newMetadata.put("refresh_time", System.currentTimeMillis())

            this.naurtLite?.updateMetadata(newMetadata)

        }


    }

    // Check if the app has permission to access location
    private fun hasLocationPermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        )
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    // Request location permission from the user
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val textView = findViewById<TextView>(R.id.locationTextView)
                textView.text = "Location access provided"
                instantiateNaurt()
            } else {
                Log.d("Naurt", "I haven't got the permissions!")
                // Permission denied, handle the situation where the user denied the permission.
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.naurtLite?.onDestroy()
    }


}
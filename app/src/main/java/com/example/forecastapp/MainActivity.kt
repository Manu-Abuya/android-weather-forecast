package com.example.forecastapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    //JSON weather url
    var weather_url = ""

    //api id for url
    var api_id = "fe5b45557a624c1db8e527ed7635df33"

    private lateinit var textView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // lint textView which will be linked to the temperature
        textView = findViewById(R.id.textView)

        //instance of the fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.e("lat", weather_url)

        //button function to get the coordinates
        btVar.setOnClickListener {
            Log.e("lat", "onClick")
            //function to find the coordinates of the last location
            obtainLocation()
        }
    }

    @SuppressLint("'MissingPermission")
    private fun obtainLocation() {
        Log.e("lat", "function")
        //get the last location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
            //get the latitude and longitude and create the http URL
            weather_url = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude + "&lon=" + location?.longitude + "&key=" + api_id
            Log.e("lat", weather_url.toString())
            //this function fetches data from URL
            getTemp()
        }
    }

    fun getTemp() {
        // Instantiate the RequestQueue
        val queue = Volley.newRequestQueue(this)
        val url: String = weather_url
        Log.e("lat", url)

        // Request a string response from the provided URL
        val stringReq = StringRequest(Request.Method.GET, url, {
            response -> Log.e("lat", response.toString())

            //get the JSON object
            val obj = JSONObject(response)

            //get the Array from obj of name - "data"
            val arr = obj.getJSONArray("data")
            Log.e("lat obj", arr.toString())

            // get the JSON object from the array at index position 0
            val obj1 = arr.getJSONObject(0)
            Log.e("lat obj1", obj1.toString())

            // set the temperature and the city name using getString() function
            textView.text = obj1.getString("temp") + " deg Celsius in " + obj1.getString("city_name")
        },
            // In case of any error
            { textView!!.text = "That didn't work!" })
        queue.add(stringReq)
    }
}
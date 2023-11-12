package com.unreal.climesage

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.unreal.climesage.adapter.ForeCastAdapter
import com.unreal.climesage.mvvm.WeatherVm
import com.unreal.climesage.service.LocationHelper

class ForeCastActivity : AppCompatActivity() {


    private lateinit var adapterForeCastAdapter: ForeCastAdapter
    lateinit var viM: WeatherVm
    lateinit var rvForeCast: RecyclerView
    var longi: String = ""
    var lati: String = ""
    private lateinit var locationHelper: LocationHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fourdayforecast)
        setUpStatusBar(true)
        viM = ViewModelProvider(this).get(WeatherVm::class.java)
        locationHelper = LocationHelper(this)

        adapterForeCastAdapter = ForeCastAdapter()
        rvForeCast = findViewById<RecyclerView>(R.id.rvForeCast)

        val sharedPrefs = SharedPrefs.getInstance(this)
        val city = sharedPrefs.getValueOrNull("city")
        Log.d("Prefs", city.toString())
        if (city != null) {
           viM.getForecastUpcoming(city)

        } else {
            if (locationHelper.isLocationPermissionGranted()) {
                // Permission is granted, request location updates
                requestLocationUpdates()
            } else {
                // Request location permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Utils.LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        viM.forecastWeatherLiveData.observe(this, Observer {
            val setNewlist = it as List<WeatherList>
            Log.d("Forecast LiveData", setNewlist.toString())
            adapterForeCastAdapter.setList(setNewlist)
            rvForeCast.adapter = adapterForeCastAdapter
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestLocationUpdates() {
        locationHelper.requestLocationUpdates { location ->
            // Log latitude and longitude here
            val latitude = location.latitude
            val longitude = location.longitude
            viM.getForecastUpcoming(null, latitude.toString(), longitude.toString())
            //logLocation(latitude, longitude)
        }
    }

    private fun logLocation(latitude: Double, longitude: Double) {
        val message = "Latitude: $latitude, Longitude: $longitude"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates()
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(
                    this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    fun setUpStatusBar(isLight: Boolean) {
        supportActionBar?.hide()
        val decorView = window.decorView
        if (isLight) {
            decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
        window.statusBarColor = Color.TRANSPARENT
    }
}
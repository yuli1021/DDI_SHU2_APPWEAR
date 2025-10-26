package com.example.skycastapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    // Key de OpenWeatherMap
    private val API_KEY = "72e2b05f4a7ac0322774a402ab972944"

    // Permiso de ubicación
    private val LOCATION_PERMISSION_REQUEST = 100

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textViewCity: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var textViewDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // bordes de pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewCity = findViewById(R.id.textViewCity)
        textViewTemperature = findViewById(R.id.textViewTemperature)
        textViewDescription = findViewById(R.id.textViewDescription)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            textViewCity.text = "Permiso de ubicación denegado"
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                getWeather(lat, lon)
            } else {
                textViewCity.text = "Ubicación no disponible"
            }
        }
    }

    private fun getWeather(lat: Double, lon: Double) {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$API_KEY"

        thread {
            try {
                val response = URL(url).readText()
                val jsonObject = JSONObject(response)

                val city = jsonObject.getString("name")
                val temp = jsonObject.getJSONObject("main").getDouble("temp")
                val desc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")

                runOnUiThread {
                    textViewCity.text = city
                    textViewTemperature.text = "${temp.toInt()}°C"
                    textViewDescription.text = desc.replaceFirstChar { it.uppercase() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    textViewCity.text = "Error al cargar clima"
                    textViewTemperature.text = "--°C"
                    textViewDescription.text = "--"
                }
            }
        }
    }
}

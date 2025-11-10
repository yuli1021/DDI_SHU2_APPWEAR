package com.example.skycastapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val API_KEY = "1c8f58430ab05ca9d1774a25d24030e9"
    private val LOCATION_PERMISSION_REQUEST = 100

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var textViewDateTime: TextView
    private lateinit var textViewCity: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var textViewMaxTemp: TextView
    private lateinit var textViewMinTemp: TextView
    private lateinit var textViewTempSymbol: TextView
    private lateinit var recyclerViewHourlyForecast: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // **FIX: Initialize all views here, after setContentView()**
        mainLayout = findViewById(R.id.main)
        textViewDateTime = findViewById(R.id.textViewDateTime)
        textViewCity = findViewById(R.id.textViewCity)
        textViewTemperature = findViewById(R.id.textViewTemperature)
        textViewDescription = findViewById(R.id.textViewDescription)
        textViewMaxTemp = findViewById(R.id.textViewMaxTemp)
        textViewMinTemp = findViewById(R.id.textViewMinTemp)
        textViewTempSymbol = findViewById(R.id.textViewTempSymbol)
        recyclerViewHourlyForecast = findViewById(R.id.recyclerViewHourlyForecast)

        recyclerViewHourlyForecast.layoutManager = LinearLayoutManager(this)

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
                getWeather(location.latitude, location.longitude)
                getHourlyForecast(location.latitude, location.longitude)
            } else {
                textViewCity.text = "Ubicación no disponible"
            }
        }
    }

    private fun getWeather(lat: Double, lon: Double) {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&lang=es&appid=$API_KEY"
        thread {
            try {
                val response = URL(url).readText()
                val json = JSONObject(response)

                val city = json.getString("name")
                val main = json.getJSONObject("main")
                val temp = main.getInt("temp")
                val tempMax = main.getInt("temp_max")
                val tempMin = main.getInt("temp_min")
                val weather = json.getJSONArray("weather").getJSONObject(0)
                val description = weather.getString("description").replaceFirstChar { it.uppercase() }
                val dt = json.getLong("dt")
                val sys = json.getJSONObject("sys")
                val sunrise = sys.getLong("sunrise")
                val sunset = sys.getLong("sunset")

                val isDay = dt in sunrise until sunset
                val dateTimeString = SimpleDateFormat("hh:mm a, EEE MMM d", Locale.getDefault()).format(Date(dt * 1000))

                runOnUiThread {
                    updateTheme(isDay)
                    textViewDateTime.text = dateTimeString
                    textViewCity.text = city
                    textViewTemperature.text = temp.toString()
                    textViewDescription.text = description
                    textViewMaxTemp.text = "$tempMax°C"
                    textViewMinTemp.text = "$tempMin°C"

                    sendWeatherDataToWear(city, "$temp°C", description)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    textViewCity.text = "Error al cargar clima"
                }
            }
        }
    }

    private fun getHourlyForecast(lat: Double, lon: Double) {
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=metric&lang=es&appid=$API_KEY"
        thread {
            try {
                val response = URL(url).readText()
                val json = JSONObject(response)
                val list = json.getJSONArray("list")
                val hourlyForecasts = mutableListOf<HourlyForecast>()
                val city = json.getJSONObject("city")
                val sunrise = city.getLong("sunrise")
                val sunset = city.getLong("sunset")

                for (i in 0 until list.length()) {
                    val item = list.getJSONObject(i)
                    val dt = item.getLong("dt")
                    val isDay = dt in sunrise until sunset
                    val hour = SimpleDateFormat("h a", Locale.getDefault()).format(Date(dt * 1000))
                    val temp = item.getJSONObject("main").getInt("temp")
                    val weather = item.getJSONArray("weather").getJSONObject(0)
                    val description = weather.getString("description")
                    val icon = getWeatherIcon(description, isDay)
                    hourlyForecasts.add(HourlyForecast(hour, icon, "$temp°"))
                }

                runOnUiThread {
                    recyclerViewHourlyForecast.adapter = HourlyForecastAdapter(hourlyForecasts)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getWeatherIcon(description: String, isDay: Boolean): Int {
        return when {
            description.contains("sol", ignoreCase = true) -> if (isDay) R.drawable.ic_sun else R.drawable.ic_moon
            description.contains("nube", ignoreCase = true) -> R.drawable.ic_cloud
            description.contains("lluvia", ignoreCase = true) -> R.drawable.ic_rain
            else -> if (isDay) R.drawable.ic_sun else R.drawable.ic_moon
        }
    }

    private fun updateTheme(isDay: Boolean) {
        val backgroundRes = if (isDay) R.drawable.background_gradient_day else R.drawable.background_gradient_night
        val textColor = if (isDay) Color.BLACK else Color.WHITE

        mainLayout.background = ContextCompat.getDrawable(this, backgroundRes)
        textViewDateTime.setTextColor(textColor)
        textViewCity.setTextColor(textColor)
        textViewTemperature.setTextColor(textColor)
        textViewDescription.setTextColor(textColor)
        textViewMaxTemp.setTextColor(textColor)
        textViewMinTemp.setTextColor(textColor)
        textViewTempSymbol.setTextColor(textColor)

        val arrowUp = ContextCompat.getDrawable(this, if (isDay) R.drawable.ic_arrow_upward else R.drawable.ic_arrow_upward_white)
        val arrowDown = ContextCompat.getDrawable(this, if (isDay) R.drawable.ic_arrow_downward else R.drawable.ic_arrow_downward_white)
        arrowUp?.setTint(textColor)
        arrowDown?.setTint(textColor)
        textViewMaxTemp.setCompoundDrawablesWithIntrinsicBounds(arrowUp, null, null, null)
        textViewMinTemp.setCompoundDrawablesWithIntrinsicBounds(arrowDown, null, null, null)
    }

    private fun sendWeatherDataToWear(city: String, temp: String, description: String) {
        val putDataMapReq = PutDataMapRequest.create(WEATHER_DATA_PATH).apply {
            dataMap.putString(KEY_CITY, city)
            dataMap.putString(KEY_TEMP, temp)
            dataMap.putString(KEY_DESC, description)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }
        val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()
        Wearable.getDataClient(this).putDataItem(putDataReq).apply {
            addOnSuccessListener { Log.d(TAG, "Datos del clima enviados al watch: $it") }
            addOnFailureListener { e -> Log.e(TAG, "Error al enviar datos del clima", e) }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        const val WEATHER_DATA_PATH = "/weather-data"
        const val KEY_CITY = "city"
        const val KEY_TEMP = "temperature"
        const val KEY_DESC = "description"
    }
}

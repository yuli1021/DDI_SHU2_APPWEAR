package com.example.skycastapp.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.skycastapp.data.WeatherDataListenerService
import com.example.skycastapp.presentation.theme.SkyCastAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var weatherUpdateReceiver: BroadcastReceiver
    private val cityState = mutableStateOf("Cargando...")
    private val tempState = mutableStateOf("--°C")
    private val descState = mutableStateOf("--")

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        weatherUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                cityState.value = intent.getStringExtra(WeatherDataListenerService.EXTRA_CITY) ?: "N/A"
                tempState.value = intent.getStringExtra(WeatherDataListenerService.EXTRA_TEMP) ?: "--°C"
                descState.value = intent.getStringExtra(WeatherDataListenerService.EXTRA_DESC) ?: "--"
            }
        }

        setContent {
            WearApp(
                city = cityState.value,
                temp = tempState.value,
                desc = descState.value
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(WeatherDataListenerService.ACTION_WEATHER_UPDATE)
        registerReceiver(weatherUpdateReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(weatherUpdateReceiver)
    }
}

@Composable
fun WearApp(city: String, temp: String, desc: String) {
    SkyCastAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = city,
                style = MaterialTheme.typography.title1
            )
            Text(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = temp,
                style = MaterialTheme.typography.display1
            )
            Text(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary,
                text = desc,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Los Angeles", "24°C", "Soleado")
}

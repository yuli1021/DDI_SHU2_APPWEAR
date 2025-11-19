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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.skycastapp.data.WeatherDataListenerService
import com.example.skycastapp.presentation.theme.SkyCastAppTheme

class MainActivity : ComponentActivity() {

    private var cityState by mutableStateOf("Waiting for data...")
    private var tempState by mutableStateOf("--°C")
    private var descState by mutableStateOf("--")

    private lateinit var weatherUpdateReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                cityState = intent.getStringExtra(WeatherDataListenerService.EXTRA_CITY) ?: "N.A"
                tempState = intent.getStringExtra(WeatherDataListenerService.EXTRA_TEMP) ?: "--°C"
                descState = intent.getStringExtra(WeatherDataListenerService.EXTRA_DESC) ?: "--"
            }
        }

        setContent {
            WearApp(
                city = cityState,
                temperature = tempState,
                description = descState
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(WeatherDataListenerService.ACTION_WEATHER_UPDATE)
        ContextCompat.registerReceiver(this, weatherUpdateReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(weatherUpdateReceiver)
    }
}

@Composable
fun WearApp(city: String, temperature: String, description: String) {
    SkyCastAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.title1,
                color = androidx.compose.ui.graphics.Color.White
            )

            val tempColor = when {
                temperature.contains("-") -> androidx.compose.ui.graphics.Color.Cyan
                (temperature.removeSuffix("°C").toIntOrNull() ?: 0) >= 28 -> androidx.compose.ui.graphics.Color.Red
                else -> androidx.compose.ui.graphics.Color.White
            }

            Text(
                text = temperature,
                style = MaterialTheme.typography.display1,
                color = tempColor
            )
            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                color = androidx.compose.ui.graphics.Color(0xFF054FF7)
            )
        }
    }
}


@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(city = "Ramos Arizpe", temperature = "30°C", description = "" +
            "Soleado")
}

package com.example.skycastapp.data

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class WeatherDataListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.d(TAG, "Nuevos datos recibidos en el reloj.")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == WEAR_PATH) {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val city = dataMap.getString(KEY_CITY, "N.A")
                    val temp = dataMap.getString(KEY_TEMP, "--°C")
                    val desc = dataMap.getString(KEY_DESC, "--")
                    Log.d(TAG, "Datos extraídos: Ciudad=$city, Temp=$temp, Desc=$desc")

                    Intent().also { intent ->
                        intent.action = ACTION_WEATHER_UPDATE
                        intent.putExtra(EXTRA_CITY, city)
                        intent.putExtra(EXTRA_TEMP, temp)
                        intent.putExtra(EXTRA_DESC, desc)
                        sendBroadcast(intent)
                        Log.d(TAG, "Broadcast enviado a MainActivity.")
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "WeatherDataListener"
        const val ACTION_WEATHER_UPDATE = "com.example.skycastapp.ACTION_WEATHER_UPDATE"
        const val WEAR_PATH = "/weather-data"

        // Claves para el Data Layer (deben coincidir con las del móvil)
        const val KEY_CITY = "city"
        const val KEY_TEMP = "temperature"
        const val KEY_DESC = "description"

        // Claves para el Broadcast a la MainActivity del reloj
        const val EXTRA_CITY = "EXTRA_CITY"
        const val EXTRA_TEMP = "EXTRA_TEMP"
        const val EXTRA_DESC = "EXTRA_DESC"
    }
}

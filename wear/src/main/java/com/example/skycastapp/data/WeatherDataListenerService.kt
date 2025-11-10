package com.example.skycastapp.data

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class WeatherDataListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: $dataEvents")
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == WEATHER_DATA_PATH) {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val city = dataMap.getString(KEY_CITY)
                    val temp = dataMap.getString(KEY_TEMP)
                    val desc = dataMap.getString(KEY_DESC)

                    Log.d(TAG, "Datos del clima recibidos: Ciudad: $city, Temp: $temp, Desc: $desc")

                    // Enviar un broadcast con los datos del clima
                    val intent = Intent(ACTION_WEATHER_UPDATE)
                    intent.putExtra(EXTRA_CITY, city)
                    intent.putExtra(EXTRA_TEMP, temp)
                    intent.putExtra(EXTRA_DESC, desc)
                    sendBroadcast(intent)
                }
            }
        }
    }

    companion object {
        private const val TAG = "WeatherDataListener"

        // Mismas constantes que en la app móvil
        private const val WEATHER_DATA_PATH = "/weather-data"
        private const val KEY_CITY = "city"
        private const val KEY_TEMP = "temperature"
        private const val KEY_DESC = "description"

        // Intent action y extras para el broadcast
        const val ACTION_WEATHER_UPDATE = "com.example.skycastapp.WEATHER_UPDATE"
        const val EXTRA_CITY = "extra_city"
        const val EXTRA_TEMP = "extra_temp"
        const val EXTRA_DESC = "extra_desc"
    }
}
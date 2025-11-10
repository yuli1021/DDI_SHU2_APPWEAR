package com.example.skycastapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class HourlyForecast(
    val time: String,
    val icon: Int,
    val temperature: String
)

class HourlyForecastAdapter(private val hourlyForecasts: List<HourlyForecast>) : RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewHour: TextView = view.findViewById(R.id.textViewHour)
        val imageViewWeatherIcon: ImageView = view.findViewById(R.id.imageViewWeatherIcon)
        val textViewHourTemp: TextView = view.findViewById(R.id.textViewHourTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecast = hourlyForecasts[position]
        holder.textViewHour.text = forecast.time
        holder.imageViewWeatherIcon.setImageResource(forecast.icon)
        holder.textViewHourTemp.text = forecast.temperature
    }

    override fun getItemCount() = hourlyForecasts.size
}
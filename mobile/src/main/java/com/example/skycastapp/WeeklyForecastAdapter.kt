package com.example.skycastapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeeklyForecastAdapter(private val forecasts: List<WeeklyForecast>) : RecyclerView.Adapter<WeeklyForecastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTextView: TextView = view.findViewById(R.id.textViewDayOfWeek)
        val iconImageView: ImageView = view.findViewById(R.id.imageViewWeatherIcon)
        val maxTempTextView: TextView = view.findViewById(R.id.textViewWeeklyMaxTemp)
        val minTempTextView: TextView = view.findViewById(R.id.textViewWeeklyMinTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weekly_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecast = forecasts[position]
        holder.dayTextView.text = forecast.day
        holder.iconImageView.setImageResource(forecast.icon)
        holder.maxTempTextView.text = forecast.maxTemp
        holder.minTempTextView.text = forecast.minTemp
    }

    override fun getItemCount() = forecasts.size
}
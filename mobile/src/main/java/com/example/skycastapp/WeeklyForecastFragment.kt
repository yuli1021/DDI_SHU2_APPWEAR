package com.example.skycastapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class WeeklyForecast(
    val day: String,
    val icon: Int,
    val maxTemp: String,
    val minTemp: String
)

class WeeklyForecastFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weekly_forecast, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewWeeklyForecast)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    fun updateForecast(forecasts: List<WeeklyForecast>) {
        recyclerView.adapter = WeeklyForecastAdapter(forecasts)
    }
}
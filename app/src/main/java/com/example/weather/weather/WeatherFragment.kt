package com.example.weather.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weather.App
import com.example.weather.R
import com.example.weather.openweathermap.WeatherWrapper
import com.example.weather.openweathermap.mapOpenWeatherDataToWeather
import com.example.weather.utils.toast
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherFragment : Fragment() {

    companion object {
        val EXTRA_CITY_NAME = "com.example.weather.extras.EXTRA_CITY_NAME"
        fun newInstance(): WeatherFragment {
            Log.i("WeatherFragment", "new instance call")
            return WeatherFragment()
        }
    }

    private val TAG = WeatherWrapper::class.java.simpleName
    private lateinit var cityName: String

    private lateinit var city: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var weatherDescription: TextView
    private lateinit var temperature: TextView
    private lateinit var humidity: TextView
    private lateinit var pressure: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        city = view.findViewById(R.id.city)
        weatherIcon = view.findViewById(R.id.weather_icon)
        weatherDescription = view.findViewById(R.id.weather_description)
        temperature = view.findViewById(R.id.temperature)
        humidity = view.findViewById(R.id.humidity)
        pressure = view.findViewById(R.id.pressure)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated()")
        if (activity?.intent!!.hasExtra(EXTRA_CITY_NAME)) {
            Log.i(TAG, "starting request 2")
            updateWeatherForCity(requireActivity().intent.getStringExtra(EXTRA_CITY_NAME)!!)
        }
    }


    private fun updateWeatherForCity(cityName: String) {
        this.cityName = cityName

        val call = App.WeatherService.getWeather("$cityName,fr")
        call.enqueue(object : Callback<WeatherWrapper> {
            override fun onResponse(
                call: Call<WeatherWrapper>,
                response: Response<WeatherWrapper>
            ) {
                response?.body()?.let {
                    val weather = mapOpenWeatherDataToWeather(it)
                    updateUi(weather)
                    Log.i(TAG, "Weather Response: $weather")
                }
            }

            override fun onFailure(call: Call<WeatherWrapper>, t: Throwable) {
                Log.e(TAG, "Could not load city weather", t)
                context?.toast("Error while trying to retrieve weather for $cityName")
            }

        })
    }

    private fun updateUi(weather: Weather) {
        Picasso.get().load(weather.iconUrl).placeholder(R.drawable.ic_launcher_foreground)
            .into(weatherIcon)
        weatherDescription.text = weather.description
        temperature.text = "${weather.temperature.toInt()} °C"
        humidity.text = "${weather.humidity} %"
        pressure.text = "${weather.pressure} hPa"

    }
}

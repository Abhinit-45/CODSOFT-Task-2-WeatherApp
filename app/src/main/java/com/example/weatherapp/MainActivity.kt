package com.example.weatherapp
//(my api)= 1baae855cafdad2c1f458d69b60d8312
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("bhopal")
        SearchCity()

    }

    private fun SearchCity() {
        val searchView = binding.searchBar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }


        })

    }

    private fun fetchWeatherData(cityname: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityname, "1baae855cafdad2c1f458d69b60d8312", "metric")
        response.enqueue(object : Callback<weatherApp> {
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    //for showing response
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val weathercondition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    binding.temperature.text = "$temperature °C"
                    binding.weatherCondition.text = weathercondition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed hPa"
                    binding.sunrise.text = "${timeformat(sunrise)}"
                    binding.sunset.text = "${timeformat(sunset)}"
                    binding.condition.text = weathercondition
                    binding.sea.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.locationName.text = " $cityname"
                    //hi

                    changeImgAccordingWeatherCondition(weathercondition)
                    //Log.d("Tag", "onResponse: $temperature")
                }
            }

            private fun changeImgAccordingWeatherCondition(conditions:String) {
when(conditions){
    "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
        binding.root.setBackgroundResource(R.drawable.colud_background)
        binding.lottieAnimation.setAnimation(R.raw.cloud)
    }
    "Clear Sky","Sunny","Clear"->{
        binding.root.setBackgroundResource(R.drawable.sunny_background)
        binding.lottieAnimation.setAnimation(R.raw.sun)
    }
    "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
        binding.root.setBackgroundResource(R.drawable.rain_background)
        binding.lottieAnimation.setAnimation(R.raw.rain)
    }
    "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
        binding.root.setBackgroundResource(R.drawable.snow_background)
        binding.lottieAnimation.setAnimation(R.raw.snow)
    }
    else->{
        binding.root.setBackgroundResource(R.drawable.sunny_background)
        binding.lottieAnimation.setAnimation(R.raw.sun)

    }
}
                binding.lottieAnimation.playAnimation()
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                TODO("Failure")
            }

        })
    }


    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }

    fun date(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun timeformat(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
}
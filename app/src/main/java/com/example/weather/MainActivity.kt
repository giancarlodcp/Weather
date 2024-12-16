package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        val repository = WeatherRepository(weatherService)
        val viewModel = WeatherViewModel(repository)

        setContent {
            MaterialTheme {
                WeatherView(viewModel = viewModel)
            }
        }
    }
}
@Composable
fun WeatherView(viewModel: WeatherViewModel) {
    val state by viewModel.state.collectAsState()
    var city by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            placeholder = { Text(text = "Ingresa ciudad")},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.fetchWeather(city.text) },
            modifier = Modifier.padding(10.dp)) {
            Text("Consultar Clima")
            PaddingValues(10.dp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (state) {
            is WeatherState.Loading -> CircularProgressIndicator()
            is WeatherState.Success -> {
                val weather = (state as WeatherState.Success).weather
                Text("Temperatura: ${weather.main.temp}°C")
                Text("Descripcion: ${weather.weather[0].description}")
                Text("Vientos: ${weather.wind.speed} m/s")
                Image(
                    painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${weather.weather[0].icon}@2x.png"),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
            is WeatherState.Error -> Text((state as WeatherState.Error).message)
                //Aqui se puede manejar los errores. Hay diversos, incluso si no coloco datos.
        }
    }
}
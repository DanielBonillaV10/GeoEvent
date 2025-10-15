package jdc.ejercicios.prueba_proyectofinal;

import android.content.Context;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherService {

    public static void getWeather(Context context, String city, TextView output) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=TU_API_KEY&units=metric";

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temp = main.getDouble("temp");
                        output.setText("Temperatura actual: " + temp + "°C");
                    } catch (JSONException e) {
                        output.setText("Error al procesar clima");
                    }
                },
                error -> output.setText("Error al conectar al servicio"));

        queue.add(jsonRequest);
    }
}

package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit extends AppCompatActivity {
    interface RequestData {
        @GET("v1/forecast")
        Call<WeatherResponse> getWeatherData(
                @Query("latitude") double latitude,
                @Query("longitude") double longitude,
                @Query("daily") String daily,
                @Query("current_weather") boolean currentWeather,
                @Query("timezone") String timezone
        );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        TextView txtcoordinat = findViewById(R.id.coordinat);
        TextView txtDate = findViewById(R.id.date);
        TextView txtTemperature = findViewById(R.id.temperature);
        TextView txtCondition = findViewById(R.id.condition);
        TextView txtWind = findViewById(R.id.wind);
        ImageView imgCondition = findViewById(R.id.imgCondition);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestData requestUser = retrofit.create(RequestData.class);

        requestUser.getWeatherData(-7.98, 112.63, "weathercode", true, "auto")
                .enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                WeatherResponse weatherData = response.body();
                txtcoordinat.setText(String.valueOf(weatherData.getLatitude() + ", " + weatherData.getLongitude()));

                WeatherResponse.CurrentWeather currentWeather = weatherData.getCurrentWeather();

                int days = currentWeather.getIs_day();
                String day_name = getDayName(days);
                String date = currentWeather.getTime();
                txtDate.setText(String.valueOf(date));
                String[] dateSplit = date.split("-", 3);
                String day = dateSplit[2].substring(0,2);
                String month = getMonthName(Integer.parseInt(dateSplit[1]));
                String year = dateSplit[0];
                txtDate.setText(day_name + ", " + day + " " + month + " " + year);

                Float temperature = currentWeather.getTemperature();
                txtTemperature.setText(String.valueOf(temperature + "°"));

                int is_day = currentWeather.getIs_day();
                String condition = Integer.toString(is_day);
                txtCondition.setText(DetailCondition(condition));

                imgCondition.setImageDrawable(setImage(condition));

                float wind = currentWeather.getWindspeed();
                txtWind.setText(String.valueOf(wind + "m/s"));

                WeatherResponse.DailyData dailyData = weatherData.getDailyData();
                String[] timeArray = dailyData.getTime();

                int[] weatherCodes = dailyData.getWeatherCode();
                ShowDataDaily(timeArray, weatherCodes);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(Retrofit.this, "API call failed: " +
                       t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowDataDaily(String[] timeArray, int[] weatherCodes) {
        String[] items_daily =  new String[7];
        for(int i=0; i < items_daily.length ; i++) {
            String[] timeArraySplit = timeArray[i].split("-", 3);
            String month_daily = getMonthName(Integer.parseInt(timeArraySplit[1]));
            items_daily[i] = timeArraySplit[2] + " " + month_daily.substring(0,3);
        }
        String[] items_weathercode =  new String[7];
        for(int i=0; i < items_weathercode.length ; i++) {
            int iterate = weatherCodes[i];
            String condition = Integer.toString(iterate);
            items_weathercode[i] = DetailCondition(condition);
        }

        Drawable[] items_images = new Drawable[7];
        for(int i=0; i < items_images.length ; i++) {
            String iterate = String.valueOf(weatherCodes[i]);
            items_images[i] = setImage(iterate);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view, items_daily);
        ListView listView1 = (ListView) findViewById(R.id.daily);
        listView1.setAdapter(adapter);

        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.list_view, items_weathercode);
        ListView listView2 = (ListView) findViewById(R.id.dailyweather);
        listView2.setAdapter(adapter2);

        ListView listView3 = (ListView) findViewById(R.id.imgWeather);
        ArrayAdapter<Drawable> adapter3 = new ArrayAdapter<Drawable>(this, R.layout.img_list, items_images) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.img_list, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.imgWeather);
                imageView.setImageDrawable(getItem(position));
                return convertView;
            }
        };
        listView3.setAdapter(adapter3);
    }

    private Drawable setImage(String cond) {
        Drawable drawable;
        switch (cond){
            case "0":
                drawable = getResources().getDrawable(R.drawable.ic_sunny);
                break;
            case "1":
                drawable =  getResources().getDrawable(R.drawable.ic_cloudy_day);
                break;
            case "2":
                drawable =  getResources().getDrawable(R.drawable.ic_cloudy_day);
                break;
            case "3":
                drawable =  getResources().getDrawable(R.drawable.ic_cloudy_day2);
                break;
            case "45":
                drawable =  getResources().getDrawable(R.drawable.ic_clouds);
                break;
            case "48":
                drawable =  getResources().getDrawable(R.drawable.ic_fog2);
                break;
            case "51":
                drawable =  getResources().getDrawable(R.drawable.ic_rain);
                break;
            case "53":
                drawable =  getResources().getDrawable(R.drawable.ic_rain);
                break;
            case "55":
                drawable =  getResources().getDrawable(R.drawable.ic_rain);
                break;
            case "61":
                drawable =  getResources().getDrawable(R.drawable.ic_heavy_rain);
                break;
            case "63":
                drawable =  getResources().getDrawable(R.drawable.ic_heavy_rain);
                break;
            case "65":
                drawable =  getResources().getDrawable(R.drawable.ic_heavy_rain);
                break;
            case "80":
                drawable =  getResources().getDrawable(R.drawable.ic_thunderstorm);
                break;
            case "81":
                drawable =  getResources().getDrawable(R.drawable.ic_thunderstorm);
                break;
            case "82":
                drawable =  getResources().getDrawable(R.drawable.ic_thunderstorm);
                break;
            default:
                drawable = getResources().getDrawable(R.drawable.ic_sunny);
                break;
        }
        return drawable;
    }
    private String DetailCondition(String cond) {
        switch (cond){
            case "0":
                cond = "Clear Sky";
                break;
            case "1":
                cond = "Mainly Clear";
                break;
            case "2":
                cond = "Partly Cloudy";
                break;
            case "3":
                cond = "Overcast";
                break;
            case "45":
                cond = "Fog";
                break;
            case "48":
                cond = "Dense Fog";
                break;
            case "51":
                cond = "Light Drizzle";
                break;
            case "53":
                cond = "Moderate Drizzle";
                break;
            case "55":
                cond = "Dense Drizzle";
                break;
            case "61":
                cond = "Slight Rain";
                break;
            case "63":
                cond = "Moderate Rain";
                break;
            case "65":
                cond = "Heavy Rain";
                break;
            case "80":
                cond = "Slight Rain Showers";
                break;
            case "81":
                cond = "Moderate Rain Showers";
                break;
            case "82":
                cond = "Violent Rain Showers";
                break;
            default:
                cond = "Sunny Day";
                break;
        }
        return cond;
    }

    public static String getMonthName(int number) {
        String[] monthNames = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };
        return monthNames[number - 1];
    }

    public static String getDayName(int number) {
        String[] dayNames = {
                "Sunday", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday"
        };
        return dayNames[number];
    }

}
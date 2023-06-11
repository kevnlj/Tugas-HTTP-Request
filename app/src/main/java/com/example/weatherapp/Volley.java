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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Volley extends AppCompatActivity {
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        mQueue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        sendAndRequestResponse();
    }

    private void sendAndRequestResponse() {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=-7.98&longitude=112.63&daily=weathercode&current_weather=true&timezone=auto";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("current_weather");
                    double latitude = response.getDouble("latitude");
                    double longitude = response.getDouble("longitude");
                    String time = jsonObject.getString("time");
                    double temperature = jsonObject.getDouble("temperature");
                    int condition = jsonObject.getInt("is_day");
                    String cond =Integer.toString(condition);
                    double windspeed = jsonObject.getDouble("windspeed");

                    JSONObject jsonObject_daily = response.getJSONObject("daily");
                    JSONArray daily = jsonObject_daily.getJSONArray("time");
                    JSONArray weatherCode = jsonObject_daily.getJSONArray("weathercode");

                    //set coordinate
                    TextView coordinat = findViewById(R.id.coordinat);
                    coordinat.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));

                    // set date
                    int days = jsonObject.getInt("is_day");
                    String day_name = getDayName(days);
                    String[] dateSplit = time.split("-", 3);
                    String day = dateSplit[2].substring(0,2);
                    String month = getMonthName(Integer.parseInt(dateSplit[1]));
                    String year = dateSplit[0];
                    TextView date = findViewById(R.id.date);
                    date.setText(day_name + ", " + day + " " + month + " " + year);

                    //set temperaure
                    TextView txttemperature = findViewById(R.id.temperature);
                    txttemperature.setText(String.valueOf(temperature) + "°");

                    //setImage
                    ImageView imgcondition = findViewById(R.id.imgCondition);
                    imgcondition.setImageDrawable(setImage(cond));

                    //set conditions this day
                    TextView txtCondition = findViewById(R.id.condition);
                    txtCondition.setText(DetailCondition(cond));

                    //set wind this day
                    TextView txtWindSpeed = findViewById(R.id.wind);
                    txtWindSpeed.setText(String.valueOf(windspeed) + "m/s");

                    ShowDataDaily(daily, weatherCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Volley.this, "API call failed: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);
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

    private void ShowDataDaily(JSONArray daily, JSONArray weathercode) throws JSONException {
        String[] items_daily =  new String[7];
        for(int i=0; i < items_daily.length ; i++) {
            String iterate = daily.getString(i);
            items_daily[i] = iterate;
            String[] timeArraySplit = items_daily[i].split("-", 3);
            String month_daily = getMonthName(Integer.parseInt(timeArraySplit[1]));
            items_daily[i] = timeArraySplit[2] + " " + month_daily.substring(0,3);
        }
        String[] items_weathercode =  new String[7];
        for(int i=0; i < items_daily.length ; i++) {
            String iterate = weathercode.getString(i);
            items_weathercode[i] = DetailCondition(iterate);
        }

        Drawable[] items_images = new Drawable[7];
        for(int i=0; i < items_images.length ; i++) {
            String iterate = weathercode.getString(i);
            items_images[i] = setImage(iterate);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view, items_daily);
        ListView listView1 = (ListView) findViewById(R.id.daily);
        listView1.setAdapter(adapter);

        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.list_view, items_weathercode);
        ListView listView2 = (ListView) findViewById(R.id.dailyweather);
        listView2.setAdapter(adapter2);

        //ArrayAdapter adapter3 = new ArrayAdapter<Drawable>(this, R.layout.img_list, items_images);
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
package com.example.weatherassignment.ui.weather;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherassignment.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WeatherFragment extends Fragment {
    //FrontEnd
    TextView mainTextView;
    TextView temperatureTextView;
    TextView highLowTempTextView;
    View view;

    private WeatherViewModel mViewModel;

    //Backend stuff
    JSONObject jsonPart;
    JSONArray arr;
    LatLng newYork;

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    public class weatherInfoDownload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            Log.d("TAG", "doInBackground: ");
            StringBuilder result = new StringBuilder();
            URL url;
            HttpsURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }

            }catch(Exception e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                String result = null,  tempLowHigh = null;
                String main, temp, tempMax, tempMin;

                JSONObject jsonObject = new JSONObject(s);

                //get the weather from json
                String weatherInfo = jsonObject.getString("weather");
                arr = new JSONArray(weatherInfo);
                jsonPart = arr.getJSONObject(0);
                main = jsonPart.getString("main");

                //get the main from json
                jsonPart = jsonObject.getJSONObject("main");
                temp = jsonPart.getString("temp");
                tempMin = jsonPart.getString("temp_min");
                tempMax = jsonPart.getString("temp_max");

                //result of the above data
                result = main + temp + tempMin + tempMax;
                tempLowHigh = "H " + tempMin + "  L " + tempMax;
                temp+="°";

                if(result != null && tempLowHigh != null) {
                    mainTextView.setText(main);
                    temperatureTextView.setText(temp);
                    highLowTempTextView.setText(tempLowHigh);
                }else{
                    Toast.makeText(getContext(), "Could not find weather ", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

//                Toast.makeText(getApplicationContext(), "Could not find weather ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getWeather(View view){
        try {
            weatherInfoDownload downloadData = new weatherInfoDownload();

            newYork = new LatLng(40.6976637, -74.1197635);

            downloadData.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + newYork.latitude + "&lon=" + newYork.longitude + "&appid=050b5612a3e8d2d64a82eb1c1cf6b59f&units=metric");  // this is the complete api

//            Log.d("TAG", "https://api.openweathermap.org/data/2.5/weather?lat=" + newYork.latitude + "&lon=" + newYork.longitude + "&appid=050b5612a3e8d2d64a82eb1c1cf6b59f"); //done

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Could not find weather ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.weather_fragment, container, false);

        getWeather(view);

        mainTextView = (TextView) view.findViewById(R.id.mainTextView);
        temperatureTextView = (TextView) view.findViewById(R.id.temperatureTextView);
        highLowTempTextView = (TextView) view.findViewById(R.id.highLowTempTextView);

        return view;
    }

}
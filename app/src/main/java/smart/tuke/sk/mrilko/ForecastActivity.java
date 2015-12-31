package smart.tuke.sk.mrilko;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ForecastActivity extends AppCompatActivity {

    private static final String TAG = "ForecastActivity";
    private String  city;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // get extras from intent
        Intent intent = getIntent();
        try {
            // read json objects
            JSONObject json = new JSONObject(intent.getStringExtra("json"));
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            JSONObject sys = json.getJSONObject("sys");
            JSONObject coord = json.getJSONObject("coord");

            JSONArray weather = json.getJSONArray("weather");
            JSONObject weatherDesc = weather.getJSONObject(0);

            // extract coordination
            this.latitude = coord.getDouble("lat");
            this.longitude = coord.getDouble("lon");

            // set location
            ((TextView) findViewById(R.id.location))
                    .setText(String.format("%s, %s", json.getString("name"), sys.getString("country")));

            // set temperature
            ((TextView) findViewById(R.id.temp))
                    .setText(main.getString("temp"));

            ((TextView) findViewById(R.id.tempUnit))
                    .setText("°C");

            // set weather description
            ((TextView) findViewById(R.id.descrWeather))
                    .setText(weatherDesc.getString("description"));

            // set temperature min
            ((TextView) findViewById(R.id.tempMin))
                    .setText(main.getString("temp_min") + "°C");

            // set temperature max
            ((TextView) findViewById(R.id.tempMax))
                    .setText(main.getString("temp_max") + "°C");

            // set wind
            ((TextView) findViewById(R.id.windSpeed))
                    .setText(wind.getString("speed") + " m/s");

            if(wind.has("deg")) {
                ((TextView) findViewById(R.id.windDeg))
                        .setText(wind.getString("deg") + "°");
            }

            // set humidity
            ((TextView) findViewById(R.id.humidity))
                    .setText(main.getString("humidity") + "%");

            // set pressure
            ((TextView) findViewById(R.id.pressure))
                    .setText(main.getString("pressure") + "hPa");

            // set visibility
            ((TextView) findViewById(R.id.visibility))
                    .setText(json.getString("visibility") + "m");


            // set sunrise and sunset
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            Date sunrise = new java.util.Date(sys.getLong("sunrise") *1000);
            Date sunset = new java.util.Date(sys.getLong("sunset") *1000);

            ((TextView) findViewById(R.id.sunrise)).setText(sdf.format(sunrise));
            ((TextView) findViewById(R.id.sunset)).setText(sdf.format(sunset));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void openProvider(View view){
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://openweathermap.org/find?q=" + this.city));
        startActivity(intent);
    }


    public void openLocation(View view){
        Log.i(TAG, "location clicked");
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(String.format("geo:%f,%f?z=10", this.latitude, this.longitude)));

        try {
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "Geolocation viewer is not installed.", Toast.LENGTH_SHORT).show();
        }
    }
}






package smart.tuke.sk.mrilko;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void search(View view){
        String city = ((EditText)findViewById(R.id.input)).getText().toString();

        RetrieveForecastTask task = new RetrieveForecastTask();
        task.execute(city);
    }
    public class RetrieveForecastTask extends AsyncTask<String, Void, JSONObject> {
        private static final String APPID = "3718d7f90e7b081ca8f46aa4305c05ea";
        private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric&q=%s&APPID=%s";
        private static final String TAG = "RetrieveForecastTask";
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.progress = new ProgressDialog(SearchActivity.this);
            this.progress.setMessage("Searching...");
            this.progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... cities) {
            for (String city : cities) {
                try {
                    URL url = new URL(String.format(WEATHER_URL, city, APPID));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    Log.i(TAG, String.format("Connecting to %s", url.toString()));
                    Log.i(TAG, String.format("HTTP Status Code: %d", connection.getResponseCode()));

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + '\n');
                    }

                    Log.i(TAG, String.format("GET: %s", stringBuilder.toString()));

                    return new JSONObject(stringBuilder.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            this.progress.dismiss();

            if (json == null) {
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                return;
            }

            // change activity
            Intent intent = new Intent(getApplicationContext(), ForecastActivity.class);
            intent.putExtra("json", json.toString());
            startActivity(intent);
        }
    }
}
package mg.studio.weatherappdesign;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadUpdate().execute();
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
        Toast.makeText(MainActivity.this, "The App has been updated", Toast.LENGTH_SHORT).show();
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://t.weather.sojson.com/api/weather/city/101040100";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            //Update the temperature displayed
            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
            //base message
            String date = jsonObject.get("date").getAsString();
            String year = date.substring(0,4);
            String month = date.substring(4,6);
            String day = date.substring(6,8);
            String city = jsonObject.getAsJsonObject("cityInfo").get("city").getAsString();
            String temperature = jsonObject.getAsJsonObject("data").get("wendu").getAsString();
            //get the future temperature data
            String[] week = new String[5];
            String[] temp = new String[5];
            for (int i = 0;i < 5;i++){
                String week_forecast = jsonObject.getAsJsonObject("data").getAsJsonArray("forecast").get(i).getAsJsonObject().get("week").getAsString();
                String temp_forecast = jsonObject.getAsJsonObject("data").getAsJsonArray("forecast").get(i).getAsJsonObject().get("type").getAsString();
                week[i] = week_forecast;
                temp[i] = temp_forecast;
            }
            //adapter all data
            ((TextView) findViewById(R.id.tv_location)).setText(city);
            ((TextView) findViewById(R.id.tv_date)).setText(day+"/"+month+"/"+year);
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
            ((TextView) findViewById(R.id.tv_week1)).setText(week[0]);
            ((TextView) findViewById(R.id.tv_week2)).setText(week[1]);
            ((TextView) findViewById(R.id.tv_week3)).setText(week[2]);
            ((TextView) findViewById(R.id.tv_week4)).setText(week[3]);
            ((TextView) findViewById(R.id.tv_week5)).setText(week[4]);

            ((ImageView) findViewById(R.id.img_weather_condition1)).setImageDrawable(updateDrawable(temp[0],true));
            ((ImageView) findViewById(R.id.img_weather_condition2)).setImageDrawable(updateDrawable(temp[1],false));
            ((ImageView) findViewById(R.id.img_weather_condition3)).setImageDrawable(updateDrawable(temp[2],false));
            ((ImageView) findViewById(R.id.img_weather_condition4)).setImageDrawable(updateDrawable(temp[3],false));
            ((ImageView) findViewById(R.id.img_weather_condition5)).setImageDrawable(updateDrawable(temp[4],false));
        }

        protected Drawable updateDrawable(String type,Boolean isToday){
            Drawable drawable = null;
            if(isToday == false){
                switch (type){
                    case "小雨":
                        drawable = getResources().getDrawable(R.drawable.rainy_small);
                        break;
                    case "晴":
                        drawable = getResources().getDrawable(R.drawable.sunny_small);
                        break;
                    case "多云":
                        drawable = getResources().getDrawable(R.drawable.partly_sunny_small);
                        break;
                    case "阴":
                        drawable = getResources().getDrawable(R.drawable.windy_small);
                        break;
                }
            }
            else {
                switch (type){
                    case "小雨":
                        drawable = getResources().getDrawable(R.drawable.rainy_up);
                        break;
                    case "晴":
                        drawable = getResources().getDrawable(R.drawable.sunny_up);
                        break;
                    case "多云":
                        drawable = getResources().getDrawable(R.drawable.partly_sunny_up);
                        break;
                    case "阴":
                        drawable = getResources().getDrawable(R.drawable.windy_up);
                        break;
                }
            }
            return drawable;
        }
    }
}

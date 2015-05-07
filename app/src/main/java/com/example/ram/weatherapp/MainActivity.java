package com.example.ram.weatherapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrWeather;

    //private TextView mTemperatureLabel;

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidity_Value_label) TextView mHumidityValue;
    @InjectView(R.id.Precip_value) TextView mPrecipValue;
    @InjectView(R.id.summary_text) TextView mSummaryLabel;
    @InjectView(R.id.icon_imageview)ImageView mIconImageView;
    @InjectView(R.id.refresh_imageview)ImageView mrefresh_imageview;
    @InjectView(R.id.refresh_progressBar)ProgressBar mprogress_imageview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mprogress_imageview.setVisibility(View.INVISIBLE);

        final double latitude = 32.7050; //32.7050
        final double longitude = -97.1228; //97.1228

        mrefresh_imageview.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      getForecast(latitude,longitude);
                                                  }

                                              });

       // mTemperatureLabel = (TextView)findViewById(R.id.temparatureLabel);

        getForecast(latitude,longitude);

    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "d09e3b3e15dcc646d82b5cc974ea8b2c";

        String forecasturl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;

        //String sampleurl = " http://requestb.in.";

        if(NetworkisAvailable()) {

            refresh_toggler();
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(forecasturl).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh_toggler();
                        }
                    });
                    AlertUseraboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh_toggler();
                        }
                    });


                    try {
                        String jsonData =  response.body().string();
                        // Response response = call.execute();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {

                            mCurrWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {
                            AlertUseraboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught:", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception caught:", e);
                    }

                }
            });
        }
                else
        {
            Toast.makeText(this, getString(R.string.network_unavalaible_msg), Toast.LENGTH_LONG).show();
        }
    }

    private void refresh_toggler() {
        if(mprogress_imageview.getVisibility()==View.INVISIBLE) {
            mprogress_imageview.setVisibility(View.VISIBLE);
            mrefresh_imageview.setVisibility(View.INVISIBLE);
        }
        else
        {
            mprogress_imageview.setVisibility(View.INVISIBLE);
            mrefresh_imageview.setVisibility(View.VISIBLE);
        }

    }

    private void updateDisplay() {
       mTemperatureLabel.setText(mCurrWeather.getmTemperature() + "");
       mTimeLabel.setText("At " + mCurrWeather.getFormattedTime() + ", the weather is");
        Drawable drawable = getResources().getDrawable(mCurrWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
       mHumidityValue.setText(mCurrWeather.getmHumidity() + "");
       mPrecipValue.setText(mCurrWeather.getmPrecipChance() + "%");
        mSummaryLabel.setText(mCurrWeather.getmSummary());
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG, "From JSON : " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentweather = new CurrentWeather();
        currentweather.setmHumidity(currently.getDouble("humidity"));
        currentweather.setmTime(currently.getLong("time"));
        currentweather.setmIcon(currently.getString("icon"));
        currentweather.setmPrecipChance(currently.getDouble("precipProbability"));
        currentweather.setmSummary(currently.getString("summary"));
        currentweather.setmTemperature(currently.getDouble("temperature"));
        currentweather.setmTimeZone(timeZone);

        Log.d(TAG, currentweather.getFormattedTime());

        return currentweather;

      //  return new CurrentWeather(); // shd check this




    }

    private boolean NetworkisAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        //check network and also if connected to the web
        if(networkInfo != null && networkInfo.isConnected())
        {
           isAvailable = true;
        }
        return isAvailable;

    }

    private void AlertUseraboutError() {

        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}

//        try {
//           // Response response = call.execute();
//
//            if(response.isSuccessful())
//            {
//                Log.v(TAG, response.body().string());
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Exception caught:",e);







//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/




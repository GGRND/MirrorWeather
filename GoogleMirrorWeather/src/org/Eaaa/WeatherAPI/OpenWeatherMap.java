package org.Eaaa.WeatherAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.http.InputStreamContent;
import com.google.gson.Gson;

public class OpenWeatherMap implements WeatherAPI{
	private static WeatherAPI weather;

    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String COUNTRY = "q=";
    private static final String COORD_1 = "lat=";
    private static final String COORD_2 = "lon=";

    private static final String[] WIND_DIRECTIONS = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};

    /**
     * @return WeatherApi instance
     */
    public static WeatherAPI getInstance() {
        if (weather == null) {
            weather = new OpenWeatherMap();
        }
        return weather;
    }

    private OpenWeatherMap() {
    }

    @Override
    public String toString() {
        return OpenWeatherMap.class.getSimpleName();
    }

    @Override
    public String getWeatherBy(String country, String city) {
        return URL + COUNTRY + country + "," + city;
    }

    @Override
    public String getWeatherBy(double latitude, double longitude) {
        return URL + COORD_1 + latitude + COORD_2 + longitude;
    }

    @Override
    public WeatherObject unpack(String content, boolean coord) {
        WeatherObject weatherObject = new WeatherObject(this);
        try {
            //Value used to define when this request was made
            weatherObject.setTime(System.currentTimeMillis());
            Logger.getLogger("MyLogger").info("Before creating JsonObject");
            //Splitting the main JSON file into the two import parts, main and wind
            JSONObject mainJSON = new JSONObject(content);
            JSONObject main = mainJSON.getJSONObject("main");
            JSONObject wind = mainJSON.getJSONObject("wind");
            Logger.getLogger("MyLogger").info("After creating JsonObject");
            //TEMPERATURE
            //The temperature obtained from OpenWeatherMap, is based on Kelvin
            double temperature = (main.getDouble("temp") - 273.15);
            weatherObject.setString(WeatherObject.TEMPERATURE, String.format("%1.1f", temperature) + " C");

            //WIND SPEED
            weatherObject.setString(WeatherObject.WIND_SPEED, wind.getDouble("speed") + " M/S");

            //WIND DIRECTION
            //To turn the wind double value into a string, 360 is divided by 16 (22.5)
            //That value is used to divide the double obtained from JSON
            //And by adding 0.5 to the result, secures that the value use rounded correctly.
            int windDirection = (int) (wind.getDouble("deg") / 22.5 + 0.5);
            weatherObject.setString(WeatherObject.WIND_DIRECTION, WIND_DIRECTIONS[windDirection]);

            //RAIN
            //Since rain isn't always defined there has to be another
            //Try/catch, and in case it fails the default value is set to 0 mm.
            try {
                JSONObject rain = mainJSON.getJSONObject("rain");
                weatherObject.setString(WeatherObject.RAIN, rain.getInt("3h") + " mm");
            } catch (JSONException e) {
                weatherObject.setString(WeatherObject.RAIN, "0 mm");
                Logger.getLogger("MyLogger").info(e.getMessage());
            }

            //WEATHER_ICON
            //The String isn't valid to use for lookup in the R.drawable,
            //So to to get the right icon when needed use #setIcon
            JSONObject weather = mainJSON.getJSONArray("weather").getJSONObject(0);
            weatherObject.setString(WeatherObject.ICON, weather.getString("icon"));

        } catch (JSONException e) {
        	Logger.getLogger("MyLogger").info(e.getMessage());
        }

        return weatherObject;
    }

    public String toJson()
    {
    	Gson gson = new Gson();
    	String json = gson.toJson(this);
    	return json;
    }
    
    public static WeatherObject constructFromJson(String json)
    {
    	Gson gson = new Gson();
    	return gson.fromJson(json, WeatherObject.class);
    }
    
    @Override
    public InputStreamContent getIcon(String icon) {
        //By using the context that is obtained from the ImageView,
        //A search can be done through the resources, which will
        //Return the correct id, or 0 if it isn't found.
        if (icon != null) {
        	URL url;
			try
			{
				url = new URL("https://hypnotic-seat-677.Appspot.com/static/OpenWeatherMapIcons/owm_" + icon + ".png");
				InputStreamContent attachment = new InputStreamContent("image/png", url.openStream());
				return attachment;
			} catch (MalformedURLException e)
			{
				Logger.getLogger("MyLogger").info(e.getMessage());
			} catch (IOException e)
			{
				Logger.getLogger("MyLogger").info(e.getMessage());
			}
        }
		return new InputStreamContent(null, null);
    }
    
    
    @Override
    public void destroy() {
        weather = null;
    }
}

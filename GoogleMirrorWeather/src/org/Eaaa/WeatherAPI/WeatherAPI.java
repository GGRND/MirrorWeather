package org.Eaaa.WeatherAPI;

import com.google.api.client.http.InputStreamContent;



public interface WeatherAPI {

    /**
     * Used to patch together an URL that can be used to make a JSON request
     * with, by using country and city.
     *
     * @param country String, The Country for the desired weather information.
     * @param city String, The City for the desired weather information.
     * @return String, URL used to download the requested JSON.
     */
    public String getWeatherBy(String country, String city);

    /**
     * Used to patch together an URL that can be used to make a JSON request
     * with, by using latitude and longitude.
     *
     * @param latitude Double, The latitude of the requested location.
     * @param longitude Double, The longitude of the requested location.
     * @return String, URL used to download the requested JSON.
     */
    public String getWeatherBy(double latitude, double longitude);

    /**
     * Used to repack the JSON into a String[].
     *
     * @param content String, the content of the entire JSON String.
     * @param coord Boolean, true if the request was made with latitude and longitude
     * @return WeatherObject
     */
    public WeatherObject unpack(String content, boolean coord);

    /**
     * Used to change icon on a selected ImageView.
     *
     * @param imageView ImageView, the view in which the icon should be displayed
     * @param icon String, name of the icon.
     */
    public InputStreamContent getIcon(String icon);

    /**
     * Used to free memory
     */
    public void destroy();
}
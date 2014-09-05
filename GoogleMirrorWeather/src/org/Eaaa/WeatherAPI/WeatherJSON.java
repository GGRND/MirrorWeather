package org.Eaaa.WeatherAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;



public class WeatherJSON {
	/**
	 * Returns a WeatherObject after assembling the data returned from a
	 * webservice.
	 *
	 * @param country
	 *            String target Country
	 * @param city
	 *            String target city
	 * @param api
	 *            WeatherAPI that is used
	 * @return WeatherObject
	 */
	public static WeatherObject getWeatherBy(String country, String city,
			WeatherAPI api) {
		String content = openConnection(api.getWeatherBy(country, city));
		return api.unpack(content, false);
	}

	/**
	 * Returns a WeatherObject after assembling the data returned from a
	 * webservice.
	 *
	 * @param latitude
	 *            latitude of the target location
	 * @param longitude
	 *            longitude of the target location
	 * @param api
	 *            WeatherAPI that is used
	 * @return WeatherObject
	 */
	public static WeatherObject getWeatherBy(double latitude, double longitude,
			WeatherAPI api) {
		String content = openConnection(api.getWeatherBy(latitude, longitude));
		return api.unpack(content, true);
	}

	/**
	 * Returns a rawJSON string if connection to the URL succeeds.
	 *
	 * @param url
	 *            String of the URL
	 * @return String rawJSON
	 */
	private static String openConnection(String url) {
		String content = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				content = connectionAccepted(connection);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.getLogger("MyLogger").info(content);
		return content;
	}

	/**
	 * Returns a rawJSON string if connection to the URL succeeds.
	 *
	 * @param connection
	 *            HttpURLConnection
	 * @return String rawJSON
	 * @throws IOException
	 */
	private static String connectionAccepted(HttpURLConnection connection)
			throws IOException {
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		
		return builder.toString();
	}
}

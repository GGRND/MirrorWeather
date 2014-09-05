package org.Eaaa.GoogleMirrorWeather;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.*;

import org.Eaaa.WeatherAPI.OpenWeatherMap;
import org.Eaaa.WeatherAPI.WeatherAPI;
import org.Eaaa.WeatherAPI.WeatherJSON;
import org.Eaaa.WeatherAPI.WeatherObject;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.glassware.AuthUtil;

@SuppressWarnings("serial")
public class GoogleMirrorWeatherServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// print out results on the web browser
				resp.setContentType("text/html; charset=utf-8");
				resp.getWriter().println(
						"<html><head>" +
						"<meta http-equiv=\"refresh\"content=\"3;url=/index.html\">" +
						"</head>" +
						"<body>A card is inserted to your timeline.<br></body></html>" );
				
				// get access to MirrorAPIT
				Mirror mirror = getMirror(req);
				
				// get access to the timeline
				Timeline timeline = mirror.timeline();
				
				WeatherObject weather = WeatherJSON.getWeatherBy("Denmark", "Aarhus", OpenWeatherMap.getInstance());
				
//				String text = 	"Temp: " + weather.getString(0) +
//								"\nWind: " + weather.getString(1) +
//								"\nDirection: " + weather.getString(2) + 
//								"\nRain: " + weather.getString(3);
				
				String img = weather.getString(4);
				
				String html = "<article>" +
						"<figure>" +
						"<img src=\"https://hypnotic-seat-677.Appspot.com/static/OpenWeatherMapIcons/owm_" + img  + ".png>" +
						"</figure>" +
						"<section>" +
						"<table class=\"text-small align-justify no-border\">" +
						"<tbody>" +
						"<tr>" +
						"<td class=\"muted\">Temp: </td>" +
						"<td>" + weather.getString(0) +"</td>" +
						"</tr>" +
						"<tr>" +
						"<td class=\"muted\">Wind: </td>" +
						"<td>" + weather.getString(1) + "</td>" +
						"</tr>" +
						"<tr>" +
						"<td class=\"muted\">Direction: </td>" +
						"<td>" + weather.getString(2) + "</td>" +
						"</tr>" +
						"<tr>" +
						"<td class=\"muted\">Rain: </td>" +
						"<td>" + weather.getString(3) + "</td>" +
						"</tr>" +
						"</tbody>" +
						"</table>" +
						"</section>" +
						"</article>";
				  
				  
				
				
				// create a timeline item (card)
				TimelineItem timelineitem = new TimelineItem()
//					.setText(text)
					
					.setHtml(html)
					.setDisplayTime(new DateTime(new Date()))
					.setNotification(new NotificationConfig().setLevel("DEFAULT"));
				
				
				
				
				// add menu items with built-in actions
				List<MenuItem> menuItemList = new ArrayList<MenuItem>();
				menuItemList.add(new MenuItem().setAction("TOGGLE_PINNED")); // pins the card on the left side of the start screen or unpins it from there
				menuItemList.add(new MenuItem().setAction("DELETE")); // deletes the timeline
				
				// insert the card into the timeline
				timeline.insert(timelineitem).execute();
	}
	
	
	private Mirror getMirror(HttpServletRequest req) throws IOException
	{
		// get credential
		Credential credential = AuthUtil.getCredential(req);
		
		// build access to Mirror API
		return new Mirror.Builder(new UrlFetchTransport(), new JacksonFactory(), credential).setApplicationName("Hello Glass!").build();
	}
}

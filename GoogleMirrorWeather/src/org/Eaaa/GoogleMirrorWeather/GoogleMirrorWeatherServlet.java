package org.Eaaa.GoogleMirrorWeather;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.Subscription;
import com.google.api.services.mirror.model.SubscriptionsListResponse;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.TimelineListResponse;
import com.google.glassware.AuthUtil;

@SuppressWarnings("serial")
public class GoogleMirrorWeatherServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		
		// get access to MirrorAPIT
		Mirror mirror = getMirror(req);
		
		// get access to the timeline
		Timeline timeline = mirror.timeline();
		
		List<TimelineItem> items = retrieveAllTimelineItems(mirror);
		boolean exists = false;

		for (int i = 0; i < items.size(); i++)
		{
			if (items.get(i).getSourceItemId() != "Weather")
			{
				exists = true;
			}
		}
		
		if (exists)
		{
			resp.setContentType("text/html; charset=utf-8");
			resp.getWriter()
					.println(
							"<html><head>"
									+ "<meta http-equiv=\"refresh\"content=\"3;url=/index.html\">"
									+ "</head>"
									+ "<body>A card already exists in your timeline.<br></body></html>");
		} else
		{
			try
			{
				Subscribe(mirror, req);
			} catch(Exception e)
			{
				Logger.getLogger("MyLogger").info(e.getMessage());
			}
			
				resp.setContentType("text/html; charset=utf-8");
				resp.getWriter().println(
						"<html><head>" +
						"<meta http-equiv=\"refresh\"content=\"3;url=/index.html\">" +
						"</head>" +
						"<body>A card is inserted to your timeline.<br></body></html>" );
				
				
				
				
				
				WeatherObject weather = WeatherJSON.getWeatherBy("Denmark", "Aarhus", OpenWeatherMap.getInstance());
				
				String html = setHtml(weather.getString(4), weather.getString(0), weather.getString(1), weather.getString(2), weather.getString(3));
				  
				  
				
				// create a timeline item (card)
				TimelineItem timelineitem = new TimelineItem()
					.setHtml(html)
					.setDisplayTime(new DateTime(new Date()))
					.setNotification(new NotificationConfig().setLevel("DEFAULT"));
				
				
				// add menu items with built-in actions
				List<MenuItem> menuItemList = new ArrayList<MenuItem>();
				menuItemList.add(new MenuItem().setAction("TOGGLE_PINNED")); // pins the card on the left side of the start screen or unpins it from there
				menuItemList.add(new MenuItem().setAction("DELETE")); // deletes the timeline
				
				// Adding Custom Menu
				List<MenuValue> menuValues = new ArrayList<MenuValue>();
				menuValues.add(new MenuValue().setDisplayName("Update Weather"));
				menuItemList.add(new MenuItem().setValues(menuValues)
						.setId("Update").setAction("CUSTOM")
						.setPayload("Update_Weather"));
				
				timelineitem.setMenuItems(menuItemList);
				timelineitem.setSourceItemId("Weather");
				timelineitem.setIsPinned(true);
				// insert the card into the timeline
				timeline.insert(timelineitem).execute();
		}
	}
	
	
	private Mirror getMirror(HttpServletRequest req) throws IOException
	{
		// get credential
		Credential credential = AuthUtil.getCredential(req);
		
		// build access to Mirror API
		return new Mirror.Builder(new UrlFetchTransport(), new JacksonFactory(), credential).setApplicationName("Hello Glass!").build();
	}
	
	public static String setHtml(String img, String temp, String wind, String direction, String rain)
	{
		String html = "<article>" +
				"<figure>" +
				"<img src=\"https://hypnotic-seat-677.Appspot.com/static/OpenWeatherMapIcons/owm_" + img  + ".png\" height=\"360\" width=\"240\" >" +
				"</figure>" +
				"<section>" +
				"<table class=\"text-small align-justify no-border\">" +
				"<tbody>" +
				"<tr>" +
				"<td class=\"muted\">Temp: </td>" +
				"<td>" + temp +"</td>" +
				"</tr>" +
				"<tr>" +
				"<td class=\"muted\">Wind: </td>" +
				"<td>" + wind + "</td>" +
				"</tr>" +
				"<tr>" +
				"<td class=\"muted\">Direction: </td>" +
				"<td>" + direction + "</td>" +
				"</tr>" +
				"<tr>" +
				"<td class=\"muted\">Rain: </td>" +
				"<td>" + rain + "</td>" +
				"</tr>" +
				"</tbody>" +
				"</table>" +
				"</section>" +
				"</article>";
		return html;
	}
	
	private void Subscribe(Mirror mirror, HttpServletRequest req) throws IOException
	{
		SubscriptionsListResponse subslist_resp = mirror.subscriptions().list().execute();
		List<Subscription> subsclist = subslist_resp.getItems();
		String subscription_id_to_delete = null;
		boolean exists = false;
		for (Subscription subsc : subsclist) 
		{
			if (subsc.getId().equals("timeline")) 
			{
				exists = true;
				subscription_id_to_delete = subsc.getId();
				break;
			}
		}
		if (exists) 
		{
			mirror.subscriptions().delete(subscription_id_to_delete).execute();
		} 
			
			// subscribe to timeline
			Subscription subscription = new Subscription();
			subscription.setCollection("timeline");
			subscription.setCallbackUrl("https://mirrornotifications.Appspot.com/forward?url=https://hypnotic-seat-677.Appspot.com/notification");
			subscription.setUserToken(AuthUtil.getUserId(req));
			mirror.subscriptions().insert(subscription).execute();
	}
	
	public static List<TimelineItem> retrieveAllTimelineItems(Mirror mirror)
	{
		List<TimelineItem> result = new ArrayList<TimelineItem>();
		try
		{
			Timeline.List request = mirror.timeline().list();

			do
			{
				request.setPinnedOnly(true);
				TimelineListResponse timelineItems = request.execute();
				if (timelineItems.getItems() != null
						&& timelineItems.getItems().size() > 0)
				{
					result.addAll(timelineItems.getItems());
					request.setPageToken(timelineItems.getNextPageToken());
				} else
				{
					break;
				}
			} while (request.getPageToken() != null
					&& request.getPageToken().length() > 0);
		} catch (IOException e)
		{
			Logger.getLogger("MyLogger").info("An error occurred " + e);
			return null;
		}
		for (int i = 0; i < result.size(); i++)
		{
			Logger.getLogger("MyLogger").info(
					"TimelineItemId " + result.get(i).getId());
		}
		return result;
	}
}

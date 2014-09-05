package org.Eaaa.GoogleMirrorWeather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.Subscription;
import com.google.api.services.mirror.model.SubscriptionsListResponse;
import com.google.glassware.AuthUtil;

@SuppressWarnings("serial")
public class SubscriptionServlet extends HttpServlet {

	Logger logger = Logger.getLogger("MyLogger");
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException 
	{
		
		// get mirror api
		Mirror mirror = getMirror(req);
	
		// see if timeline subscription already exists
		SubscriptionsListResponse subslist_resp = mirror.subscriptions().list().execute();
		List<Subscription> subsclist = subslist_resp.getItems();
		boolean exists = false;
		for (Subscription subsc : subsclist) 
		{
			if (subsc.getId().equals("timeline")) 
			{
				exists = true;
				break;
			}
		}
		if (exists) 
		{
			// print out results on the web browser
			resp.setContentType("text/html; charset=utf-8");
			resp.getWriter().println(
					"<html><head><meta http-equiv=\"refresh\" content=\"3;url=/index.html\"></head> "
					+ "<body>Timeline subsciption already exists.</body></html>");
		} else 
		{
			
			// subscribe to timeline
			Subscription subscription = new Subscription();
			subscription.setCollection("timeline");
			subscription.setCallbackUrl("https://mirrornotifications.Appspot.com/forward?url=https://hypnotic-seat-677.Appspot.com/notification");
			subscription.setUserToken(AuthUtil.getUserId(req));
			mirror.subscriptions().insert(subscription).execute();
			logger.info(mirror.subscriptions().toString());
			
			// print out results on the web browser
			resp.setContentType("text/html; charset=utf-8");
			resp.getWriter().println(
					"<html><head><meta http-equiv=\"refresh\" content=\"3;url=/index.html\"></head> "
					+ "<body>Subscribed to timeline update.</body></html>");
		}
	}

private Mirror getMirror(HttpServletRequest req) throws IOException
{
	// get credential
	Credential credential = AuthUtil.getCredential(req);
	
	// build access to Mirror API
	return new Mirror.Builder(new UrlFetchTransport(), new JacksonFactory(), credential).setApplicationName("Hello Glass!").build();
}

}

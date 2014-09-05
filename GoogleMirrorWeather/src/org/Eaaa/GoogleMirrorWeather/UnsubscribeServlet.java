package org.Eaaa.GoogleMirrorWeather;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
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
public class UnsubscribeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// get mirror api
		Mirror mirror = getMirror(req);

		// look for timeline subscription
		SubscriptionsListResponse subslist_resp = mirror.subscriptions().list().execute();
		List<Subscription> subsclist = subslist_resp.getItems();
		String subscription_id_to_delete = null;
		for (Subscription subsc : subsclist) {
			if (subsc.getId().equals("timeline")) {
				subscription_id_to_delete = subsc.getId();
				break;
			}
		}

		if (subscription_id_to_delete == null) {
			// print out results on the web browser
			resp.setContentType("text/html; charset=utf-8");
			resp.getWriter().println(
					"<html><head><meta http-equiv=\"refresh\" content=\"3;url=/index.html\"></head> "
							+ "<body>No timeline subscriptions available.</body></html>");
		} else {
			// unsubscribe to timeline
			mirror.subscriptions().delete(subscription_id_to_delete).execute();

			// print out results on the web browser
			resp.setContentType("text/html; charset=utf-8");
			resp.getWriter().println(
					"<html><head><meta http-equiv=\"refresh\" content=\"3;url=/index.html\"></head> "
							+ "<body>Unsubscribed timeline update.</body></html>");
		}
	}
	
	private Mirror getMirror(HttpServletRequest req) throws IOException {
		// get credential
		Credential credential = AuthUtil.getCredential(req);
		
		// build access to Mirror API
		return new Mirror.Builder( 
				new UrlFetchTransport(), new JacksonFactory(), credential)
					.setApplicationName("Hello World").build();
	}
}

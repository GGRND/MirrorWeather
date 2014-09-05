package org.Eaaa.GoogleMirrorWeather;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.glassware.AuthUtil;

public class LogoutServlet extends HttpServlet 
{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// log out
		AuthUtil.clearUserId(req);
		// print out results on the web browser
		resp.setContentType("text/html; charset=utf-8");
		resp.getWriter().println(
				"<html><head><meta http-equiv=\"refresh\" content=\"3;url=/index.html\"></head> " +
		"<body>Good bye!<br></body></html>");
	}

}

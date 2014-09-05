package com.google.glassware;

import java.util.Arrays;
import java.util.List;

public class AuthSettings {
	public static String CLIENT_ID = "632716633733-slgikj825qmvipiueknolmgq82he5h1m.apps.googleusercontent.com";
	public static String CLIENT_SECRET = "_ZTkkDlokAj0IF9oH8cZlkDB";
	
	public static final List<String> GLASS_SCOPE = 
			  Arrays.asList("https://www.googleapis.com/auth/glass.timeline",
					  	"https://www.googleapis.com/auth/glass.location",
					  	"https://www.googleapis.com/auth/userinfo.profile");
}

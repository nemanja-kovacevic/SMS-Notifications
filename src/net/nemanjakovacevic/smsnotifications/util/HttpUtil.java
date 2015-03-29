package net.nemanjakovacevic.smsnotifications.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import net.nemanjakovacevic.smsnotifications.exceptions.InternetConnenctionException;

public class HttpUtil {

	public static InputStream openHttpConnection(String urlString) throws InternetConnenctionException, IOException {
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
        if (!(conn instanceof HttpURLConnection)) {                    
            throw new InternetConnenctionException("Not an HTTP connection. URL : " + urlString);
        }
        
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.connect(); 

            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        } catch (Exception ex) {
            throw new InternetConnenctionException("Error connecting. URL : " + urlString);            
        }
        
        if(in == null){
        	throw new InternetConnenctionException("Input stream is null");
        }
        
        return in;     
    }
	
}

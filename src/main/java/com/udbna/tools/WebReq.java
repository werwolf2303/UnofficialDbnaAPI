package com.udbna.tools;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicTokenIterator;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebReq {
    public CookieStore cookieStore = null;
    HttpContext httpContext = null;
    boolean loggedIn = false;
    public String user = "";
    public String pass = "";
    public boolean ping(String url) {
        try {
            HttpGet request = new HttpGet(url.replace("wss", "https"));
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response1 = httpClient.execute(request, httpContext);
            return true;
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
    public void login(String username, String password) {
        user = username;
        pass = password;
        try {
            HttpPost request = new HttpPost("https://www.dbna.com/json/user/login");
            HttpClient httpClient = new DefaultHttpClient();
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            cookieStore = new BasicCookieStore();
            httpContext = new BasicHttpContext();
            httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
            HttpResponse response1 = httpClient.execute(request, httpContext);
            if(response1.getStatusLine().getStatusCode()==200) {
                loggedIn = true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getLoginResponse() {
        try {
            HttpPost request = new HttpPost("https://www.dbna.com/json/user/login");
            HttpClient httpClient = new DefaultHttpClient();
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("username", user));
            params.add(new BasicNameValuePair("password", pass));
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
            HttpResponse response1 = httpClient.execute(request, httpContext);
            return EntityUtils.toString(response1.getEntity());
        } catch (ClientProtocolException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }
    public String request(String url) {
        if(!loggedIn) {
            System.out.println("Wrong credentials");
            return "";
        }
        try {
            HttpGet request = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response1 = httpClient.execute(request, httpContext);
            if(response1.getStatusLine().getStatusCode()!=200) {
                login(user,pass);
                return request(url);
            }
            return EntityUtils.toString(response1.getEntity());
        } catch (ClientProtocolException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }
}

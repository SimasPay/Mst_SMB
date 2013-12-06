package com.mfino.android.client.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpConnectionManager
{
    private String              URL;
    private List<NameValuePair> params;
    HttpResponse                response;

    public HttpConnectionManager(final String URL, final List<NameValuePair> params)
    {
	this.URL = URL;
	this.params = params;
    }

    public void execute() throws IOException, Exception
    {
	HttpPost postRequest = new HttpPost(URL);

	if (params == null)
	{
	    throw new Exception("Parameters not set for POST Request");
	}
	if (URL == null)
	{
	    throw new Exception("URL not set");
	}

	try
	{
	    HttpParams httpParams = new BasicHttpParams();
	    int timeoutConnection = Utils.TimeoutConnection;
	    HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
	    int timeoutSocket = Utils.TimeoutSocket;
	    HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

	    DefaultHttpClient dhc = new DefaultHttpClient(httpParams);
	    postRequest.setEntity(new UrlEncodedFormEntity(params));
	    response = dhc.execute(postRequest);
	}
	catch (UnsupportedEncodingException e)
	{
	    e.printStackTrace();
	}
	catch (ClientProtocolException e)
	{
	    e.printStackTrace();
	}
    }

    public String getResponseContent() throws Exception
    {
	if (response == null)
	    throw new Exception("Response is Null");

	HttpEntity ent = response.getEntity();
	InputStream is = ent.getContent();
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	String line = null;
	StringBuilder str = new StringBuilder();
	while ((line = reader.readLine()) != null)
	{
	    str.append(line + "\n");
	}
	reader.close();
	line = str.toString();
	line = line.replaceAll("(<[^>]+>)", "");
	return line;

    }
}

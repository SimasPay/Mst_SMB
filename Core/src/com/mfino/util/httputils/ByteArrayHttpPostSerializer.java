package com.mfino.util.httputils;

import java.io.ByteArrayInputStream;
import java.net.Socket;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CBase64;
import com.mfino.util.ConfigurationUtil;

public class ByteArrayHttpPostSerializer {

	private static final Logger log = LoggerFactory.getLogger(ByteArrayHttpPostSerializer.class);

	/*
	 * 
	 * This method serializes a byte array to a URL on the network using a HTTP
	 * POST request and gets a byte array in response.
	 */
	public static byte[] serialize(byte[] data, String hostName, int port,
			String requestLine) throws Exception {

		byte[] retValObject = null;

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		BasicHttpProcessor httpproc = new BasicHttpProcessor();
		// Required protocol interceptors
		httpproc.addInterceptor(new RequestContent());
		httpproc.addInterceptor(new RequestTargetHost());
		// Recommended protocol interceptors
		httpproc.addInterceptor(new RequestConnControl());
		httpproc.addInterceptor(new RequestUserAgent());
		httpproc.addInterceptor(new RequestExpectContinue());

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpContext context = new BasicHttpContext(null);

		HttpHost host = new HttpHost(hostName, port);

		DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

		ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

		try {
			params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, ConfigurationUtil.getSocketTimeout());
			params.setParameter("Content-Length", data.length);

			BasicHttpEntity bi = new BasicHttpEntity();
			bi.setContent(new ByteArrayInputStream(data));
			bi.setContentLength(data.length);
			bi.setChunked(false);

			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket, params);
				conn.setSocketTimeout(ConfigurationUtil.getSocketTimeout());
			}
			BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
					"POST", requestLine);
			request.setEntity(bi);

			final String userName = ConfigurationUtil.getBackendUsername();
			final String password = ConfigurationUtil.getBackendPassword();
			String Credentials = userName + ":" + password;
			String encPassword = CBase64.ToBase64(Credentials.getBytes());
			request.setHeader("Authorization", "Basic " + encPassword);

			request.setParams(params);
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);
			httpexecutor.postProcess(response, httpproc, context);

			retValObject = EntityUtils.toByteArray(response.getEntity());

			log.info("Response from server : " + new String(retValObject));

			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				log.info("Connection kept alive...");
			}
		} finally {
			conn.close();
		}

		return retValObject;
	}
}

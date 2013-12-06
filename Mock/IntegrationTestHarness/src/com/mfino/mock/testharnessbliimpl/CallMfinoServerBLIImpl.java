/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import com.mfino.mock.testharnessbli.CallMfinoServerBLI;
import java.net.Socket;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import sun.misc.BASE64Encoder;



/**
 *
 * @author sunil
 */
public class CallMfinoServerBLIImpl implements CallMfinoServerBLI {
    public void invokeMfinoServer(TestHarnessValueObject input, TestHarnessValueObject output)throws Exception{
        invokeMfinoServer(input);
    }

    public void invokeMfinoServer(TestHarnessValueObject input)throws Exception {
        Logger logger=ITHLogger.getLogger();
//        Properties properties = new Properties();
//        try {
//            InputStream fileIS = this.getClass().getResourceAsStream("../integrationtestharness/resources/IntegrationTestHarness.properties");
//            if(fileIS!=null)
//                properties.load(fileIS);
//            else
//                logger.warn("Failed to read the Properties File:");
//
//
//        } catch (IOException e) {
//            logger.warn( "Failed to read the Properties File:");
//            logger.warn( e.getMessage());
//            e.printStackTrace();
//
//        }

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);

        
        logger.info("Calling mFino Server");

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
        HttpHost host = new HttpHost("dev.mfino.com",30004);

        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();
         
        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);


        try {   
                
                String action=new String();
                action=input.getOutgoingUrl().substring(1,(input.getOutgoingUrl().indexOf("?"))-1);
                String outgoingURL=new String();
                //outgoingURL="http://staging.mfino.com:30004";
                //+properties.getProperty("mFinoHostServer")+":"+properties.getProperty("mFinoServerPort");
                outgoingURL=input.getOutgoingUrl();

                logger.info("Url going to mFino Server is ::"+outgoingURL);


                if (!conn.isOpen()) {
                    Socket socket = new Socket(host.getHostName(), host.getPort());
                    conn.bind(socket, params);
                }
                BasicHttpRequest request = new BasicHttpRequest("GET", outgoingURL);
                logger.info(">> Request URI: " + request.getRequestLine().getUri());

                request.setParams(params);
                final String userName = "username";
                final String password = "pasword";
                BASE64Encoder encoder = new BASE64Encoder();
                byte[] encodedPassword = ( userName + ":" + password ).getBytes();
                request.setHeader( "Authorization","Basic " + encoder.encode( encodedPassword ) );
                httpexecutor.preProcess(request, httpproc, context);
                HttpResponse response = httpexecutor.execute(request, conn, context);
                response.setParams(params);
                httpexecutor.postProcess(response, httpproc, context);
                response.getParams();
                input.setHtmlResponse(EntityUtils.toString(response.getEntity()));
                logger.info("Response From Multix: " + response.getEntity().toString());
                logger.info(input.getHtmlResponse());
                logger.info("==============");
                if (!connStrategy.keepAlive(response, context)) {
                    conn.close();
                } else {
                    logger.info("Connection kept alive...");
                }

            }
            finally {
                conn.close();
            }

    }

     public void invokeMfinoServer_noVO(String outgoingUrl)throws Exception {
        Logger logger=ITHLogger.getLogger();
//        Properties properties = new Properties();
//        try {
//            InputStream fileIS = this.getClass().getResourceAsStream("../integrationtestharness/resources/IntegrationTestHarness.properties");
//            if(fileIS!=null)
//                properties.load(fileIS);
//            else
//                logger.warn("Failed to read the Properties File:");
//
//
//        } catch (IOException e) {
//            logger.warn( "Failed to read the Properties File:");
//            logger.warn( e.getMessage());
//            e.printStackTrace();
//
//        }

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);


        logger.info("Calling mFino Server");

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
        HttpHost host = new HttpHost("dev.mfino.com", 30004);

        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);


        try {

                logger.info("Url going to mFino Server is ::"+outgoingUrl);
                if (!conn.isOpen()) {
                    Socket socket = new Socket(host.getHostName(), host.getPort());
                    conn.bind(socket, params);
                }
                BasicHttpRequest request = new BasicHttpRequest("GET", outgoingUrl);
                logger.info(">> Request URI: " + request.getRequestLine().getUri());

                request.setParams(params);
                final String userName = "username";
                final String password = "pasword";
                BASE64Encoder encoder = new BASE64Encoder();
                byte[] encodedPassword = ( userName + ":" + password ).getBytes();
                request.setHeader( "Authorization","Basic " + encoder.encode( encodedPassword ) );
                httpexecutor.preProcess(request, httpproc, context);
                HttpResponse response = httpexecutor.execute(request, conn, context);
                response.setParams(params);
                httpexecutor.postProcess(response, httpproc, context);
                response.getParams();
                logger.info("<< Response: " + response.getStatusLine());
                logger.info(EntityUtils.toString(response.getEntity()));
                logger.info("==============");
                if (!connStrategy.keepAlive(response, context)) {
                    conn.close();
                } else {
                    logger.info("Connection kept alive...");
                }

            }
            finally {
                conn.close();
            }

    }
}

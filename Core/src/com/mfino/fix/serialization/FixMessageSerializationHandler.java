/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.nio.protocol.HttpRequestExecutionHandler;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;

/**
 *
 * @author sandeepjs
 */

public class FixMessageSerializationHandler implements HttpRequestExecutionHandler {

    private final static String REQUEST_SENT = "request-sent";
    private final static String RESPONSE_RECEIVED = "response-received";
    public static FixMessageRequestCallback callback;
    public final CountDownLatch requestCount;
    private byte[] data;
    private byte[] retValdata;
    private String requestLine;
    private int dataLength;

    private static Logger log = LoggerFactory.getLogger(FixMessageSerializationHandler.class);
    
    public FixMessageSerializationHandler() {
        super();
        this.requestCount = new CountDownLatch(1);
        callback = new FixMessageRequestCallback(requestCount);
    }

    public void setData(int length , byte[] data) {
        this.dataLength =length;
        this.data = data;
    }

    public void setRequestLine(String requestLine)
    {
        this.requestLine = requestLine;
    }

    public void initalizeContext(final HttpContext context, final Object attachment) {
        HttpHost targetHost = (HttpHost) attachment;
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, targetHost);
    }

    public void finalizeContext(final HttpContext context) {
        Object flag = context.getAttribute(RESPONSE_RECEIVED);
        if (flag == null) {
            // Signal completion of the request execution
            requestCount.countDown();
        }
    }

    public HttpRequest submitRequest(final HttpContext context) {
        HttpHost targetHost = (HttpHost) context.getAttribute(
                ExecutionContext.HTTP_TARGET_HOST);
        Object flag = context.getAttribute(REQUEST_SENT);
        if (flag == null) {

            BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
            basicHttpEntity.setContent(new ByteArrayInputStream(data));

            BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", this.requestLine);
            request.setEntity(basicHttpEntity);
            
            request.getParams().setParameter("Content-Length", this.dataLength);
            final String userName = "admin";
            final String password = "admin";
            
            PasswordEncoder encoder = new ShaPasswordEncoder(1);
            String encPassword = encoder.encodePassword(password, userName);
            request.setHeader( "Authorization","Basic " + encPassword );

            return request;

        } else {
            // No new request to submit
            return null;
        }
    }

    public void handleResponse(final HttpResponse response, final HttpContext context) {
        HttpEntity entity = response.getEntity();
        try {

            retValdata = EntityUtils.toByteArray(response.getEntity());

            CMultiXBuffer buffers = new CMultiXBuffer();
            buffers.Append(retValdata);

            CFIXMsg retValFixMsg = CFIXMsg.fromFIX(buffers);
            CMultiXBuffer nbuffers = new CMultiXBuffer();

            retValFixMsg.toFIX(nbuffers);

            log.info("After deSerialization :" + new String(nbuffers.DataPtr(), 0, nbuffers.Length()));

        } catch (IOException ex) {
            log.error("I/O error: " + ex.getMessage(), ex);
        } 

        context.setAttribute(RESPONSE_RECEIVED, Boolean.TRUE);

        // Signal completion of the request execution
        requestCount.countDown();
    }

    static class FixMessageRequestCallback implements SessionRequestCallback {

        private final CountDownLatch requestCount;

        public FixMessageRequestCallback(final CountDownLatch requestCount) {
            super();
            this.requestCount = requestCount;
        }

        public void cancelled(final SessionRequest request) {
            log.error("Connect request cancelled: " + request.getRemoteAddress());
            this.requestCount.countDown();
        }

        public void completed(final SessionRequest request) {
        }

        public void failed(final SessionRequest request) {
            log.error("Connect request failed: " + request.getRemoteAddress());
            this.requestCount.countDown();
        }

        public void timeout(final SessionRequest request) {
            log.error("Connect request timed out: " + request.getRemoteAddress());
            this.requestCount.countDown();
        }
    }
}

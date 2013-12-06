package com.mfino.util.httputils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultClientIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.BufferingHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.HttpRequestExecutionHandler;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteArrayHttpPostAsyncSerializer {

    private static Logger log = LoggerFactory.getLogger(ByteArrayHttpPostAsyncSerializer.class);
    /*
     *
     * This method serializes a byte array to a URL on the network
     * using a HTTP POST request
     * and gets a byte array in response.
     *
     */
    public static void serializeAsync(String host, int port, HttpRequestExecutionHandler mhandler, SessionRequestCallback callback, CountDownLatch requestCount) {

        try {

            HttpParams params = new BasicHttpParams();
            params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 15000).setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 18 * 1024).setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).setParameter(CoreProtocolPNames.USER_AGENT, "HttpComponents/1.1");

           
            final ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(2, params);

            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            httpproc.addInterceptor(new RequestContent());
            httpproc.addInterceptor(new RequestTargetHost());
            httpproc.addInterceptor(new RequestConnControl());
            httpproc.addInterceptor(new RequestUserAgent());
            httpproc.addInterceptor(new RequestExpectContinue());


            BufferingHttpClientHandler handler = new BufferingHttpClientHandler(
                    httpproc,
                    mhandler,
                    new DefaultConnectionReuseStrategy(),
                    params);

            handler.setEventListener(new EventLogger());

            final IOEventDispatch ioEventDispatch = new DefaultClientIOEventDispatch(handler, params);

            Thread t = new Thread(new Runnable() {

                public void run() {
                    try {
                        ioReactor.execute(ioEventDispatch);
                    } catch (InterruptedIOException ex) {
                        log.error("Interrupted", ex);
                    } catch (IOException error) {
                        log.error("I/O error: ", error);
                    }
                   
                }
            });
            t.start();

            SessionRequest[] reqs = new SessionRequest[1];
            reqs[0] = ioReactor.connect(
                    new InetSocketAddress(host, port),
                    null,
                    new HttpHost(host),
                    callback);

            // Block until all connections signal
            // completion of the request execution
            requestCount.await();

           
            ioReactor.shutdown();

         
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
    }

    static class EventLogger implements EventListener {

        public void connectionOpen(final NHttpConnection conn) {
        
        }

        public void connectionTimeout(final NHttpConnection conn) {
           
        }

        public void connectionClosed(final NHttpConnection conn) {
           
        }

        public void fatalIOException(final IOException ex, final NHttpConnection conn) {
            log.error("I/O error: " + ex.getMessage());
        }

        public void fatalProtocolException(final HttpException ex, final NHttpConnection conn) {
            log.error("HTTP error: " + ex.getMessage());
        }
    }
}

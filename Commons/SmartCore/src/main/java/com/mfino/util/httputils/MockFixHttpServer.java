package com.mfino.util.httputils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;

public class MockFixHttpServer {

    private Thread t;

    private static Logger log = LoggerFactory.getLogger(MockFixHttpServer.class);
    
    public MockFixHttpServer() {
    }

    public void startServer(String hostName, int port, long sleepTime) {

        try {
            t = new RequestListenerThread(port, hostName, sleepTime);
            t.setDaemon(false);
            t.start();
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
    }

    public void stopServer() {
        try {
            t.interrupt();
            Thread.sleep(2000);
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        } finally {
            t = null;
        }

    }

    static class ObjectHandler implements HttpRequestHandler {

        public ObjectHandler() {
            super();
        }

        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

            try {

                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                byte[] entityContent = EntityUtils.toByteArray(entity);

                CMultiXBuffer buffers = new CMultiXBuffer();
                buffers.Append(entityContent);

                CFIXMsg retValFixMsg = CFIXMsg.fromFIX(buffers);

                ByteArrayInputStream bis = new ByteArrayInputStream(entityContent);

                BasicHttpEntity ben = new BasicHttpEntity();
                ben.setContent(bis);

                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(ben);


            } catch (Exception error) {
                log.error(error.getMessage(), error);
            }

        }
    }

    static class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;
        private long sleepTime;

        public RequestListenerThread(int port, final String docroot, long sleepTime) throws IOException {
            this.serversocket = new ServerSocket(port);
            this.params = new BasicHttpParams();
            this.sleepTime = sleepTime;
            this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024).setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up the HTTP protocol processor
            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            httpproc.addInterceptor(new ResponseDate());
            httpproc.addInterceptor(new ResponseServer());
            httpproc.addInterceptor(new ResponseContent());
            httpproc.addInterceptor(new ResponseConnControl());

            // Set up request handlers
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            //reqistry.register("*", new HttpFileHandler(docroot));
            reqistry.register("*", new ObjectHandler());


            // Set up the HTTP service
            this.httpService = new HttpService(
                    httpproc,
                    new DefaultConnectionReuseStrategy(),
                    new DefaultHttpResponseFactory());
            this.httpService.setParams(this.params);
            this.httpService.setHandlerResolver(reqistry);
        }

        public void run() {
            log.info("Listening on port " + this.serversocket.getLocalPort());
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    log.info("Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage(), ex);
                    }

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                	log.error("I/O error initialising connection thread: ", ex);
                    break;
                } catch (IOException ex) {
                	log.error("I/O error initialising connection thread: ", ex);
                    break;
                }
            }
        }
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(
                final HttpService httpservice,
                final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        public void run() {
            log.info("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
               log.error("Client closed connection", ex);
            } catch (IOException ex) {
                log.error("I/O error: ", ex);
            } catch (HttpException ex) {
                log.error("Unrecoverable HTTP protocol violation: ", ex);
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                	log.warn("Shutdown error", ignore);
                }
            }
        }
    }
}

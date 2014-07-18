/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Hai Lu luhonghai@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.luhonghai.haminium.remote;

import com.luhonghai.haminium.remote.handler.CommandHandler;
import org.openqa.grid.selenium.GridLauncher;
import org.openqa.jetty.http.HttpContext;
import org.openqa.jetty.http.SocketListener;
import org.openqa.jetty.jetty.Server;
import org.openqa.jetty.jetty.servlet.ServletHandler;
import org.openqa.jetty.util.MultiException;
import java.net.BindException;

/**
 * @author Hai Lu
 */

public class HaminiumServer {

    private Server server;

    private final int port;
    /**
     * This lock is very important to ensure that SeleniumServer and the underlying Jetty instance
     * shuts down properly. It ensures that ProxyHandler does not add an SslRelay to the Jetty server
     * dynamically (needed for SSL proxying) if the server has been shut down or is in the process of
     * getting shut down.
     */
    private final Object shutdownLock = new Object();

    private static final int MAX_SHUTDOWN_RETRIES = 8;

    private Thread shutDownHook;

    public HaminiumServer(int port) {
        this.port = port;
        init();
    }

    protected void init() {
        final SocketListener socketListener;
        server = new Server();
        socketListener = new SocketListener();
        socketListener.setMaxIdleTimeMs(60000);
        socketListener.setPort(port);
        server.addListener(socketListener);

        HttpContext extra = new HttpContext();
        extra.setContextPath("/remote");
        ServletHandler handler = new ServletHandler();
        handler.addServlet("/command/*", CommandHandler.class.getName());
        extra.addHandler(handler);
        server.addContext(extra);
    }

    /**
     * Starts the Jetty server
     *
     * @throws Exception on error.
     */
    public void start() throws Exception {
        System.setProperty("org.openqa.jetty.http.HttpRequest.maxFormContentSize", "0"); // default max
        // is 200k;
        // zero is
        // infinite
        try {
            server.start();
        } catch (MultiException e) {
            if (e.getExceptions().size() == 1 && e.getException(0) instanceof BindException) {
                throw new BindException("Haminium is already running on port " + port +
                        ". Or some other service is.");
            }
            throw e;
        }

        shutDownHook = new Thread(new ShutDownHook(this)); // Thread safety reviewed
        shutDownHook.setName("SeleniumServerShutDownHook");
        Runtime.getRuntime().addShutdownHook(shutDownHook);
    }

    private class ShutDownHook implements Runnable {
        private final HaminiumServer haminium;

        ShutDownHook(HaminiumServer haminium) {
            this.haminium = haminium;
        }

        public void run() {
            haminium.stop();
        }
    }

    /**
     * Stops the Jetty server
     */
    public void stop() {
        int numTries = 0;
        Exception shutDownException = null;

        // this may be called by a shutdown hook, or it may be called at any time
        // in case it was called as an ordinary method, try to clean up the shutdown
        // hook
        try {
            if (shutDownHook != null) {
                Runtime.getRuntime().removeShutdownHook(shutDownHook);
            }
        } catch (IllegalStateException ignored) {
        } // thrown if we're shutting down; that's OK

        // shut down the jetty server (try try again)
        while (numTries <= MAX_SHUTDOWN_RETRIES) {
            ++numTries;
            try {
                // see docs for the lock object for information on this and why it is IMPORTANT!
                synchronized (shutdownLock) {
                    server.stop();
                }

                // If we reached here stop didnt throw an exception.
                // So we assume it was successful.
                break;
            } catch (Exception ex) { // org.openqa.jetty.jetty.Server.stop() throws Exception
                shutDownException = ex;
                // If Exception is thrown we try to stop the jetty server again
            }
        }

        if (numTries > MAX_SHUTDOWN_RETRIES) { // This is bad!! Jetty didnt shutdown..
            if (null != shutDownException) {
                throw new RuntimeException(shutDownException);
            }
        }
    }

    /**
     * Exposes the internal Jetty server used by Selenium. This lets users add their own webapp to the
     * Selenium Server jetty instance. It is also a minor violation of encapsulation principles (what
     * if we stop using Jetty?) but life is too short to worry about such things.
     *
     * @return the internal Jetty server, pre-configured with the /selenium-server context as well as
     *         the proxy server on /
     */
    public Server getServer() {
        return server;
    }

    public static void main(String[] args) {
        try {
            GridLauncher.main(args);
            HaminiumServer server = new HaminiumServer(17388);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

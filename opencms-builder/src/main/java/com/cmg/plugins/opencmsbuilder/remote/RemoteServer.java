/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.remote;

import com.cmg.plugins.opencmsbuilder.remote.hanlder.CommandHandler;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.BindException;

/**
 * @author Hai Lu
 */

public class RemoteServer {

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

    public RemoteServer(int port) {
        this.port = port;
        init();
    }

    protected void init() {
        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setMaxIdleTime(60000);
        server.setConnectors(new Connector[] {connector});
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", true, false);
        servletContextHandler.addServlet(CommandHandler.class, "/command");
    }

    /**
     * Starts the Jetty server
     *
     * @throws Exception on error.
     */
    public void start() throws Exception {
        try {
            Logger.getLogger().info("Start OpenCMS Builder Remote server at port " + port);
            server.start();
            Logger.getLogger().info("Server is running. Waiting for command ...");
            server.join();
        } catch (Exception e) {
            if (e instanceof BindException) {
                throw new BindException("OpenCms Builder is already running on port " + port +
                        ". Or some other service is.");
            }
            throw e;
        }

        shutDownHook = new Thread(new ShutDownHook(this)); // Thread safety reviewed
        shutDownHook.setName("SeleniumServerShutDownHook");
        Runtime.getRuntime().addShutdownHook(shutDownHook);
    }

    private class ShutDownHook implements Runnable {
        private final RemoteServer haminium;

        ShutDownHook(RemoteServer haminium) {
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
                    Logger.getLogger().info("Try to stop server");
                    server.stop();
                    Logger.getLogger().info("Server stopped.");
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
        RemoteServer server = new RemoteServer(8888);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

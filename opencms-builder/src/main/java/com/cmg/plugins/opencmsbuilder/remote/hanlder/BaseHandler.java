/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.remote.hanlder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * @author Hai Lu
 */
public abstract class BaseHandler extends HttpServlet {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    private PrintWriter out;

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        execute(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        execute(request, response);
    }

    private void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
        out = response.getWriter();
        process(request, response);
    }

    protected abstract void process(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException;

    protected String getParameter(HttpServletRequest req, String parameter,
                                  String defaultValue) {
        String value = req.getParameter(parameter);
        if (isEmptyOrNull(value)) {
            value = defaultValue;
        }
        return value.trim();
    }

    protected boolean isEmptyOrNull(String value) {
        return value == null || value.trim().length() == 0;
    }

    protected void println(String message) {
        if (out != null) {
            logger.info(message);
            out.println(message);
        }
    }

    protected void print(String message) {
        if (out != null) {
            logger.info(message);
            out.print(message);
        }
    }
}

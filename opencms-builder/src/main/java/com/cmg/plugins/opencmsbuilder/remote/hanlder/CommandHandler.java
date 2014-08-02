/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.remote.hanlder;

import com.cmg.plugins.opencmsbuilder.command.ServiceCommand;
import com.cmg.plugins.opencmsbuilder.helper.CmsShellConnector;
import com.cmg.plugins.opencmsbuilder.helper.WindowsService;
import com.cmg.plugins.opencmsbuilder.remote.CommandResponse;
import com.cmg.plugins.opencmsbuilder.remote.RemoteCommand;
import com.cmg.plugins.opencmsbuilder.remote.RemotePackage;
import com.google.gson.Gson;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Lu
 */
public class CommandHandler extends BaseHandler {

    @Override
    protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = getParameter(request, "data", "");
        long start = System.currentTimeMillis();
        CommandResponse cr = new CommandResponse();
        Gson gson = new Gson();
        CmsShellConnector connector = null;
        try {
            if (data.length() == 0) {
                cr.setType(CommandResponse.TYPE_ERROR);
                cr.setMessage("Missing data parameter");
            } else {
                StringBuffer sb = new StringBuffer();
                RemotePackage remotePackage = gson.fromJson(data, RemotePackage.class);
                RemoteCommand[] commands = remotePackage.getRemoteCommands();
                if (commands != null && commands.length > 0) {
                    if (remotePackage.getConfiguration() != null) {
                        connector = new CmsShellConnector(remotePackage.getConfiguration());
                    }
                    for (RemoteCommand command: commands) {
                        switch (command.getType()) {
                            case RemoteCommand.WINDOWS_SERVICE:
                                String[] parameters = command.getParameters();
                                if (parameters.length > 0) {
                                    String serviceName = parameters[0];
                                    WindowsService windowsService = new WindowsService(serviceName);
                                    boolean status = false;
                                    if (command.getName().equals(ServiceCommand.START)) {
                                        sb.append("Start service " + serviceName);
                                        status = windowsService.start();
                                    } else if (command.getName().equals(ServiceCommand.STOP)) {
                                        sb.append("Stop service " + serviceName);
                                        status = windowsService.stop();
                                    } else if (command.getName().equals(ServiceCommand.RESTART)) {
                                        sb.append("Restart service " + serviceName);
                                        status = windowsService.restart();
                                    }
                                    sb.append(status ? " success": " failure");
                                }
                                break;
                            case RemoteCommand.CMS_SHELL:
                                if (connector != null) {
                                    sb.append(connector.executeCommand(command)).append("\n");
                                }
                            default:
                                break;
                        }
                    }
                    cr.setType(CommandResponse.TYPE_SUCCESS);
                    cr.setData(sb.toString());
                    cr.setMessage("Execution success");
                } else {
                    cr.setType(CommandResponse.TYPE_ERROR);
                    cr.setMessage("No commands found");
                }
            }
        } catch (Exception e) {
            cr.setType(CommandResponse.TYPE_ERROR);
            cr.setMessage(ExceptionUtils.getStackTrace(e));
        } finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {

                }
            }
            long end = System.currentTimeMillis();
            cr.setExecutionTime(end - start);
            print(gson.toJson(cr));
        }
    }
}

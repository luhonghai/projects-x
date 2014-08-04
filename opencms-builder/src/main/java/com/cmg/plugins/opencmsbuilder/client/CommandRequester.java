/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.client;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;
import com.cmg.plugins.opencmsbuilder.remote.CommandResponse;
import com.cmg.plugins.opencmsbuilder.remote.RemoteCommand;
import com.cmg.plugins.opencmsbuilder.remote.RemotePackage;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hai Lu
 */
public class CommandRequester {

    private final ApplicationConfiguration configuration;
    private List<RemoteCommand> commands;

    public CommandRequester(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    public void addCommand(RemoteCommand command) {
        if (commands == null) {
            commands = new ArrayList<RemoteCommand>();
        }
        commands.add(command);
    }

    public void execute() throws IOException, MojoExecutionException {
        String url = configuration.getRemoteServer() + "/command";
        Logger.getLogger().info("Request command url " + url);
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(configuration.getRemoteServer() + "/command");
        Gson gson = new Gson();
        RemotePackage remotePackage = new RemotePackage();
        remotePackage.setConfiguration(configuration);
        RemoteCommand[] listRemoteCommands = new RemoteCommand[commands.size()];
        commands.toArray(listRemoteCommands);
        remotePackage.setRemoteCommands(listRemoteCommands);


        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("data", gson.toJson(remotePackage)));

        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        Logger.getLogger().info("Execute request ...");
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String data = writer.toString();
            Logger.getLogger().info("Receive response ... " + data);
            CommandResponse cr = gson.fromJson(data, CommandResponse.class);
            Logger.getLogger().info("Execution time: " + cr.getExecutionTime() + "ms");
            if (cr.getType().equals(CommandResponse.TYPE_ERROR)) {
                throw  new MojoExecutionException("An error occurred. Message: " + cr.getMessage());
            } else if (cr.getType().equals(CommandResponse.TYPE_SUCCESS)) {
                Logger.getLogger().info(cr.getData());
                Logger.getLogger().info("Execution completed");
            } else {
                Logger.getLogger().error("Unknown type: " + cr.getType());
            }
        }
    }
}

package com.cmg.maven.plugins.cmgium;

import com.cmg.maven.plugins.cmgium.utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hai Lu on 26/05/2014.
 */
public class CommandMapping {
    public static final String SPLIT_LEVEL_0 = "=====";
    public static final String SPLIT_LEVEL_1 = "===";

    private String command;
    private String template;

    private static Map<String,CommandMapping> commandMappings;

    public static Map<String,CommandMapping> getCommandMappings() throws IOException {
        if (commandMappings == null) {
            commandMappings = new HashMap<String, CommandMapping>();
            String source = FileUtils.readResourceContent("command-mapping");
            String[] cm1 = source.split(SPLIT_LEVEL_0);
            for (int i = 0; i < cm1.length; i++) {
                String tmp = cm1[i];
                if (tmp.contains(SPLIT_LEVEL_1)) {
                    String[] cm2 = cm1[i].split(SPLIT_LEVEL_1);
                    CommandMapping cm = new CommandMapping();
                    cm.setCommand(cm2[0].trim());
                    cm.setTemplate(cm2[1].trim());
                    commandMappings.put(cm.getCommand(), cm);
                }
            }
        }
        return commandMappings;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}

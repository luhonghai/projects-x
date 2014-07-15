/** The MIT License (MIT)
 *
 *   Copyright (c) 2004 Hai Lu luhonghai@gmail.com
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package com.luhonghai.maven.plugins.haminium.old;

import com.luhonghai.maven.plugins.haminium.utils.FileUtils;

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

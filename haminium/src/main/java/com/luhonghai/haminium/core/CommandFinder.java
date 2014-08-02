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

package com.luhonghai.haminium.core;

import com.luhonghai.haminium.annotation.Function;
import com.luhonghai.haminium.ide.docs.ApiDoc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Hai Lu
 */
public class CommandFinder {
    private static final Logger LOGGER = Logger.getLogger(CommandFinder.class.getName());
    private Map<String, Command> commands;
    private ApiDoc docs;

    public List<Command> fromClass(String className, boolean isCustom) throws ClassNotFoundException {
        return fromClass(Class.forName(className), isCustom);
    }

    public List<Command> fromClass(Class clazz, boolean isCustom) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Command> commands = new ArrayList<Command>();
        if (methods.length > 0) {
            for (Method m : methods) {
                if (isCustom) {
                    Function function = m.getAnnotation(Function.class);
                    if (function != null) {
                        LOGGER.info("Detect Function annotation method " + m.getName() + "");
                        addDocs(function);
                        addCommand(m);
                    } else {
                        LOGGER.info("Skip method " + m.getName());
                    }
                } else {
                    LOGGER.info("Add method " + m.getName());
                    addCommand(m);
                }
            }
        }
        return commands;
    }

    private void addDocs(Function f) {

    }

    private void addCommand(Method m) {

    }

    public static class Command {
        public static enum TYPE {
            _DO,
            _IS,
            _GET
        };

        TYPE type;

        String name;

        String className;

        List<String> params;
    }
}

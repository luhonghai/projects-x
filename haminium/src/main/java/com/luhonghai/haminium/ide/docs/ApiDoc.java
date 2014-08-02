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

package com.luhonghai.haminium.ide.docs;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hai Lu
 */
@XmlRootElement(name = "apidoc")
public class ApiDoc {

    @XmlElement(name="function")
    private List<Function> functions;

    public List<Function> getFunctions() {
        if (functions == null) {
            functions = new ArrayList<Function>();
        }
        return functions;
    }

    public Function newFunction(String name) {
        final Function f = new Function();
        f.name = name;
        final List<Function> functions = getFunctions();
        if (functions.contains(f)) {
            functions.remove(f);
        }
        functions.add(f);
        return f;
    }

    public void toXml(File output) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        //jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        jaxbMarshaller.setProperty(CharacterEscapeHandler.class.getName(),
                new CharacterEscapeHandler() {
                    public void escape(char[] ac, int i, int j, boolean flag,
                                       Writer writer) throws IOException {
                        writer.write(ac, i, j);
                    }
                });
        jaxbMarshaller.marshal(this, output);
    }

    public static ApiDoc fromXml(File input) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ApiDoc.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (ApiDoc) jaxbUnmarshaller.unmarshal(input);
    }

    public static class CustomAdapter extends XmlAdapter<String, String> {

        @Override
        public String unmarshal(String v) throws Exception {
            return v;
        }

        @Override
        public String marshal(String v) throws Exception {
            return "<![CDATA[" + v + "]]>";
        }
    }

    @XmlRootElement
    @XmlType(propOrder = {"parameters", "comment"})
    public static class Function {

        private String name;

        private List<Parameter> parameters;

        private String comment;

        @Override
        public boolean equals(Object obj) {
            // Make the equals simply with just compare name
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            } else {
                return this.getName().equals(((Function) obj).getName());
            }
        }

        public Parameter newParameter() {
            final Parameter p = new Parameter();
            getParameters().add(p);
            return p;
        }

        @XmlAttribute
        public String getName() {
            return this.name;
        }

        @XmlElement(name = "param")
        public List<Parameter> getParameters() {
            if (parameters == null) {
                parameters = new ArrayList<Parameter>();
            }
            return parameters;
        }

        @XmlElement(name = "comment")
        @XmlJavaTypeAdapter(CustomAdapter.class)
        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @XmlRootElement
        public static class Parameter {

            private String name;

            private String comment;

            @XmlValue
            @XmlJavaTypeAdapter(CustomAdapter.class)
            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            @XmlAttribute
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}

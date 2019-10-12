package ru.javaops.masterjava.xml.util;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class XsltProcessor {
    private static TransformerFactory FACTORY = TransformerFactory.newInstance();
    private final Transformer transformer;

    public XsltProcessor(InputStream in) {
        this(new BufferedReader(new InputStreamReader(in)));
    }

    public XsltProcessor(Reader reader) {
        try {
            var template = FACTORY.newTemplates(new StreamSource(reader));
            transformer = template.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("XSLT transformer creation error: " + e.getMessage(), e);
        }
    }

    public String transform(InputStream in) throws TransformerException {
        var buffer = new StringWriter();
        transform(in, buffer);
        return buffer.toString();

    }

    public void transform(InputStream sourceInput, Writer resultWriter) throws TransformerException {
        transform(new BufferedReader(new InputStreamReader(sourceInput)), resultWriter);
    }

    public void transform(Reader sourceReader, Writer resultWriter) throws TransformerException {
        transformer.transform(new StreamSource(sourceReader), new StreamResult(resultWriter));
    }

    public static String getXsltHeader(String xslt) {
        return "<?xml-stylesheet type=\"text/xsl\" href=\"" + xslt + "\"?>\n";
    }

    public void setParameter(String name, String value) {
        transformer.setParameter(name, value);
    }
}
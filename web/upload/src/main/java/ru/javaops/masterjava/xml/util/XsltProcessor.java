package ru.javaops.masterjava.xml.util;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class XsltProcessor {
    private static TransformerFactory FACTORY = TransformerFactory.newInstance();
    private final Transformer transformer;

    public XsltProcessor(InputStream in) {
        this(new BufferedReader(new InputStreamReader(in)));
    }

    public XsltProcessor(Reader reader) {
        try {
            transformer = FACTORY.newTemplates(new StreamSource(reader)).newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("XSLT transformer creation failed: " + e.toString(), e);
        }
    }

    public String transform(InputStream in) throws TransformerException {
        StringWriter out = new StringWriter();
        transform(in, out);
        return out.toString();

    }

    public void transform(InputStream in, Writer result) throws TransformerException {
        transform(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)), result);
    }

    public void transform(Reader sourceReader, Writer result) throws TransformerException {
        transformer.transform(new StreamSource(sourceReader), new StreamResult(result));
    }

    public static String getXsltHeader(String xslt) {
        return "<?xml-stylesheet type=\"text/xsl\" href=\"" + xslt + "\"?>\n";
    }

    public void setParameter(String name, String value) {
        transformer.setParameter(name, value);
    }
}

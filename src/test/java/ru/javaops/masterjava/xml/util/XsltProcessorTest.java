package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.InputStream;

public class XsltProcessorTest {
    @Test
    public void transform() throws Exception {
        try (InputStream xslIn = Resources.getResource("cities.xsl").openStream();
             InputStream xmlIn = Resources.getResource("payload.xml").openStream()
        ) {
            XsltProcessor processor = new XsltProcessor(xslIn);
            System.out.println(processor.transform(xmlIn));
        }
    }

}
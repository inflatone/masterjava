package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.InputStream;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class XPathProcessorTest {
    @Test
    public void getCities() throws Exception {
        try (InputStream in = Resources.getResource("payload.xml").openStream()) {
            XPathProcessor processor = new XPathProcessor(in);
            XPathExpression expression
                    = XPathProcessor.getExpression("/*[name()='Payload']/*[name()='Cities']/*[name()='City']/text()");
            NodeList nodes = processor.evaluate(expression, XPathConstants.NODESET);
            IntStream.range(0, nodes.getLength()).forEach(
                    i -> System.out.println(nodes.item(i).getNodeValue())
            );
        }
    }

}